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

    private List<Integer> rotaryControl;
    private Map<String, KeyboardAction> mapping;

    public static ConfigBuilder builder() {
        return new Config(new ArrayList<>(), new HashMap<>()).toBuilder();
    }

    public static Config read(@NotNull File file) throws IOException {
        String json = Files.readString(file.toPath());
        Gson gson = new Gson();
        return gson.fromJson(json, Config.class);
    }

    public String toJson(){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(this);
    }

    public void save(@NotNull File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(toJson());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config addRotaryControl(@NotNull MidiAction control) {
        List<Integer> rc = new ArrayList<>(rotaryControl);
        rc.add(control.getControl());
        return new Config(rc, mapping);
    }

    public Config addMapping(@NotNull MidiAction midiEvent, @NotNull KeyboardAction keyboardAction) {
        var m = new HashMap<>(mapping);
        m.put(midiEvent.getKey(), keyboardAction);
        return new Config(rotaryControl, m);
    }

    public Optional<KeyboardAction>map(@NotNull MidiAction midiEvent) {
        return Optional.ofNullable(mapping.get(midiEvent.getKey()));
    }

    // TODO dont like the AdapterMidiEvent leaking here
    public boolean isRotary(AdapterMidiEvent raw) {
        return rotaryControl.contains(raw.getControl());
    }
}
