package com.ownimage.midi2key.core;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.ownimage.midi2key.model.KeyboardStroke;

public class KeyboardListener implements NativeKeyListener {

    private static KeyboardListener keyboardListener;

    public static KeyboardListener instance() {
        return keyboardListener;
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
//        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        var shift =  (e.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0;
        var ctrl =  (e.getModifiers() & NativeInputEvent.CTRL_MASK) != 0;
        var alt =  (e.getModifiers() & NativeInputEvent.ALT_MASK) != 0;
        var keyChar = e.getKeyCode();
        var keyboardStroke = new KeyboardStroke(ctrl, alt, shift, keyChar);
        if (e.getKeyCode() == NativeKeyEvent.VC_SHIFT || e.getKeyCode() == NativeKeyEvent.VC_CONTROL || e.getKeyCode() == NativeKeyEvent.VC_ALT) {
            return;
        }
        System.out.println("Key Pressed: " + keyboardStroke);
        System.out.println("Key Pressed: " + e.getID() + " " + e.getSource());

        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException nativeHookException) {
                nativeHookException.printStackTrace();
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
//        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
//        System.out.println("Key Released: " + e.getKeyText(e.getKeyCode()) + " " + e.getModifiers());
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
//        System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()) + " " + e.getModifiers());
//        System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()) + " " + e.getModifiers());
    }

    static {
        try {
            keyboardListener = new KeyboardListener();
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(keyboardListener);
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}