package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MenuLabelMidiControl extends AbstractMenu {

    private static Logger logger = Logger.getLogger(MenuLabelMidiControl.class);

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
        try {
            printPrompt(false);
            var midiAction = getMidiAction();
            logger.info("Enter Label, press return to leave unchanged");
            var label = new BufferedReader(new InputStreamReader(System.in)).readLine();
            if (StringUtils.isNotEmpty(label)) {
                var config = configChanger.config().addLabel(midiAction, label);
                configChanger.config(config);
                logger.info("MIDI Label added");
            }

        } catch (Throwable t) {
            logger.info("Error", t);
        }
    }
}
