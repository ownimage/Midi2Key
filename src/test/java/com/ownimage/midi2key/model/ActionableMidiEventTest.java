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
        return Stream.of(
                Arguments.of(null, true, null, Optional.empty()),
                Arguments.of(raw, true, null, Optional.empty()),
                Arguments.of(null, false, null, Optional.empty()),
                Arguments.of(raw, false, null, Optional.empty()),
                Arguments.of(raw, false, 20, Optional.of(new ActionableMidiEvent(10, MidiAction.PRESS))),
                Arguments.of(raw, false, 10, Optional.of(new ActionableMidiEvent(10, MidiAction.PRESS))),
                Arguments.of(raw, false, 30, Optional.of(new ActionableMidiEvent(10, MidiAction.PRESS))),
                // more test cases needed

        );
    }
}