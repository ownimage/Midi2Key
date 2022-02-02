package com.ownimage.midi2key.core;

import com.ownimage.midi2key.model.MidiEvent;

public class Midi2Key implements IMidiEventReceiver {

    private Config config = Config.builder().build();
    private IMidiAdapter midiAdapter= new MidiAdapter(this);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world");
        var midi2key = new Midi2Key();
        midi2key.start();
    }

    private Midi2Key() {
    }

    public Midi2Key(IMidiAdapter midiAdapter) {
        this.midiAdapter = midiAdapter;
    }

    @Override
    public void receive(MidiEvent midiEvent) {
        System.out.println("####### " + midiEvent);
    }

    private synchronized void start() throws InterruptedException {
        midiAdapter.start();
        this.wait();
    }
}
