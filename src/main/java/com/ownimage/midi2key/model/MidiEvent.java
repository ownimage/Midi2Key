package com.ownimage.midi2key.model;

import lombok.*;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class MidiEvent {

    private int control;
    private int value;

    public int getKey() {
        return control;
    }
}
