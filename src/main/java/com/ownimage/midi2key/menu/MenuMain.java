package com.ownimage.midi2key.menu;

import com.ownimage.midi2key.core.ConfigChanger;
import org.jetbrains.annotations.NotNull;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*;

public class MenuMain extends AbstractMenu {

    public MenuMain(
            @NotNull MenuInputProvider menuInputProvider,
            @NotNull ConfigChanger configChanger
    ) {
        super(menuInputProvider, configChanger);
        addMenuMap(VC_S, new MenuSaveConfig(menuInputProvider, configChanger));
        addMenuMap(VC_O, new MenuOpenConfig(menuInputProvider, configChanger));
        addMenuMap(VC_P, new MenuPrintConfig(menuInputProvider, configChanger));
        addMenuMap(VC_R, new MenuSetRotary(menuInputProvider, configChanger));
        addMenuMap(VC_M, new MenuMapMIDIEvent(menuInputProvider, configChanger));
        addMenuMap(VC_L, new MenuLabelMidiControl(menuInputProvider, configChanger));
    }

    @Override
    protected String getPrompt() {
        return "Main menu\n" +
                "S - Config Save\n" +
                "O - Config Open\n" +
                "P - Config Print\n" +
                "R - Set MIDI control as Rotary\n" +
                "L - Set MIDI label\n" +
                "M - Map MIDI control to Key\n";
    }


}
