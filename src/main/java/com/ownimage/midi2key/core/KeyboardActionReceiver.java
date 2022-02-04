package com.ownimage.midi2key.core;

import com.ownimage.midi2key.model.KeyboardAction;

public interface KeyboardActionReceiver {

    void receive(KeyboardAction keyboardAction);
}
