package com.ownimage.midi2key.adapter;

import com.ownimage.midi2key.model.MidiAction;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.log4j.Logger;

import java.util.Optional;

import static com.ownimage.midi2key.model.MidiAction.Action.DOWN;
import static com.ownimage.midi2key.model.MidiAction.Action.UP;
import static com.ownimage.midi2key.model.MidiAction.ROTARY_MAX;

@Builder
@Getter
@Accessors(fluent = true)
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class AdapterMidiEvent {

    private static final Logger logger = Logger.getLogger(AdapterMidiEvent.class);

    private final int control;
    private final boolean rotary;
    private final int value;

    public Optional<MidiAction> toMidiAction(Integer previousValue) {
        if (rotary) { // is dial
            if (previousValue != null && value > previousValue)
                return Optional.of(new MidiAction(control, rotary, UP));
            if (previousValue != null && value < previousValue)
                return Optional.of(new MidiAction(control, rotary, DOWN));
            if (previousValue != null && previousValue == value && previousValue != 0 && previousValue != ROTARY_MAX)
                return Optional.empty();
            if (previousValue != null && previousValue == 0)
                return Optional.of(new MidiAction(control, rotary, DOWN));
            if (previousValue == null && value == 0)
                return Optional.of(new MidiAction(control, rotary, DOWN));
            return Optional.of(new MidiAction(control, rotary, UP));
        }
        // is button press
        logger.debug("AdapterMidiEvent::toMidiAction value=" + value);
        if (value == 0) return Optional.of(new MidiAction(control, rotary, UP));
        if (value == ROTARY_MAX) return Optional.of(new MidiAction(control, rotary, DOWN));
        return Optional.empty();
    }


}
