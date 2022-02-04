package com.ownimage.midi2key;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ownimage.midi2key.core.Config;
import com.ownimage.midi2key.model.KeyboardStroke;
import com.ownimage.midi2key.model.RawMidiEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    private Config underTest;

    Gson gson;

    @BeforeEach
    public void before(){
        underTest = Config.builder().build();
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void defaultConstructorToJSON() {
        // given
        var expected = "{\"rotaryControl\":[],\"mapping\":{}}";
        // when
        var actual = gson.toJson(underTest);
        // then
        assertEquals(expected, actual);
    }

    @Test
    public void addRotaryControl() {
        // given
        var expectedOriginal = "{\"rotaryControl\":[],\"mapping\":{}}";
        var expectedAfter = "{\"rotaryControl\":[1],\"mapping\":{}}";
        // when
        var after = underTest.addRotaryControl(new RawMidiEvent(1, 10));
        var actualOriginal = gson.toJson(underTest);
        var actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);
    }

    @Test
    public void addTwoRotaryControls() {
        // given
        var expectedOriginal = "{\"rotaryControl\":[],\"mapping\":{}}";
        var expectedAfter = "{\"rotaryControl\":[1,2],\"mapping\":{}}";
        // when
        var after = underTest
                .addRotaryControl(new RawMidiEvent(1, 10))
                .addRotaryControl(new RawMidiEvent(2, 10));
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
                .addRotaryControl(new RawMidiEvent(1, 10))
                .addRotaryControl(new RawMidiEvent(2, 10))
                .addMapping(new RawMidiEvent(10, 20), new KeyboardStroke(true, true, false, (char)19))
                .addMapping(new RawMidiEvent(11, 21), new KeyboardStroke(true, true, false, (char)120));
        var expected = "{\"rotaryControl\":[1,2],\"mapping\":{\"11\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyChar\":120},\"10\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyChar\":19}}}";
        // when
        config.save(testFile);
        // then
        var actual = fileToString(testFile);
        assertEquals(expected, actual);
    }

    @Test
    public void testRead(@TempDir Path tempDir) throws IOException {
        // given
        var fileName = "config.json";
        var testFile = new File(tempDir.toFile(), fileName);;
        var expected = "{\"rotaryControl\":[1,2],\"mapping\":{\"11\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyChar\":120},\"10\":{\"ctrl\":true,\"alt\":true,\"shift\":false,\"keyChar\":19}}}";
        stringToFile(expected,testFile);
        // when
        var config = Config.read(testFile);
        // then
        var actual = gson.toJson(config);
        assertEquals(expected, actual);
    }

    @Test
    public void testAddMapping() {
        // given
        var expectedOriginal = "{\"rotaryControl\":[],\"mapping\":{}}";
        var expectedAfter = "{\"rotaryControl\":[],\"mapping\":{\"1\":{\"ctrl\":true,\"alt\":false,\"shift\":true,\"keyChar\":12}}}";
        // when
        var after = underTest.addMapping(new RawMidiEvent(1, 2), new KeyboardStroke(true, false, true, (char) 12));
        var actualOriginal = gson.toJson(underTest);
        var actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);

    }

    @Test
    void testIsRotaryOnSameObject() {
        // given
        var raw = new RawMidiEvent(10, 20);
        var config = underTest.addRotaryControl(raw);
        // when - then
        assertTrue(config.isRotary(raw));
    }

    @Test
    void testIsRotaryEqualObject() {
        // given
        var raw1 = new RawMidiEvent(10, 20);
        var raw2 = new RawMidiEvent(10, 20);
        var config = underTest.addRotaryControl(raw1);
        // when - then
        assertTrue(config.isRotary(raw2));
    }

    @Test
    void testIsRotaryOnDifferentObject() {
        // given
        var raw1 = new RawMidiEvent(10, 20);
        var raw2 = new RawMidiEvent(11, 21);
        var config = underTest.addRotaryControl(raw1);
        // when - then
        assertFalse(config.isRotary(raw2));
    }

    private String fileToString(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    private void stringToFile(String content, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}