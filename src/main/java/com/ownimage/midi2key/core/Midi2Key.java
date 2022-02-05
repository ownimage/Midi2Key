package com.ownimage.midi2key.core;

import com.ownimage.midi2key.adapter.KeyboardAdapter;
import com.ownimage.midi2key.adapter.MidiAdapter;
import com.ownimage.midi2key.menu.MenuInputProvider;
import com.ownimage.midi2key.menu.MenuMain;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import com.ownimage.midi2key.util.WaitForNextValue;

import java.util.function.Consumer;

public class Midi2Key implements MidiActionReceiver, KeyboardActionReceiver, MenuInputProvider, ConfigChanger {

    private Config config = Config.builder().build();
    private MidiAdapter midiAdapter = new MidiAdapter(this, this, true);
    private KeyboardAdapter keyboardAdapter = new KeyboardAdapter(this, true);

    private WaitForNextValue<KeyboardAction> lastKeyboardAction = new WaitForNextValue<>();

    private Midi2Key() {
    }

    public Midi2Key(MidiAdapter midiAdapter) {
        this.midiAdapter = midiAdapter;
    }

    public static void main(String[] args) {
        var midi2key = new Midi2Key();
        midi2key.start();
    }

    @Override
    public void receive(MidiAction midiEvent) {
        System.out.println("####### " + midiEvent);
    }

    private synchronized void start() {
        var menu = new MenuMain(this, this);
        menu.run();
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public void receive(KeyboardAction keyboardAction) {
        //sSystem.out.println("Midi2Key receive: " + keyboardAction);
        lastKeyboardAction.value(keyboardAction);
    }

    @Override
    public KeyboardAction getKeyboardAction() {
        return lastKeyboardAction.nextValue();
    }

    @Override
    public MidiAction getMidiAction() {
        return null;
    }

    @Override
    public void config(Config config) {
        // TODO is this used
    }

    @Override
    public void saveConfig() {
        System.out.println("Midi2Key::saveConfig not yet implemented");
    }
}
