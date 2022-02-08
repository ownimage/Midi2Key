package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MenuOpenConfig extends AbstractMenu {

    private static Logger logger = Logger.getLogger(MenuOpenConfig.class);

    public MenuOpenConfig(
            @NotNull MenuInputProvider menuInputProvider,
            @NotNull ConfigChanger configChanger
    ) {
        super(menuInputProvider, configChanger);
    }

    @Override
    public void run() {
        printSeparator();
        configChanger.openConfig();
        logger.info("Config Opened");
    }
}
