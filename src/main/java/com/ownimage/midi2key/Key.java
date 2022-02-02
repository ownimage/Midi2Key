package com.ownimage.midi2key;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@ToString
public class Key {
    public boolean control;
    public boolean alt;
    public boolean shift;
    public short code;
}
