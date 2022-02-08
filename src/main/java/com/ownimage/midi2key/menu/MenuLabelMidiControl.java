package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class MenuLabelMidiControl extends AbstractMenu {

    public MenuLabelMidiControl(
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
        System.out.println("Enter Label, x leave unchanged");
        var label = new Scanner(System.in).next();
        if (StringUtils.isNotEmpty(label) && !"x".equals(label)) {
            var config = configChanger.config().addLabel(midiAction, label);
            configChanger.config(config);
            System.out.println("MIDI Label added");
        }
    }
}
