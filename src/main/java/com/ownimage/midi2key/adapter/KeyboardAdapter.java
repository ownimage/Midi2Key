package com.ownimage.midi2key.adapter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.ownimage.midi2key.core.KeyboardActionReceiver;
import com.ownimage.midi2key.model.KeyboardAction;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*;

public class KeyboardAdapter implements NativeKeyListener {

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
        var keyChar = e.getKeyCode();
        if (keyChar == VC_SHIFT || keyChar == VC_ALT || keyChar == VC_CONTROL) return;

        var shift = (e.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0;
        var ctrl = (e.getModifiers() & NativeInputEvent.CTRL_MASK) != 0;
        var alt = (e.getModifiers() & NativeInputEvent.ALT_MASK) != 0;
        var keyboardStroke = new KeyboardAction(ctrl, alt, shift, keyChar);
//        System.out.println("Key Pressed: id=" + e.getID() + "\tmodifiers=" + e.getModifiers() + "\trawCode=" + e.getRawCode() + "\tkeyCode=" + e.getKeyCode() + "\tkeyChar=" + (int) e.getKeyChar() + "\tKeyLocation=" + e.getKeyLocation());
        keyboardActionReceiver.receive(keyboardStroke);

        if (e.getRawCode() == 65) { // press A
            var thread = new Thread(() -> {
                synchronized (this) {
                    try {
                        wait(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                    var keySHIFTdown = new NativeKeyEvent(2401, 0, 0, 42, '\uFFFF', 2);
                    var keyBdown = new NativeKeyEvent(2401, 0, 0, 48, '\uFFFF', 1);
                    var keyBup = new NativeKeyEvent(2402, 0, 0, 48, '\uFFFF', 1);
                    var keySHIFTup = new NativeKeyEvent(2402, 0, 0, 42, '\uFFFF', 2);

//                    GlobalScreen.postNativeEvent(keySHIFTdown);
//                    GlobalScreen.postNativeEvent(keyBdown);
//                    GlobalScreen.postNativeEvent(keyBup);
//                    GlobalScreen.postNativeEvent(keySHIFTup);
//                    System.out.println("PRINTED");
                }
            });
            thread.start();
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
//        System.out.println("Key Released: id=" + e.getID() + "\tmodifiers=" + e.getModifiers() + "\trawCode=" + e.getRawCode() + "\tkeyCode=" + e.getKeyCode() + "\tkeyChar=" + (int)e.getKeyChar() + "\tKeyLocation=" + e.getKeyLocation());
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
//        System.out.println("Key Typed: id=" + e.getID() + "\tmodifiers=" + e.getModifiers() + "\trawCode=" + e.getRawCode() + "\tkeyCode=" + e.getKeyCode() + "\tkeyChar=" + (int)e.getKeyChar() + "\tKeyLocation=" + e.getKeyLocation());
    }
}