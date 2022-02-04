package com.ownimage.midi2key.menu;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.VC_M;
import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.VC_X;

public class MenuMain extends AbstractMenu {

    public MenuMain(MenuInputProvider menuInputProvider) {
        super(menuInputProvider);
        addMenuMap(VC_M, new MenuM(menuInputProvider));
    }

    @Override
    protected String getPrompt() {
        return "Main menu\n" +
                "S - Config Save\n" +
                "M - Map\n" +
                "X - Exit";
    }


}
