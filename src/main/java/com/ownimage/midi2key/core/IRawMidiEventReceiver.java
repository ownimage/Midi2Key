package com.ownimage.midi2key.core;

import com.ownimage.midi2key.model.RawMidiEvent;

public interface IRawMidiEventReceiver {

     void receive(RawMidiEvent midiEvent);
}
