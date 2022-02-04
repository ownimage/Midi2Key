package com.ownimage.midi2key.core;

import com.ownimage.midi2key.adapter.KeyboardAdapter;
import com.ownimage.midi2key.adapter.MidiAdapter;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;

public class Midi2Key implements MidiActionReceiver, KeyboardActionReceiver, ConfigSuppier {

    private Config config = Config.builder().build();
    private MidiAdapter midiAdapter = new MidiAdapter(this, this, true);
    private KeyboardAdapter keyboardAdapter = new KeyboardAdapter(this, true);

    private Midi2Key() {
    }

    public Midi2Key(MidiAdapter midiAdapter) {
        this.midiAdapter = midiAdapter;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world");
        var midi2key = new Midi2Key();
        midi2key.start();
    }

    @Override
    public void receive(MidiAction midiEvent) {
        System.out.println("####### " + midiEvent);
    }

    private synchronized void start() {
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public void receive(KeyboardAction keyboardAction) {
        System.out.println("Midi2Key receive: " + keyboardAction);
    }
}
