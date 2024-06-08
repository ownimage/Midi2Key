package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MenuSaveConfig extends AbstractMenu {

    private static final Logger logger = Logger.getLogger(MenuSaveConfig.class);

    public MenuSaveConfig(
            @NotNull MenuInputProvider menuInputProvider,
            @NotNull ConfigChanger configChanger
    ) {
        super(menuInputProvider, configChanger);
    }

    @Override
    public void run() {
        printSeparator();
        configChanger.saveConfig();
        logger.info("Config Saved");
    }
}
