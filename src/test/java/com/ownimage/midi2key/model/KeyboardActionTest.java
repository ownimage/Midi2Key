package com.ownimage.midi2key.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class KeyboardActionTest {

    Gson gson;
    private KeyboardAction underTest;

    @BeforeEach
    public void before() {
        underTest = new KeyboardAction(true, false, true, (char) 11, "DESC");
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

    @ParameterizedTest
    @CsvSource({
            "false, false, false, A, A",
            "false, false, true, B, Shift B",
            "false, true, false, C, Alt C",
            "false, true, true, D, Alt-Shift D",
            "true, false, false, E, Ctrl E",
            "true, false, true, F, Ctrl-Shift F",
            "true, true, false, G, Ctrl-Alt G",
            "true, true, true, H, Ctrl-Alt-Shift H",
    })
    void toPrettyString(boolean ctrl, boolean alt, boolean shift, String description, String expected) {
        // given
        var underTest = new KeyboardAction(ctrl, alt, shift, (char) 10, description);
        // when
        var actual = underTest.toPrettyString();
        // then
        assertEquals(expected, actual);
    }
}