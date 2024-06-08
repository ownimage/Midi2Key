package com.ownimage.midi2key.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.ownimage.midi2key.model.MidiAction.Action.DOWN;
import static com.ownimage.midi2key.model.MidiAction.Action.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {

    Gson gson;
    private Config underTest;


    @BeforeEach
    public void before() {
        underTest = Config.builder().build();
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void defaultConstructorToJSON() {
        // given
        var expected = "{\"filename\":\"config.json\",\"mapping\":{},\"labels\":{}}";
        // when
        var actual = gson.toJson(underTest);
        // then
        assertEquals(expected, actual);
    }


    @Test
    public void testSave(@TempDir Path tempDir) throws IOException {
        // given
        var fileName = "config.json";
        var testFile = new File(tempDir.toFile(), fileName);
        var config = underTest
                .filename(testFile.getAbsolutePath())
                .addMapping(new MidiAction(10, false, DOWN), new KeyboardAction(true, true, false, (char) 19, "DESC-A"))
                .addMapping(new MidiAction(11, false, UP), new KeyboardAction(true, true, false, (char) 120, "DESC-B"));
        var expected = "{\"filename\":\"config.json\",\"mapping\":{\"10\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyCode\":19,\"description\":\"DESC-A\"},\"11\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyCode\":120,\"description\":\"DESC-B\"}},\"labels\":{}}";
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

        var expected = "{\"filename\":\"config.json\",\"mapping\":{\"10\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyCode\":19},\"11\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyCode\":120}}}";
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
        var expectedOriginal = "{\"filename\":\"config.json\",\"mapping\":{},\"labels\":{}}";
        var expectedAfter = "{\"filename\":\"config.json\",\"mapping\":{\"1R-DOWN\":{\"ctrl\":true,\"alt\":false,\"shift\":true,\"keyCode\":12,\"description\":\"button\"},\"2\":{\"ctrl\":false,\"alt\":true,\"shift\":false,\"keyCode\":11,\"description\":\"dial\"}},\"labels\":{}}";
        // when
        var after = underTest
                .addMapping(new MidiAction(1, true, DOWN), new KeyboardAction(true, false, true, (char) 12, "button"))
                .addMapping(new MidiAction(2, false, DOWN), new KeyboardAction(false, true, false, (char) 11, "dial"));
        var actualOriginal = gson.toJson(underTest);
        var actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);
    }

    @ParameterizedTest
    @CsvSource({
            "12-PRESS,12,label,label-PRESS",
            "AAA-PRESS, 12, BBB, AAA-PRESS",
            "NotProperlyFormatted,10,A1B1,NotProperlyFormatted"
    })
    public void testActionNameToString(String mappingKey, int control, String label, String expected) {
        // given
        underTest = underTest.addLabel(new MidiAction(control, true, DOWN), label);
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
        var midiEventUp = new MidiAction(10, false, UP);
        var midiEventDown = new MidiAction(10, false, DOWN);
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
        var midiEventUp = new MidiAction(10, false, UP);
        var keyboardAction = new KeyboardAction(true, true, true, 20, "A");
        underTest = underTest.addMapping(midiEventUp, keyboardAction);
        var expected = Optional.of(keyboardAction);
        // when
        var actual = underTest.map(midiEventUp);
        // then
        assertEquals(expected, actual);
    }

    @Test
    void testMap_for_rotary_2() {
        // given
        var midiEventUp = new MidiAction(10, true, UP);
        var keyboardAction = new KeyboardAction(true, true, true, 20, "A");
        underTest = underTest.addMapping(midiEventUp, keyboardAction);
        var expected = Optional.of(keyboardAction);
        // when
        var actual = underTest.map(midiEventUp);
        // then
        assertEquals(expected, actual);
    }

}