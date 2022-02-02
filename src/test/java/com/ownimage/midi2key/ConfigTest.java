package com.ownimage.midi2key;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ownimage.midi2key.core.Config;
import com.ownimage.midi2key.model.KeyboardStroke;
import com.ownimage.midi2key.model.MidiEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var after = underTest.addRotaryControl(new MidiEvent(1, 10));
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
                .addRotaryControl(new MidiEvent(1, 10))
                .addRotaryControl(new MidiEvent(2, 10));
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
                .addRotaryControl(new MidiEvent(1, 10))
                .addRotaryControl(new MidiEvent(2, 10))
                .addMapping(new MidiEvent(10, 20), new KeyboardStroke(true, true, false, (short)19))
                .addMapping(new MidiEvent(11, 21), new KeyboardStroke(true, true, false, (short)120));
        var expected = "{\"rotaryControl\":[1,2],\"mapping\":{\"11\":{\"control\":true,\"alt\":true,\"shift\":false,\"code\":120},\"10\":{\"control\":true,\"alt\":true,\"shift\":false,\"code\":19}}}";
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
        var expected = "{\"rotaryControl\":[1,2],\"mapping\":{\"11\":{\"control\":true,\"alt\":true,\"shift\":false,\"code\":120},\"10\":{\"control\":true,\"alt\":true,\"shift\":false,\"code\":19}}}";
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
        var expectedAfter = "{\"rotaryControl\":[],\"mapping\":{\"1\":{\"control\":true,\"alt\":false,\"shift\":true,\"code\":12}}}";
        // when
        var after = underTest.addMapping(new MidiEvent(1, 2), new KeyboardStroke(true, false, true, (short) 12));
        var actualOriginal = gson.toJson(underTest);
        var actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);

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