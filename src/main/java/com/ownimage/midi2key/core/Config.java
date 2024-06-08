package com.ownimage.midi2key.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

@ToString
@AllArgsConstructor
@EqualsAndHashCode
@With
@Builder(toBuilder = true)
public class Config {

    private static final Logger logger = Logger.getLogger(Config.class);

    private String filename;
    private final TreeMap<String, KeyboardAction> mapping;
    private final TreeMap<String, String> labels;

    public static ConfigBuilder builder() {
        return builder("config.json");
    }

    public static ConfigBuilder builder(String filename) {
        return new Config(filename, new TreeMap<>(), new TreeMap<>()).toBuilder();
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

    public Config addMapping(@NotNull MidiAction midiEvent, @NotNull KeyboardAction keyboardAction) {
        var m = new TreeMap<>(mapping);
        m.put(midiEvent.getKey(), keyboardAction);
        return withMapping(m);
    }

    public Optional<KeyboardAction> map(@NotNull MidiAction midiEvent) {
        return Optional.ofNullable(mapping.get(midiEvent.getKey()));
    }

    public Config addLabel(MidiAction midiAction, String label) {
        var l = new TreeMap<>(labels);
        l.put(String.valueOf(midiAction.getKey()), label);
        return withLabels(l);
    }

    public String getLabel(MidiAction midiAction) {
        return labels.get(String.valueOf(midiAction.getKey()));
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
            var label = labels.getOrDefault(pieces[0], pieces[0]);
            if (pieces.length == 1)
                return label;
            else
                return label + "-" + pieces[1];
        } catch (Throwable t) {
            return name;
        }
    }
}
