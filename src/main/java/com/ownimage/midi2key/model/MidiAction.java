package com.ownimage.midi2key.model;

import lombok.*;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class MidiAction {

    public enum Action {
        UP, DOWN, PRESS
    }

    public static final int ROTARY_MAX = 127;

    private final int control;
    private final Action action;

    public String getKey() {
        return String.format("%s-%s", control, action);
    }
}
