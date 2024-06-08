package com.ownimage.midi2key.core;

import com.ownimage.midi2key.adapter.KeyboardAdapter;
import com.ownimage.midi2key.adapter.MidiAdapter;
import com.ownimage.midi2key.menu.MenuInputProvider;
import com.ownimage.midi2key.menu.MenuMain;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import com.ownimage.midi2key.util.WaitForNextValue;
import lombok.SneakyThrows;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Midi2Key implements MidiActionReceiver, KeyboardActionReceiver, MenuInputProvider, ConfigChanger {

    private static final Logger logger = Logger.getLogger(Midi2Key.class);
    private static final String USAGE = "Usage java Midi2Key RUN|EDIT config.file";

    private Config config = Config.builder().build();
    private MidiAdapter midiAdapter = new MidiAdapter(this, true);
    private final KeyboardAdapter keyboardAdapter;

    private final WaitForNextValue<KeyboardAction> lastKeyboardAction = new WaitForNextValue<>();
    private final WaitForNextValue<MidiAction> lastMidiAction = new WaitForNextValue<>();

    private boolean mapMidiEvents = true;
    private final String action;

    private Midi2Key(String[] args) {
        if (args.length != 2) {
            System.out.println("Must supply 2 arguments. " + USAGE);
            System.exit(1);
        }
        action = args[0];
        if (!"RUN".equals(action) && !"EDIT".equals(action)) {
            System.out.println("Must supply valid action. " + USAGE);
            System.exit(1);
        }

        keyboardAdapter = new KeyboardAdapter(this, "EDIT".equals(action));

        var filename = (args == null || args.length == 1) ? "config.json" : args[1];
        config = config.withFilename(filename).open();
        logger.setLevel(Level.INFO);
        logger.debug("Filename: " + config.getConfigFile().getAbsolutePath());
    }

    public static void main(String[] args) {
        var midi2key = new Midi2Key(args);
        midi2key.start();
        logger.debug("end of menu");
        midi2key.stop();
    }

    @Override
    public void receive(boolean rotary, @NotNull MidiAction midiAction) {
        logger.debug("Midi2Key::recieve: rotary=" + rotary + " control=" + midiAction.control() + "->" + config().getLabel(midiAction) + " midiAction=" + midiAction);
        if (mapMidiEvents) {
            lastMidiAction.value(midiAction);
            logger.debug(String.format("Midi2Key::receive %s", midiAction));
            config.map(midiAction).ifPresent(ka -> {
                logger.debug(String.format("Midi2Key::receive %s %s %s", rotary, midiAction, ka));
                keyboardAdapter.sendKeyboardAction(rotary, midiAction.action(), ka);
            });
        }
    }

    @SneakyThrows
    private synchronized void start() {
        if ("EDIT".equals(action)) {
            var menu = new MenuMain(this, this);
            menu.run();
        }
        else {
            Thread.sleep(Long.MAX_VALUE);
        }
    }

    private void stop() {
        keyboardAdapter.stop();
        midiAdapter.stop();
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public void receive(@NotNull KeyboardAction keyboardAction) {
        logger.debug("Midi2Key receive: " + keyboardAction);
        lastKeyboardAction.value(keyboardAction);
    }

    @Override
    public KeyboardAction getKeyboardAction() {
        return lastKeyboardAction.nextValue();
    }

    @Override
    public MidiAction getMidiAction() {
        return lastMidiAction.nextValue();
    }

    @Override
    public void config(@NotNull Config config) {
        this.config = config;
    }

    @Override
    public void saveConfig() {
        logger.debug("config = " + config.toJson(false));
        config.save(true);
        logger.debug("Config saved");
    }

    @Override
    public synchronized void stopMapping() {
        mapMidiEvents = false;
    }

    @Override
    public synchronized void startMapping() {
        mapMidiEvents = true;
    }

    @Override
    @SneakyThrows
    public void openConfig() {
        var c = config.open();
        config(c);
    }

    @Override
    public void printConfig() {
        config.printConfig();
    }
}
