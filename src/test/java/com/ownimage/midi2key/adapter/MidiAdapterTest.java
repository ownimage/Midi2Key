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

import static com.ownimage.midi2key.model.MidiAction.Action.DOWN;
import static com.ownimage.midi2key.model.MidiAction.Action.UP;
import static com.ownimage.midi2key.model.MidiAction.ROTARY_MAX;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MidiAdapterTest implements MidiActionReceiver, ConfigSuppier {

    private MidiAdapter underTest;

    private Config config;
    private MidiAction latestMidiEvent;

    private static Stream<Arguments> testSend_parameters() {
        var control = 10;
        var buttonUp = new MidiAction(control, false, UP);
        var buttonDown = new MidiAction(control, false, DOWN);
        var dialUp = new MidiAction(control, true, UP);
        var dialDown = new MidiAction(control, true, DOWN);

        return Stream.of(
                // non rotary always fires regardless of previous value
                Arguments.of(false, control, null, 0, buttonUp),
                Arguments.of(false, control, 5, 0, buttonUp),
                Arguments.of(false, control, 10, 0, buttonUp),
                Arguments.of(false, control, 20, 0, buttonUp),
                Arguments.of(false, control, ROTARY_MAX, 0, buttonUp),
                Arguments.of(false, control, null, ROTARY_MAX, buttonDown),
                Arguments.of(false, control, 5, ROTARY_MAX, buttonDown),
                Arguments.of(false, control, 10, ROTARY_MAX, buttonDown),
                Arguments.of(false, control, 20, ROTARY_MAX, buttonDown),
                Arguments.of(false, control, ROTARY_MAX, ROTARY_MAX, buttonDown),
                // rotary null
                Arguments.of(true, control, 20, 20, dialUp), // note the dialUp is from the set previous value
                // rotary up
                Arguments.of(true, control, 20, 21, dialUp),
                Arguments.of(true, control, 21, ROTARY_MAX, dialUp),
                Arguments.of(true, control, ROTARY_MAX, ROTARY_MAX, dialUp),
                Arguments.of(true, control, null, 20, dialUp),
                Arguments.of(true, control, null, ROTARY_MAX, dialUp),
                // rotary down
                Arguments.of(true, control, null, 0, dialDown),
                Arguments.of(true, control, 20, 19, dialDown),
                Arguments.of(true, control, 19, 0, dialDown),
                Arguments.of(true, control, 0, 0, dialDown)
        );
    }

    @BeforeEach
    public void setup() {
        underTest = new MidiAdapter(this, false);
        config = Config.builder().build();
        latestMidiEvent = null;
    }

    @ParameterizedTest
    @MethodSource("testSend_parameters")
    public void testSend(boolean isRotary, int control, Integer previousValue, int value, MidiAction expected) throws InvalidMidiDataException {
        // given
        if (previousValue != null) underTest.send(shortMessage(control, isRotary, previousValue), 0L);
        // when
        underTest.send(shortMessage(control, isRotary, value), 0L);
        // then
        assertEquals(expected, latestMidiEvent);
    }

    private ShortMessage shortMessage(int control, boolean rotary, int value) throws InvalidMidiDataException {
        var command = rotary ? 0xBA : 0x89;
        return new ShortMessage(command, 10, control, value);
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public void receive(boolean rotary, MidiAction midiEvent) {
        latestMidiEvent = midiEvent;
    }
}
