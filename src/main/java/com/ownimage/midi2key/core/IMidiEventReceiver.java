package com.ownimage.midi2key.core;

import com.ownimage.midi2key.model.MidiEvent;

public interface IMidiEventReceiver {

     void receive(MidiEvent midiEvent);
}
