package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.jetbrains.annotations.NotNull;

public class MenuOpenConfig extends AbstractMenu {

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
        System.out.println("Config Opened");
        printSeparator();
    }
}
