package com.ownimage.midi2key.model;

import lombok.*;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class KeyboardStroke {
    private boolean control;
    private boolean alt;
    private boolean shift;
    private short code;
}
