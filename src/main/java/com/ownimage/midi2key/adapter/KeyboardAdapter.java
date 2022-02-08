package com.ownimage.midi2key.adapter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.ownimage.midi2key.core.Config;
import com.ownimage.midi2key.core.KeyboardActionReceiver;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*;
import static com.ownimage.midi2key.model.MidiAction.Action.DOWN;
import static com.ownimage.midi2key.model.MidiAction.Action.UP;

public class KeyboardAdapter implements NativeKeyListener {

    private static Logger logger = Logger.getLogger(KeyboardAdapter.class);
    
    private static NativeKeyEvent CONTROL_KEY_DOWN = new NativeKeyEvent(2401, 0, 0, VC_CONTROL, '\uFFFF', 2);
    private static NativeKeyEvent ALT_KEY_DOWN = new NativeKeyEvent(2401, 0, 0, VC_ALT, '\uFFFF', 2);
    private static NativeKeyEvent SHIFT_KEY_DOWN = new NativeKeyEvent(2401, 0, 0, VC_SHIFT, '\uFFFF', 2);
    private static NativeKeyEvent CONTROL_KEY_UP = new NativeKeyEvent(2402, 0, 0, VC_CONTROL, '\uFFFF', 2);
    private static NativeKeyEvent ALT_KEY_UP = new NativeKeyEvent(2402, 0, 0, VC_ALT, '\uFFFF', 2);
    private static NativeKeyEvent SHIFT_KEY_UP = new NativeKeyEvent(2402, 0, 0, VC_SHIFT, '\uFFFF', 2);
    KeyboardActionReceiver keyboardActionReceiver;


    // the false option allows for reliable testing without other events arriving
    public KeyboardAdapter(KeyboardActionReceiver keyboardActionReceiver, boolean start) {
        this.keyboardActionReceiver = keyboardActionReceiver;
        if (start) start();
    }

    private void start() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        var keyCode = e.getKeyCode();
        if (keyCode == VC_SHIFT || keyCode == VC_ALT || keyCode == VC_CONTROL) return;

        var shift = (e.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0;
        var ctrl = (e.getModifiers() & NativeInputEvent.CTRL_MASK) != 0;
        var alt = (e.getModifiers() & NativeInputEvent.ALT_MASK) != 0;
        var keyboardStroke = new KeyboardAction(ctrl, alt, shift, keyCode, NativeKeyEvent.getKeyText(keyCode));
        logger.debug("Key " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        logger.debug("Key Pressed: id=" + e.getID() + "\tmodifiers=" + e.getModifiers() + "\trawCode=" + e.getRawCode() + "\tkeyCode=" + e.getKeyCode() + "\tkeyChar=" + (int) e.getKeyChar() + "\tKeyLocation=" + e.getKeyLocation());
        keyboardActionReceiver.receive(keyboardStroke);
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        logger.debug("Key Released: id=" + e.getID() + "\tmodifiers=" + e.getModifiers() + "\trawCode=" + e.getRawCode() + "\tkeyCode=" + e.getKeyCode() + "\tkeyChar=" + (int)e.getKeyChar() + "\tKeyLocation=" + e.getKeyLocation());
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        logger.debug("Key Typed: id=" + e.getID() + "\tmodifiers=" + e.getModifiers() + "\trawCode=" + e.getRawCode() + "\tkeyCode=" + e.getKeyCode() + "\tkeyChar=" + (int)e.getKeyChar() + "\tKeyLocation=" + e.getKeyLocation());
    }

    public void sendKeyboardAction(boolean rotary, MidiAction.Action action, @NotNull KeyboardAction keyboardAction) {
        logger.debug(String.format("KeyboardAction::sendKeyboardAction rotary= %s, action=%s, keyboardAction=%s ", rotary, action, keyboardAction));
        var keyDown = new NativeKeyEvent(2401, 0, 0, keyboardAction.getKeyCode(), '\uFFFF', 2);
        var keyUp = new NativeKeyEvent(2402, 0, 0, keyboardAction.getKeyCode(), '\uFFFF', 2);

        if (rotary || action == DOWN) {
            if (keyboardAction.isCtrl()) GlobalScreen.postNativeEvent(CONTROL_KEY_DOWN);
            if (keyboardAction.isShift()) GlobalScreen.postNativeEvent(SHIFT_KEY_DOWN);
            if (keyboardAction.isAlt()) GlobalScreen.postNativeEvent(ALT_KEY_DOWN);
            GlobalScreen.postNativeEvent(keyDown);
        }
        if (rotary || action == UP) {
            GlobalScreen.postNativeEvent(keyUp);
            if (keyboardAction.isAlt()) GlobalScreen.postNativeEvent(ALT_KEY_UP);
            if (keyboardAction.isShift()) GlobalScreen.postNativeEvent(SHIFT_KEY_UP);
            if (keyboardAction.isCtrl()) GlobalScreen.postNativeEvent(CONTROL_KEY_UP);
        }
    }

    @SneakyThrows
    public void stop() {
        GlobalScreen.unregisterNativeHook();
    }
}