package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MenuMapMIDIEvent extends AbstractMenu {

    private static final Logger logger = Logger.getLogger(MenuMapMIDIEvent.class);

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

    @Override
    @SneakyThrows
    public synchronized void run() {
        try {
            printPrompt(false);
            var midiAction = getMidiAction();
            logger.info("Press Key combo");
            configChanger.stopMapping();
            wait(1000);
            var keyboardAction = menuInputProvider.getKeyboardAction();
            var config = configChanger.config().addMapping(midiAction, keyboardAction);
            configChanger.config(config);
            logger.info("MIDI Action mapped");
        } finally {
            configChanger.startMapping();
        }
    }
}
