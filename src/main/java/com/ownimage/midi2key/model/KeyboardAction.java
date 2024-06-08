package com.ownimage.midi2key.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;

@Builder
@Getter
@Accessors(fluent = true)
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class KeyboardAction {

    private final boolean ctrl;
    private final boolean alt;
    private final boolean shift;
    private final int keyCode;
    private final String description;

    public String toPrettyString() {
        var list = new ArrayList<String>();
        if (ctrl) list.add("Ctrl");
        if (alt) list.add("Alt");
        if (shift) list.add("Shift");
        return (ctrl || alt || shift) ? String.join("-", list) + " " + description : description;
    }
}
