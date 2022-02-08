package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.VC_X;

public class AbstractMenu {

    private static String SEPARATOR = "##################################################################";

    protected MenuInputProvider menuInputProvider;
    protected ConfigChanger configChanger;
    private HashMap<Integer, AbstractMenu> menuMap = new HashMap<>();
    private Integer exitKeyCode = VC_X;

    public AbstractMenu(
            @NotNull MenuInputProvider menuInputProvider,
            @NotNull ConfigChanger configChanger
    ) {
        this.menuInputProvider = menuInputProvider;
        this.configChanger = configChanger;
    }

    protected MidiAction getMidiAction() {
        var midiAction = menuInputProvider.getMidiAction();
        System.out.println("Midi Input:" + midiAction.control() + "->" + configChanger.config().getLabel(midiAction));
        return midiAction;
    }

    protected KeyboardAction getKeyboardAction() {
        return menuInputProvider.getKeyboardAction();
    }

    protected String getPrompt() {
        return "";
    }

    protected void printPrompt(boolean addExitThisMenuOption) {
        printSeparator();
        System.out.print(getPrompt());
        if (addExitThisMenuOption) System.out.println("X - Exit this menu");
    }

    protected void printSeparator() {
        System.out.println(SEPARATOR);
    }

    public void run() {
        Integer keyCode;
        do {
            printPrompt(true);
            keyCode = getKeyboardAction().getKeyCode();
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
