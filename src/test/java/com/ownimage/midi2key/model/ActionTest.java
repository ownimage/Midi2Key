package com.ownimage.midi2key.model;

import com.ownimage.midi2key.adapter.AdapterMidiEvent;
import com.ownimage.midi2key.core.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static com.ownimage.midi2key.model.MidiAction.Action.*;
import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    MidiAction underTest = new MidiAction(10, UP);

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testGetKey() {
        // given - when - then
        assertEquals("10-UP", underTest.getKey());
    }
}