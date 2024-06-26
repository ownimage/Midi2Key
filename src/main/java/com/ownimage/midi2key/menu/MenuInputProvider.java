package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;

public interface MenuInputProvider {

    KeyboardAction getKeyboardAction();

    MidiAction getMidiAction();
}
