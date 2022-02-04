package com.ownimage.midi2key.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyboardStrokeTest {

    private KeyboardStroke underTest;

    Gson gson;

    @BeforeEach
    public void before() {
        underTest= new KeyboardStroke(true, false, true, (char) 11);
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void testEquals() {
        // given
        var compare = new KeyboardStroke(true, false, true, (char) 11);
        // then
        assertTrue(compare.equals(underTest));
    }

    @Test
    public void testNotEquals() {
        // given
        var compare = new KeyboardStroke(false, false, true, (char) 11);
        // then
        assertFalse(compare.equals(underTest));
    }

    @Test
    public void testToJson() {
        // given
        var expected = "{\"ctrl\":true,\"alt\":false,\"shift\":true,\"keyChar\":11}";
        // when
        var actual = gson.toJson(underTest);
        // then
        assertEquals(expected, actual);
    }

    @Test
    public void testFromJson() {
        // given
        var from = "{\"ctrl\":true,\"alt\":false,\"shift\":true,\"keyChar\":11}";
        // when
        var actual = gson.fromJson(from, KeyboardStroke.class);
        // then
        assertEquals(underTest, actual);
    }
}