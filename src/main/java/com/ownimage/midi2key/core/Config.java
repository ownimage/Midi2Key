package com.ownimage.midi2key.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ownimage.midi2key.adapter.AdapterMidiEvent;
import com.ownimage.midi2key.menu.AbstractMenu;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.model.MidiAction;
import lombok.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

@ToString
@AllArgsConstructor
@EqualsAndHashCode
@With
@Builder(toBuilder = true)
public class Config {

    private static Logger logger = Logger.getLogger(Config.class);

    private String filename;
    private TreeSet<Integer> rotaryControls;
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
        var file = getConfigFile();
        if (file.exists()) {
            String json = Files.readString(file.toPath());
            Gson gson = new Gson();
            var config = gson.fromJson(json, Config.class);
            config.filename = getConfigFile().getName();
            return config;
        }
        return this;
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
        TreeSet<Integer> rc = new TreeSet<>(rotaryControls);
        rc.add(control.control());
        return withRotaryControls(rc);
    }

    public Config addMapping(@NotNull MidiAction midiEvent, @NotNull KeyboardAction keyboardAction) {
        var m = new TreeMap<>(mapping);
        m.put(getMappingKey(midiEvent), keyboardAction);
        return withMapping(m);
    }

    public String getMappingKey(@NotNull MidiAction midiEvent) {
        return rotaryControls.contains(midiEvent.control())
                ? midiEvent.control() + "-" + midiEvent.action()
                : String.valueOf(midiEvent.control());
    }

    public Optional<KeyboardAction> map(@NotNull MidiAction midiEvent) {
        return Optional.ofNullable(mapping.get(getMappingKey(midiEvent)));
    }

    // TODO dont like the AdapterMidiEvent leaking here
    public boolean isRotary(AdapterMidiEvent raw) {
        return rotaryControls.contains(raw.getControl());
    }

    public Config addLabel(MidiAction midiAction, String label) {
        var l = new TreeMap<>(labels);
        l.put(midiAction.control(), label);
        return withLabels(l);
    }

    public String getLabel(MidiAction midiAction) {
        return labels.get(midiAction.control());
    }
    public void printConfig() {
        logger.debug("############ printing config");
        mapping.entrySet().forEach(e -> logger.info(mappingToString(e)));
    }

    public String mappingToString(@NotNull Map.Entry<String, KeyboardAction> e) {
        var name = actionNameToString(e.getKey());
        return name + "\t-> " + e.getValue().toPrettyString();
    }

    public String actionNameToString(@NotNull String name) {
        try {
            var pieces = name.split("-");
            var label = labels.getOrDefault(Integer.parseInt(pieces[0]), pieces[0]);
            return label + "-" + pieces[1];
        } catch (Throwable t) {
            return name;
        }
    }
}
