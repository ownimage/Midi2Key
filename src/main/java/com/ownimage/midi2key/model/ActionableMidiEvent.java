package com.ownimage.midi2key.model;

import com.ownimage.midi2key.core.Config;
import lombok.*;

import java.util.Optional;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class ActionableMidiEvent {

    public static final int ROTARY_MAX = 127;

    private final int control;
    private final MidiAction action;

    public static Optional<ActionableMidiEvent> from(RawMidiEvent raw, Integer previousValue, Config config) {
        if (raw == null || config == null) return Optional.empty();
        if (previousValue == null && config.isRotary(raw)) return Optional.empty();

        if (config.isRotary(raw)) { // is dial
            if (raw.getValue() > previousValue)
                return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.UP));
            if (raw.getValue() < previousValue)
                return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.DOWN));
            if (previousValue == raw.getValue() && previousValue != 0 && previousValue != ROTARY_MAX)
                return Optional.empty();
            if (previousValue == 0)
                return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.DOWN));
            return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.UP));
        }
        // is button press
        return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.PRESS));
    }

    public String getKey() {
        return String.format("%s-%s", control, action);
    }
}
