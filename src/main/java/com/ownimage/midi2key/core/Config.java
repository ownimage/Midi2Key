package com.ownimage.midi2key.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ownimage.midi2key.model.MidiAction;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.adapter.AdapterMidiEvent;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@ToString
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Config {

    public final static String DEFAULT_FILENAME = "config.json";

    private TreeSet<Integer> rotaryControl;
    private TreeMap<String, KeyboardAction> mapping;
    private TreeMap<Integer, String> labels;

    public static ConfigBuilder builder() {
        return new Config(new TreeSet<>(), new TreeMap<>(), new TreeMap<>()).toBuilder();
    }

    public static Config open(@NotNull File file) throws IOException {
        String json = Files.readString(file.toPath());
        Gson gson = new Gson();
        return gson.fromJson(json, Config.class);
    }

    public String toJson(boolean prettyPrint){
        Gson gson = prettyPrint
        ? new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        : new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(this);
    }

    public void save(@NotNull File file, boolean prettyPrint) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(toJson(prettyPrint));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config addRotaryControl(@NotNull MidiAction control) {
        TreeSet<Integer> rc = new TreeSet<>(rotaryControl);
        rc.add(control.getControl());
        return new Config(rc, mapping, labels);
    }

    public Config addMapping(@NotNull MidiAction midiEvent, @NotNull KeyboardAction keyboardAction) {
        var m = new TreeMap<>(mapping);
        m.put(midiEvent.getKey(), keyboardAction);
        return new Config(rotaryControl, m, labels);
    }

    public Optional<KeyboardAction>map(@NotNull MidiAction midiEvent) {
        return Optional.ofNullable(mapping.get(midiEvent.getKey()));
    }

    // TODO dont like the AdapterMidiEvent leaking here
    public boolean isRotary(AdapterMidiEvent raw) {
        return rotaryControl.contains(raw.getControl());
    }

    public Config addLabel(MidiAction midiAction, String label) {
        var l = new TreeMap<>(labels);
        l.put(midiAction.getControl(), label);
        return new Config(rotaryControl, mapping, l);
    }
}
