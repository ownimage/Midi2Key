package com.ownimage.midi2key.core;

import com.ownimage.midi2key.adapter.KeyboardAdapter;
import com.ownimage.midi2key.adapter.MidiAdapter;
import com.ownimage.midi2key.menu.MenuInputProvider;
import com.ownimage.midi2key.menu.MenuMain;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import com.ownimage.midi2key.util.WaitForNextValue;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Midi2Key implements MidiActionReceiver, KeyboardActionReceiver, MenuInputProvider, ConfigChanger {

    private static Logger logger = Logger.getLogger(Midi2Key.class);

    private Config config = Config.builder().build();
    private MidiAdapter midiAdapter = new MidiAdapter(this, true);
    private KeyboardAdapter keyboardAdapter = new KeyboardAdapter(this, true);

    private WaitForNextValue<KeyboardAction> lastKeyboardAction = new WaitForNextValue<>();
    private WaitForNextValue<MidiAction> lastMidiAction = new WaitForNextValue<>();

    private boolean mapMidiEvents = true;

    private Midi2Key(String[] args) {
        var filename = (args == null || args.length == 0) ? "config.json" : args[0];
        config = config.withFilename(filename).open();
        logger.debug("Filename: " + config.getConfigFile().getAbsolutePath());
    }

    public Midi2Key(MidiAdapter midiAdapter) {
        this.midiAdapter = midiAdapter;
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
        if (mapMidiEvents) {m
            lastMidiAction.value(midiAction);
            logger.debug(String.format("Midi2Key::receive %s", midiAction));
            config.map(midiAction).ifPresent(ka -> {
                logger.debug(String.format("Midi2Key::receive %s %s %s", rotary, midiAction, ka));
                keyboardAdapter.sendKeyboardAction(rotary, midiAction.action(), ka);
            });
        }
    }

    private synchronized void start() {
        var menu = new MenuMain(this, this);
        menu.run();
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
