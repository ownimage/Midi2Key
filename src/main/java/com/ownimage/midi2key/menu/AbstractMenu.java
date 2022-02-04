package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.VC_X;

public class AbstractMenu {

    private static String SEPARATOR = "##################################################################";

    private MenuInputProvider menuInputProvider;
    private HashMap<Integer, AbstractMenu> menuMap = new HashMap<>();
    private Integer exitKeyCode = VC_X;

    public AbstractMenu(MenuInputProvider menuInputProvider) {
        this.menuInputProvider = menuInputProvider;
    }

    protected MidiAction getMidiAction() {
        return menuInputProvider.getMidiAction();
    }

    protected KeyboardAction getKeyboardAction() {
        return menuInputProvider.getKeyboardAction();
    }

    protected String getPrompt() {
        return "";
    }

    protected void printPrompt() {
        System.out.println(SEPARATOR);
        System.out.println(getPrompt());
    }

    public void run(){
        Integer keyCode;
        do {
            printPrompt();
            keyCode = getKeyboardAction().getKeyCode();
            System.out.println("Main menu:  keyCode " + keyCode);
            menuFromKeyCode(keyCode).ifPresent(m -> m.run());
        } while (keyCode != exitKeyCode);
    }

    protected void addMenuMap(Integer keyCode, AbstractMenu menu) {
        menuMap.put(keyCode, menu);
    }

    protected Optional<AbstractMenu> menuFromKeyCode(Integer keyCode) {
        return Optional.ofNullable(menuMap.get(keyCode));
    }

    protected void setExitKeyCode(@NotNull Integer keyCode) {
        exitKeyCode = keyCode;
    }

}
