package com.ownimage.midi2key.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ownimage.midi2key.adapter.AdapterMidiEvent;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static com.ownimage.midi2key.model.MidiAction.Action.DOWN;
import static com.ownimage.midi2key.model.MidiAction.Action.UP;
import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    Gson gson;
    private Config underTest;

    private static Stream<Arguments> testIsRotary_parameters() {
        var add = new AdapterMidiEvent(10, 20);
        var same = new AdapterMidiEvent(10, 20);
        var differentControl = new AdapterMidiEvent(11, 20);
        var differentValue = new AdapterMidiEvent(10, 20);

        return Stream.of(
                Arguments.of("same object", add, add, true),
                Arguments.of("same value", add, same, true),
                Arguments.of("different control", add, differentControl, false),
                Arguments.of("different value", add, differentValue, true)
        );
    }

    @BeforeEach
    public void before() {
        underTest = Config.builder().build();
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void defaultConstructorToJSON() {
        // given
        var expected = "{\"filename\":\"config.json\",\"rotaryControls\":[],\"mapping\":{},\"labels\":{}}";
        // when
        var actual = gson.toJson(underTest);
        // then
        assertEquals(expected, actual);
    }

    @Test
    public void addRotaryControl() {
        // given
        var expectedOriginal = "{\"filename\":\"config.json\",\"rotaryControls\":[],\"mapping\":{},\"labels\":{}}";
        var expectedAfter = "{\"filename\":\"config.json\",\"rotaryControls\":[1],\"mapping\":{},\"labels\":{}}";
        // when
        var after = underTest.addRotaryControl(new MidiAction(1, UP));
        var actualOriginal = gson.toJson(underTest);
        var actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);
    }

    @Test
    public void addTwoRotaryControls() {
        // given
        var expectedOriginal = "{\"filename\":\"config.json\",\"rotaryControls\":[],\"mapping\":{},\"labels\":{}}";
        var expectedAfter = "{\"filename\":\"config.json\",\"rotaryControls\":[1,2],\"mapping\":{},\"labels\":{}}";
        // when
        var after = underTest
                .addRotaryControl(new MidiAction(1, UP))
                .addRotaryControl(new MidiAction(2, UP));
        var actualOriginal = gson.toJson(underTest);
        var actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);
    }

    @Test
    public void testSave(@TempDir Path tempDir) throws IOException {
        // given
        var fileName = "config.json";
        var testFile = new File(tempDir.toFile(), fileName);
        var config = underTest
                .filename(testFile.getAbsolutePath())
                .addRotaryControl(new MidiAction(1, UP))
                .addRotaryControl(new MidiAction(2, UP))
                .addMapping(new MidiAction(10, DOWN), new KeyboardAction(true, true, false, (char) 19, "DESC-A"))
                .addMapping(new MidiAction(11, UP), new KeyboardAction(true, true, false, (char) 120, "DESC-B"));
        var expected = "{\"filename\":\"config.json\",\"rotaryControls\":[1,2],\"mapping\":{\"10\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyCode\":19,\"description\":\"DESC-A\"},\"11\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyCode\":120,\"description\":\"DESC-B\"}},\"labels\":{}}";
        // when
        config.save(false);
        // then
        var actual = fileToString(testFile);
        assertEquals(expected, actual);
    }

    @Test
    public void testOpen(@TempDir Path tempDir) throws IOException {
        // given
        var fileName = "config.json";
        var testFile = new File(tempDir.toFile(), fileName);

        var expected = "{\"filename\":\"config.json\",\"rotaryControls\":[1,2],\"mapping\":{\"10\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyCode\":19},\"11\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyCode\":120}}}";
        stringToFile(expected, testFile);
        var config = underTest.withFilename(testFile.getAbsolutePath());
        // when
        config = config.open();
        // then
        var actual = gson.toJson(config);
        assertEquals(expected, actual);
    }

    @Test
    public void testAddMapping() {
        // given
        var expectedOriginal = "{\"filename\":\"config.json\",\"rotaryControls\":[],\"mapping\":{},\"labels\":{}}";
        var expectedAfter = "{\"filename\":\"config.json\",\"rotaryControls\":[],\"mapping\":{\"1\":{\"ctrl\":true,\"alt\":false,\"shift\":true,\"keyCode\":12,\"description\":\"DESC\"}},\"labels\":{}}";
        // when
        var after = underTest.addMapping(new MidiAction(1, DOWN), new KeyboardAction(true, false, true, (char) 12, "DESC"));
        var actualOriginal = gson.toJson(underTest);
        var actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);
    }

    @Test
    void testIsRotaryWithMutilpleObjects() {
        // given
        var t1 = new AdapterMidiEvent(1, 20);
        var t2 = new AdapterMidiEvent(2, 20);
        var t3 = new AdapterMidiEvent(3, 20);
        var t4 = new AdapterMidiEvent(4, 20);
        var t5 = new AdapterMidiEvent(5, 20);
        // when
        var config = underTest
                .addRotaryControl(new MidiAction(1, UP))
                .addRotaryControl(new MidiAction(2, UP))
                .addRotaryControl(new MidiAction(3, UP))
                .addRotaryControl(new MidiAction(4, UP));
        // then
        assertTrue(config.isRotary(t1));
        assertTrue(config.isRotary(t2));
        assertTrue(config.isRotary(t3));
        assertTrue(config.isRotary(t4));
        assertFalse(config.isRotary(t5));
    }

    @ParameterizedTest
    @MethodSource("testIsRotary_parameters")
    void testIsRotary(String name, AdapterMidiEvent add, AdapterMidiEvent test, boolean expected) {
        // given - when
        var config = underTest.addRotaryControl(new MidiAction(add.getControl(), UP));
        // then
        assertEquals(expected, config.isRotary(test));
    }

    @ParameterizedTest
    @CsvSource({
            "12-PRESS,12,label,label-PRESS",
            "AAA-PRESS, 12, BBB, AAA-PRESS",
            "NotProperlyFormatted,10,A1B1,NotProperlyFormatted"
    })
    public void testActionNameToString(String mappingKey, int control, String label, String expected) {
        // given
        underTest = underTest.addLabel(new MidiAction(control, DOWN), label);
        // when
        var actual = underTest.actionNameToString(mappingKey);
        // then
        assertEquals(expected, actual);
    }

    private String fileToString(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    @SneakyThrows
    private void stringToFile(String content, File file) {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.flush();
        fileWriter.close();
    }

    @Test
    void testMap_for_buttons_1() {
        // given
        var midiEventUp = new MidiAction(10, UP);
        var midiEventDown = new MidiAction(10, DOWN);
        var keyboardAction = new KeyboardAction(true, true, true, 20, "A");
        underTest = underTest.addMapping(midiEventUp, keyboardAction);
        var expected = Optional.of(keyboardAction);
        // when
        var actual = underTest.map(midiEventDown);
        // then
        assertEquals(expected, actual);
    }

    @Test
    void testMap_for_buttons_2() {
        // given
        var midiEventUp = new MidiAction(10, UP);
        var keyboardAction = new KeyboardAction(true, true, true, 20, "A");
        underTest = underTest.addMapping(midiEventUp, keyboardAction);
        var expected = Optional.of(keyboardAction);
        // when
        var actual = underTest.map(midiEventUp);
        // then
        assertEquals(expected, actual);
    }

    @Test
    void testMap_for_rotary_not_mapped_1() {
        // given
        var control = 10;
        var midiEventUp = new MidiAction(control, UP);
        var expected = Optional.empty();
        // when
        var actual = underTest.map(midiEventUp);
        // then
        assertEquals(expected, actual);
    }

    @Test
    void testMap_for_rotary_not_mapped_2() {
        // given
        var control = 10;
        var midiEventUp = new MidiAction(control, UP);
        var midiEventDown = new MidiAction(control, DOWN);
        var keyboardAction = new KeyboardAction(true, true, true, 20, "A");
        underTest = underTest.addRotaryControl(midiEventUp).addMapping(midiEventUp, keyboardAction);
        var expected = Optional.empty();
        // when
        var actual = underTest.map(midiEventDown);
        // then
        assertEquals(expected, actual);
    }
}