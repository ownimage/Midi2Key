package com.ownimage.midi2key.core;

import com.ownimage.midi2key.adapter.KeyboardAdapter;
import com.ownimage.midi2key.adapter.MidiAdapter;
import com.ownimage.midi2key.menu.MenuInputProvider;
import com.ownimage.midi2key.menu.MenuMain;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;

public class Midi2Key implements MidiActionReceiver, KeyboardActionReceiver, MenuInputProvider, ConfigSuppier {

    private Config config = Config.builder().build();
    private MidiAdapter midiAdapter = new MidiAdapter(this, this, true);
    private KeyboardAdapter keyboardAdapter = new KeyboardAdapter(this, true);

    private KeyboardAction lastKeyboardAction;
    private Object keyboardActionLock = new Object();

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
        var menu = new MenuMain(this);
        menu.run();
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public void receive(KeyboardAction keyboardAction) {
        System.out.println("Midi2Key receive: " + keyboardAction);
        lastKeyboardAction = keyboardAction;
        synchronized (keyboardActionLock) {
            keyboardActionLock.notify();
        }
    }

    @Override
    public KeyboardAction getKeyboardAction() {
        KeyboardAction result = null;
        do {
            try {
                lastKeyboardAction = null;
                synchronized (keyboardActionLock) {
                    keyboardActionLock.wait();
                }
                result = lastKeyboardAction;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (result == null);
        return result;
    }

    @Override
    public MidiAction getMidiAction() {
        return null;
    }
}
