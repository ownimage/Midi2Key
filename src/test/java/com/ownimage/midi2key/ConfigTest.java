package com.ownimage.midi2key;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {

    private Config underTest;

    @BeforeEach
    public void before(){
        underTest = Config.builder().build();
    }

    @Test
    public void defaultConstructorToJSON() {
        // given
        Gson gson = new Gson();
        String expected = "{\"rotaryControl\":[],\"mapping\":{}}";
        // when
        String actual = gson.toJson(underTest);
        // then
        assertEquals(expected, actual);
    }

    @Test
    public void addRotaryControl() {
        // given
        Gson gson = new Gson();
        String expectedOriginal = "{\"rotaryControl\":[],\"mapping\":{}}";
        String expectedAfter = "{\"rotaryControl\":[1],\"mapping\":{}}";
        // when
        Config after = underTest.addRotaryControl(1);
        String actualOriginal = gson.toJson(underTest);
        String actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);
    }

    @Test
    public void addTwoRotaryControls() {
        // given
        Gson gson = new Gson();
        String expectedOriginal = "{\"rotaryControl\":[],\"mapping\":{}}";
        String expectedAfter = "{\"rotaryControl\":[1,2],\"mapping\":{}}";
        // when
        Config after = underTest.addRotaryControl(1).addRotaryControl(2);
        String actualOriginal = gson.toJson(underTest);
        String actualAfter = gson.toJson(after);
        // then
        assertEquals(expectedOriginal, actualOriginal);
        assertEquals(expectedAfter, actualAfter);
    }



    @Test
    public void testSave(@TempDir Path tempDir) throws IOException {
        // given
        String fileName = "config.json";
        File testFile = new File(tempDir.toFile(), fileName);
        Config config = underTest.addRotaryControl(1).addRotaryControl(2);
        String expected = "{\"rotaryControl\":[1,2],\"mapping\":{}}";
        // when
        config.save(testFile);
        // then
        String actual = fileToString(testFile);
        assertEquals(expected, actual);
    }

    @Test
    public void testRead(@TempDir Path tempDir) throws IOException {
        // given
        String fileName = "config.json";
        File testFile = new File(tempDir.toFile(), fileName);;
        String expected = "{\"rotaryControl\":[1,2],\"mapping\":{}}";
        stringToFile(expected,testFile);
        // when
        Config config = Config.read(testFile);
        // then
        Gson gson = new Gson();
        String actual = gson.toJson(config);
        assertEquals(expected, actual);
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