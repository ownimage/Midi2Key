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
        return "Mapping menu\n" +
                "Create MIDI event:\n";
    }

    @Override
    public void run() {
        printPrompt(false);
        var midiAction = getMidiAction();
        var config = configChanger.config().addRotaryControl(midiAction);
        configChanger.config(config);
        System.out.println("MIDI control marked as Rotary");
    }
}
