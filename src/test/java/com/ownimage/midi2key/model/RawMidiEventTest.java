package com.ownimage.midi2key.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RawMidiEventTest {

    private RawMidiEvent underTest;

    Gson gson;

    @BeforeEach
    public void before() {
        underTest= new RawMidiEvent(10, 20);
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void testEquals() {
        // given
        var compare =  new RawMidiEvent(10, 20);
        // then
        assertTrue(compare.equals(underTest));
    }

    @Test
    public void testNotEquals() {
        // given
        var compare = new RawMidiEvent(10, 21);
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
        var actual = gson.fromJson(from, RawMidiEvent.class);
        // then
        assertEquals(underTest, actual);
    }
}