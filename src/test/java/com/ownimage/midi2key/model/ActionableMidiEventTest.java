package com.ownimage.midi2key.model;

import com.ownimage.midi2key.core.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ActionableMidiEventTest {

    ActionableMidiEvent underTest = new ActionableMidiEvent(10, MidiAction.UP);

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testGetKey() {
        // given - when - then
        assertEquals("10-UP", underTest.getKey());
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    public void testFrom(RawMidiEvent raw, boolean isRotary, Integer previous, Optional<ActionableMidiEvent> expected) {
        // given
        var config = Config.builder().build();
        if (isRotary) config = config.addRotaryControl(raw);
        // when - then
        assertEquals(expected, ActionableMidiEvent.from(raw, previous, config));
    }

    private static Stream<Arguments> provideStringsForIsBlank() {
        var raw = new RawMidiEvent(10, 20);
        var raw0 = new RawMidiEvent(10, 0);
        var rawMax = new RawMidiEvent(10, ActionableMidiEvent.ROTARY_MAX);

        var actionPRESS = Optional.of(new ActionableMidiEvent(10, MidiAction.PRESS));
        var actionUP = Optional.of(new ActionableMidiEvent(10, MidiAction.UP));
        var actionDOWN = Optional.of(new ActionableMidiEvent(10, MidiAction.DOWN));

        return Stream.of(
                // rotary null values
                Arguments.of(raw, true, null, Optional.empty()),
                Arguments.of(null, true, null, Optional.empty()),
                Arguments.of(null, true, 10, Optional.empty()),
                // button null values
                Arguments.of(null, false, null, Optional.empty()),
                Arguments.of(null, false, 10, Optional.empty()),
                // button presses
                Arguments.of(raw, false, 20, actionPRESS),
                Arguments.of(raw, false, 10, actionPRESS),
                Arguments.of(raw, false, 30, actionPRESS),
                Arguments.of(raw, false, null, actionPRESS),
                // rotary
                Arguments.of(raw, true, 10, actionUP),
                Arguments.of(raw, true, 30, actionDOWN),
                Arguments.of(raw, true, 20, Optional.empty()),
                // rotary spin down past 0
                Arguments.of(raw0, true, 0, actionDOWN),
                // rotary spin up past ROTARY_MAX
                Arguments.of(rawMax, true, ActionableMidiEvent.ROTARY_MAX, actionUP)
        );
    }
}