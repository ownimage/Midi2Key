package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.jetbrains.annotations.NotNull;

public class MenuSaveConfig extends AbstractMenu {

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
        System.out.println("Config Saved");
    }
}
