package com.ownimage.midi2key.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ownimage.midi2key.adapter.AdapterMidiEvent;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

@ToString
@AllArgsConstructor
@EqualsAndHashCode
@With
@Builder(toBuilder = true)
public class Config {

    private String filename;
    private TreeSet<Integer> rotaryControl;
    private TreeMap<String, KeyboardAction> mapping;
    private TreeMap<Integer, String> labels;

    public static ConfigBuilder builder() {
        return builder("config.json");
    }

    public static ConfigBuilder builder(String filename) {
        return new Config(filename, new TreeSet<>(), new TreeMap<>(), new TreeMap<>()).toBuilder();
    }

    @SneakyThrows
    public Config open() {
        String json = Files.readString(getConfigFile().toPath());
        Gson gson = new Gson();
        var config = gson.fromJson(json, Config.class);
        config.filename = getConfigFile().getName();
        return config;
    }

    public Config filename(String filename) {
        return toBuilder().filename(filename).build();
    }

    public File getConfigFile() {
        return new File(filename);
    }

    public String toJson(boolean prettyPrint) {
        Gson gson = prettyPrint
                ? new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
                : new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(this);
    }

    @SneakyThrows
    public void save(boolean prettyPrint) {
        var filename = getConfigFile().getName();
        FileWriter fileWriter = new FileWriter(getConfigFile());
        fileWriter.write(withFilename(filename).toJson(prettyPrint));
        fileWriter.flush();
        fileWriter.close();
    }

    public Config addRotaryControl(@NotNull MidiAction control) {
        TreeSet<Integer> rc = new TreeSet<>(rotaryControl);
        rc.add(control.getControl());
        return withRotaryControl(rc);
    }

    public Config addMapping(@NotNull MidiAction midiEvent, @NotNull KeyboardAction keyboardAction) {
        var m = new TreeMap<>(mapping);
        m.put(midiEvent.getKey(), keyboardAction);
        return withMapping(m);
    }

    public Optional<KeyboardAction> map(@NotNull MidiAction midiEvent) {
        return Optional.ofNullable(mapping.get(midiEvent.getKey()));
    }

    // TODO dont like the AdapterMidiEvent leaking here
    public boolean isRotary(AdapterMidiEvent raw) {
        return rotaryControl.contains(raw.getControl());
    }

    public Config addLabel(MidiAction midiAction, String label) {
        var l = new TreeMap<>(labels);
        l.put(midiAction.getControl(), label);
        return withLabels(l);
    }
}
