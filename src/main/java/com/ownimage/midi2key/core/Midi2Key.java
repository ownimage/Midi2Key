package com.ownimage.midi2key.core;

import com.ownimage.midi2key.adapter.KeyboardAdapter;
import com.ownimage.midi2key.adapter.MidiAdapter;
import com.ownimage.midi2key.menu.MenuInputProvider;
import com.ownimage.midi2key.menu.MenuMain;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import com.ownimage.midi2key.util.WaitForNextValue;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Midi2Key implements MidiActionReceiver, KeyboardActionReceiver, MenuInputProvider, ConfigChanger {

    private Config config = Config.builder().build();
    private MidiAdapter midiAdapter = new MidiAdapter(this, this, true);
    private KeyboardAdapter keyboardAdapter = new KeyboardAdapter(this, true);

    private WaitForNextValue<KeyboardAction> lastKeyboardAction = new WaitForNextValue<>();
    private WaitForNextValue<MidiAction> lastMidiAction = new WaitForNextValue<>();

    private boolean mapMidiEvents = true;

    private Midi2Key() {
    }

    public Midi2Key(MidiAdapter midiAdapter) {
        this.midiAdapter = midiAdapter;
    }

    public static void main(String[] args) {
        var midi2key = new Midi2Key();
        midi2key.start();
        System.out.println("end of menu");
        midi2key.stop();
    }

    @Override
    public void receive(@NotNull MidiAction midiAction) {
//        System.out.println("####### " + midiAction);
        lastMidiAction.value(midiAction);
        if (mapMidiEvents) config.map(midiAction).ifPresent(ka -> keyboardAdapter.sendKeyboardAction(ka));
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
        //System.out.println("Midi2Key receive: " + keyboardAction);
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
        System.out.println("Midi2Key::saveConfig not yet implemented");
        System.out.println("config = " + config.toJson(false));
        config.save(new File(Config.DEFAULT_FILENAME), true);
    }

    @Override
    public void stopMapping() {
        mapMidiEvents = false;
    }

    @Override
    public void startMapping() {
        mapMidiEvents = true;
    }

    @Override
    @SneakyThrows
    public void openConfig() {
        var c = config.open(new File(Config.DEFAULT_FILENAME));
        config(c);
    }
}
