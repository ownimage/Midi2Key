package com.ownimage.midi2key.core;

import com.google.gson.Gson;
import com.ownimage.midi2key.model.MidiAction;
import com.ownimage.midi2key.model.KeyboardAction;
import com.ownimage.midi2key.adapter.AdapterMidiEvent;
import lombok.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
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

    public static Config read(File file) throws IOException {
        String json = Files.readString(file.toPath());
        Gson gson = new Gson();
        return gson.fromJson(json, Config.class);
    }

    public void save(File file) {
        try {
            Gson gson = new Gson();

            String json = gson.toJson(this);

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config addRotaryControl(int control) {
        List<Integer> rc = new ArrayList<>(rotaryControl);
        rc.add(control);
        return new Config(rc, mapping);
    }

    public Config addMapping(MidiAction midiEvent, KeyboardAction keyboardAction) {
        var m = new HashMap<>(mapping);
        m.put(midiEvent.getKey(), keyboardAction);
        return new Config(rotaryControl, m);
    }

    public boolean isRotary(AdapterMidiEvent raw) {
        return rotaryControl.contains(raw.getControl());
    }
}
