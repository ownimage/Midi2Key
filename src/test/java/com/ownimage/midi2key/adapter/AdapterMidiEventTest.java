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

import static com.ownimage.midi2key.model.MidiAction.Action.*;
import static org.junit.jupiter.api.Assertions.*;

class AdapterMidiEventTest {

    Gson gson;
    private AdapterMidiEvent underTest;

    private static Stream<Arguments> toMidiAction_parameters() {
        var raw = new AdapterMidiEvent(10, 20);
        var raw0 = new AdapterMidiEvent(10, 0);
        var rawMax = new AdapterMidiEvent(10, MidiAction.ROTARY_MAX);

        var actionPRESS = Optional.of(new MidiAction(10, PRESS));
        var actionUP = Optional.of(new MidiAction(10, UP));
        var actionDOWN = Optional.of(new MidiAction(10, DOWN));

        return Stream.of(
                // rotary null values
                Arguments.of(raw, true, null, Optional.empty()),
                // button presses
                Arguments.of(raw0, false, 20, actionPRESS),
                Arguments.of(raw0, false, 10, actionPRESS),
                Arguments.of(raw0, false, 30, actionPRESS),
                Arguments.of(raw0, false, null, actionPRESS),
                // rotary
                Arguments.of(raw, true, 10, actionUP),
                Arguments.of(raw, true, 30, actionDOWN),
                Arguments.of(raw, true, 20, Optional.empty()),
                // rotary spin down past 0
                Arguments.of(raw0, true, 0, actionDOWN),
                // rotary spin up past ROTARY_MAX
                Arguments.of(rawMax, true, MidiAction.ROTARY_MAX, actionUP)
        );
    }

    @BeforeEach
    public void before() {
        underTest = new AdapterMidiEvent(10, 20);
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void testEquals() {
        // given
        var compare = new AdapterMidiEvent(10, 20);
        // then
        assertTrue(compare.equals(underTest));
    }

    @Test
    public void testNotEquals() {
        // given
        var compare = new AdapterMidiEvent(10, 21);
        // then
        assertFalse(compare.equals(underTest));
    }

    @Test
    public void testToJson() {
        // given
        var expected = "{\"control\":10,\"value\":20}";
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
    public void toMidiAction(@NotNull AdapterMidiEvent raw, boolean isRotary, Integer previous, Optional<MidiAction> expected) {
        // given
        var config = Config.builder().build();
        if (isRotary) config = config.addRotaryControl(new MidiAction(raw.getControl(), UP));
        // when - then
        assertEquals(expected, raw.toMidiAction(previous, config));
    }
}