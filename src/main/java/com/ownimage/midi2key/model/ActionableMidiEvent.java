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

    private int control;
    private MidiAction action;

    public String getKey() {
        return String.format("%s-%s", control, action);
    }

    public static Optional<ActionableMidiEvent> from(RawMidiEvent raw, Integer previousValue, Config config) {
        if (raw == null || previousValue == null) return Optional.empty();

        if (config.isRotary(raw)) { // is dial
            if (raw.getValue() > previousValue) return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.UP));
            if (raw.getValue() < previousValue) return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.DOWN));
            if (previousValue == 0) return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.DOWN));
            Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.UP));
        }
        // is button press
        return Optional.of(new ActionableMidiEvent(raw.getControl(), MidiAction.PRESS));
    }
}
