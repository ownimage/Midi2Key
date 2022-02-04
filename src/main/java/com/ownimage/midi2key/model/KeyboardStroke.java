package com.ownimage.midi2key.model;

import lombok.*;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class KeyboardStroke {
    private boolean ctrl;
    private boolean alt;
    private boolean shift;
    private int keyChar;
}
