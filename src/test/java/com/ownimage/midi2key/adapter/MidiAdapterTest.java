package com.ownimage.midi2key.adapter;

import com.ownimage.midi2key.core.Config;
import com.ownimage.midi2key.core.ConfigSuppier;
import com.ownimage.midi2key.core.MidiActionReceiver;
import com.ownimage.midi2key.model.MidiAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.stream.Stream;

import static com.ownimage.midi2key.model.MidiAction.Action.*;
import static com.ownimage.midi2key.model.MidiAction.ROTARY_MAX;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MidiAdapterTest implements MidiActionReceiver, ConfigSuppier {

    private MidiAdapter underTest;

    private Config config;
    private MidiAction latestMidiEvent;

    private static Stream<Arguments> testSend_parameters() {
        var control = 10;
        var press = new MidiAction(control, PRESS);
        var up = new MidiAction(control, UP);
        var down = new MidiAction(control, DOWN);

        return Stream.of(
                // non rotary always fires regardless of previous value
                Arguments.of(false, control, null, 10, press),
                Arguments.of(false, control, 5, 10, press),
                Arguments.of(false, control, 10, 10, press),
                Arguments.of(false, control, 20, 10, press),
                // rotary null
                Arguments.of(true, control, null, 20, null),
                Arguments.of(true, control, null, 0, null),
                Arguments.of(true, control, null, ROTARY_MAX, null),
                Arguments.of(true, control, 20, 20, null),
                // rotary up
                Arguments.of(true, control, 20, 21, up),
                Arguments.of(true, control, 21, ROTARY_MAX, up),
                Arguments.of(true, control, ROTARY_MAX, ROTARY_MAX, up),
                // rotary down
                Arguments.of(true, control, 20, 19, down),
                Arguments.of(true, control, 19, 0, down),
                Arguments.of(true, control, 0, 0, down)
        );
    }

    @BeforeEach
    public void setup() {
        underTest = new MidiAdapter(this, this, false);
        config = Config.builder().build();
        latestMidiEvent = null;
    }

    @ParameterizedTest
    @MethodSource("testSend_parameters")
    public void testSend(boolean isRotary, int control, Integer previousValue, int value, MidiAction expected) throws InvalidMidiDataException {
        // given
        if (isRotary) config = config.addRotaryControl(control);
        if (previousValue != null) underTest.send(shortMessage(control, previousValue), 0L);
        // when
        underTest.send(shortMessage(control, value), 0L);
        // then
        assertEquals(expected, latestMidiEvent);
    }

    private ShortMessage shortMessage(int control, int value) throws InvalidMidiDataException {
        return new ShortMessage(0x89, 10, control, value);
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public void receive(MidiAction midiEvent) {
        latestMidiEvent = midiEvent;
    }
}
