package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.jetbrains.annotations.NotNull;

public class MenuMapMIDIEvent extends AbstractMenu {

    public MenuMapMIDIEvent(
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

    public void run() {
        printPrompt();
        var midiAction = menuInputProvider.getMidiAction();
        System.out.println("Press Key combo");
        var keyboardAction = menuInputProvider.getKeyboardAction();
        var config = configChanger.config().addMapping(midiAction, keyboardAction);
        configChanger.config(config);
        System.out.println("MIDI Action mapped");
        printSeparator();
    }
}
