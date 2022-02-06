package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.jetbrains.annotations.NotNull;

public class MenuSetRotary extends AbstractMenu {

    public MenuSetRotary(
            @NotNull MenuInputProvider menuInputProvider,
            @NotNull ConfigChanger configChanger
    ) {
        super(menuInputProvider, configChanger);
    }

    @Override
    protected String getPrompt() {
        return "Set Control as Rotary\n" +
                "Create MIDI event:\n";
    }

    public void run() {
        printPrompt(false);
        var midiAction = menuInputProvider.getMidiAction();
        var config = configChanger.config().addRotaryControl(midiAction);
        configChanger.config(config);
        System.out.println("MIDI Control marked as Rotary");
        printSeparator();
    }
}
