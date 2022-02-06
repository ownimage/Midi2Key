package com.ownimage.midi2key.model;

import lombok.*;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class KeyboardAction {
    private boolean ctrl;
    private boolean alt;
    private boolean shift;
    private int keyCode;
    private String description;
}
