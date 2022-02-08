package com.ownimage.midi2key.core;

import com.ownimage.midi2key.model.MidiAction;

public interface MidiActionReceiver {

     void receive(boolean rotary, MidiAction midiEvent);
}
