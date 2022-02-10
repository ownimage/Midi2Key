package com.ownimage.midi2key.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ownimage.midi2key.core.Config;
import com.ownimage.midi2key.model.MidiAction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static com.ownimage.midi2key.model.MidiAction.Action.DOWN;
import static com.ownimage.midi2key.model.MidiAction.Action.UP;
import static org.junit.jupiter.api.Assertions.*;

class AdapterMidiEventTest {

    Gson gson;
    private AdapterMidiEvent underTest;

    private static Stream<Arguments> toMidiAction_parameters() {
        var rawButton = new AdapterMidiEvent(10, false, 20);
        var raw0Button = new AdapterMidiEvent(10, false, 0);
        var rawMaxButton = new AdapterMidiEvent(10, false, MidiAction.ROTARY_MAX);
        var rawDial = new AdapterMidiEvent(10, true, 20);
        var raw0Dial = new AdapterMidiEvent(10, true, 0);
        var rawMaxDial = new AdapterMidiEvent(10, true, MidiAction.ROTARY_MAX);

        var actionButtonUP = Optional.of(new MidiAction(10, false, UP));
        var actionButtonDOWN = Optional.of(new MidiAction(10, false, DOWN));
        var actionDialUP = Optional.of(new MidiAction(10, true, UP));
        var actionDialDOWN = Optional.of(new MidiAction(10, true, DOWN));
        return Stream.of(
                // rotary null values
                Arguments.of(raw0Dial, null, actionDialDOWN),
                Arguments.of(rawDial, null, actionDialUP),
                // button presses
                Arguments.of(rawButton, 20, Optional.empty()),
                Arguments.of(rawButton, 10, Optional.empty()),
                Arguments.of(rawButton, 30, Optional.empty()),
                Arguments.of(rawButton, null, Optional.empty()),
                Arguments.of(raw0Button, 20, actionButtonUP),
                Arguments.of(raw0Button, 10, actionButtonUP),
                Arguments.of(raw0Button, 30, actionButtonUP),
                Arguments.of(raw0Button, null, actionButtonUP),
                Arguments.of(rawMaxButton, 20, actionButtonDOWN),
                Arguments.of(rawMaxButton, 10, actionButtonDOWN),
                Arguments.of(rawMaxButton, 30, actionButtonDOWN),
                Arguments.of(rawMaxButton, null, actionButtonDOWN),
                // rotary
                Arguments.of(rawDial, 10, actionDialUP),
                Arguments.of(rawDial, 30, actionDialDOWN),
                Arguments.of(rawDial, 20, Optional.empty()),
                // rotary spin down past 0
                Arguments.of(raw0Dial, 0, actionDialDOWN),
                // rotary spin up past ROTARY_MAX
                Arguments.of(rawMaxDial, MidiAction.ROTARY_MAX, actionDialUP)
        );
    }

    @BeforeEach
    public void before() {
        underTest = new AdapterMidiEvent(10, false, 20);
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void testEquals() {
        // given
        var compare = new AdapterMidiEvent(10, false, 20);
        // then
        assertTrue(compare.equals(underTest));
    }

    @Test
    public void testNotEquals() {
        // given
        var compare = new AdapterMidiEvent(10, false, 21);
        // then
        assertFalse(compare.equals(underTest));
    }

    @Test
    public void testToJson() {
        // given
        var expected = "{\"control\":10,\"rotary\":false,\"value\":20}";
        // when
        var actual = gson.toJson(underTest);
        // then
        assertEquals(expected, actual);
    }

    @Test
    public void testFromJson() {
        // given
        var from = "{\"control\":10,\"value\":20}";
        // when
        var actual = gson.fromJson(from, AdapterMidiEvent.class);
        // then
        assertEquals(underTest, actual);
    }

    @ParameterizedTest
    @MethodSource("toMidiAction_parameters")
    public void toMidiAction(@NotNull AdapterMidiEvent raw, Integer previous, Optional<MidiAction> expected) {
        // given
        var config = Config.builder().build();
        // when - then
        assertEquals(expected, raw.toMidiAction(previous));
    }
}