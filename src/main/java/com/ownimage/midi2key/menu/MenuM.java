package com.ownimage.midi2key.menu;

public class MenuM extends AbstractMenu {

    public MenuM(MenuInputProvider menuInputProvider) {
        super(menuInputProvider);
    }

    @Override
    protected String getPrompt() {
        return "Mapping menu\n" +
                "Create MIDI event";
    }

    public void run() {
        printPrompt();
    }
}
