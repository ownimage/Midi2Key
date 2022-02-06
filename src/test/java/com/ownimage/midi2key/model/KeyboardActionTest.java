package com.ownimage.midi2key.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyboardActionTest {

    private KeyboardAction underTest;

    Gson gson;

    @BeforeEach
    public void before() {
        underTest= new KeyboardAction(true, false, true, (char) 11, "DESC");
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void testEquals() {
        // given
        var compare = new KeyboardAction(true, false, true, (char) 11, "DESC");
        // then
        assertTrue(compare.equals(underTest));
    }

    @Test
    public void testNotEquals() {
        // given
        var compare = new KeyboardAction(false, false, true, (char) 11, "DESC");
        // then
        assertFalse(compare.equals(underTest));
    }

    @Test
    public void testToJson() {
        // given
        var expected = "{\"ctrl\":true,\"alt\":false,\"shift\":true,\"keyCode\":11,\"description\":\"DESC\"}";
        // when
        var actual = gson.toJson(underTest);
        // then
        assertEquals(expected, actual);
    }

    @Test
    public void testFromJson() {
        // given
        var from = "{\"ctrl\":true,\"alt\":false,\"shift\":true,\"keyCode\":11,\"description\":\"DESC\"}";
        // when
        var actual = gson.fromJson(from, KeyboardAction.class);
        // then
        assertEquals(underTest, actual);
    }
}