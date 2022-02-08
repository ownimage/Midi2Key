package com.ownimage.midi2key.adapter;

import com.ownimage.midi2key.model.MidiAction;
import lombok.*;
import org.apache.log4j.Logger;

import java.util.Optional;

import static com.ownimage.midi2key.model.MidiAction.Action.DOWN;
import static com.ownimage.midi2key.model.MidiAction.Action.UP;
import static com.ownimage.midi2key.model.MidiAction.ROTARY_MAX;

@Builder
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class AdapterMidiEvent {

    private static Logger logger = Logger.getLogger(AdapterMidiEvent.class);

    private int control;
    private int value;

    public Optional<MidiAction> toMidiAction(Integer previousValue, boolean rotary) {
        if (rotary) { // is dial
            if (previousValue != null && getValue() > previousValue)
                return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), UP));
            if (previousValue != null && getValue() < previousValue)
                return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), DOWN));
            if (previousValue != null && previousValue == getValue() && previousValue != 0 && previousValue != ROTARY_MAX)
                return Optional.empty();
            if (previousValue != null && previousValue == 0)
                return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), DOWN));
            return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), UP));
        }
        // is button press
        logger.debug("########### " + value);
        if (value == 0) return Optional.of(new com.ownimage.midi2key.model.MidiAction(getControl(), UP));
        if (value == ROTARY_MAX) return Optional.of(new MidiAction(getControl(), DOWN));
        return Optional.empty();

    }
}
