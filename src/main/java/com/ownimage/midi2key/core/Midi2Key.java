package com.ownimage.midi2key.core;

import com.ownimage.midi2key.model.RawMidiEvent;

public class Midi2Key implements IRawMidiEventReceiver {

    static {
        KeyboardListener.instance();
    }

    private Config config = Config.builder().build();
    private IRawMidiAdapter midiAdapter= new RawMidiAdapter(this);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world");
        var midi2key = new Midi2Key();
        midi2key.start();
    }

    private Midi2Key() {
    }

    public Midi2Key(IRawMidiAdapter midiAdapter) {
        this.midiAdapter = midiAdapter;
    }

    @Override
    public void receive(RawMidiEvent midiEvent) {
        System.out.println("####### " + midiEvent);
    }

    private synchronized void start() throws InterruptedException {
        midiAdapter.start();
        this.wait();
    }
}
