package com.ownimage.midi2key.model;

import lombok.*;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = true)
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class MidiAction {

    public enum Action {
        UP, DOWN
    }

    public static final int ROTARY_MAX = 127;

    private final int control;
    private final boolean rotary;
    private final Action action;

    public String getKey() {
        return rotary()
                ? control + "R-" + action
                : String.valueOf(control);
    }
}
