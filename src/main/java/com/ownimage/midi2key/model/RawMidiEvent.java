package com.ownimage.midi2key.model;

import lombok.*;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class RawMidiEvent {

    private int control;
    private int value;

    public String getKey() {
        return "" + control;
    }
}
