package com.ownimage.midi2key.adapter;

import com.ownimage.midi2key.core.Config;
import com.ownimage.midi2key.model.MidiAction;
import lombok.*;

import java.util.Optional;

import static com.ownimage.midi2key.model.MidiAction.Action.*;
import static com.ownimage.midi2key.model.MidiAction.ROTARY_MAX;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class AdapterMidiEvent {
    private int control;
    private int value;

    public Optional<MidiAction> toMidiAction(Integer previousValue, Config config) {
        if (config == null) return Optional.empty();

        var isRotary = config.isRotary(this);
        if (previousValue == null && isRotary) return Optional.empty();

        if (isRotary) { // is dial
            if (getValue() > previousValue)
                return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), UP));
            if (getValue() < previousValue)
                return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), DOWN));
            if (previousValue == getValue() && previousValue != 0 && previousValue != ROTARY_MAX)
                return Optional.empty();
            if (previousValue == 0)
                return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), DOWN));
            return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), UP));
        }
        // is button press
        if (value == 0) return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), PRESS));
        return Optional.empty();

    }
}
