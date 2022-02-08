package com.ownimage.midi2key.model;

import lombok.*;

import java.util.ArrayList;

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

    public String toPrettyString() {
        var list = new ArrayList<String>();
        if (ctrl) list.add("Ctrl");
        if (alt) list.add("Alt");
        if (shift) list.add("Shift");
        return (ctrl || alt || shift) ? String.join("-", list) + " " + description : description;
    }
}
