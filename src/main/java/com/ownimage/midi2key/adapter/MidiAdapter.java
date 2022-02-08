package com.ownimage.midi2key.adapter;

import com.ownimage.midi2key.core.MidiActionReceiver;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MidiAdapter implements Receiver {

    private static final int ROTARY_CONTROL_START = 1000;
    private static Logger logger = Logger.getLogger(MidiAdapter.class);
    private MidiActionReceiver midiEventReceiver;
    private HashMap<Integer, Integer> previousValues;
    private List<MidiDevice> devices = new ArrayList<>();

    public MidiAdapter(
            @NotNull MidiActionReceiver midiEventReceiver,
            boolean start) {
        this.midiEventReceiver = midiEventReceiver;
        this.previousValues = new HashMap<>();
        if (start) start();
    }

    private void start() {
        MidiDevice device;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            try {
                device = MidiSystem.getMidiDevice(infos[i]);
                logger.debug(infos[i]);

                List<Transmitter> transmitters = device.getTransmitters();
                for (int j = 0; j < transmitters.size(); j++) {
                    //create a new receiver
                    transmitters.get(j).setReceiver(this);
                }

                Transmitter trans = device.getTransmitter();
                trans.setReceiver(this);

                device.open();
                devices.add(device);
                logger.debug(device.getDeviceInfo() + " Was Opened");
            } catch (MidiUnavailableException e) {
            }
        }
    }

    public void stop() {
        devices.forEach(d -> d.close());
    }

    public void send(MidiMessage msg, long timeStamp) {
        logger.debug("send");
        byte[] aMsg = msg.getMessage();
        // aMsg[0] is something, velocity maybe? Not 100% sure.
        // aMsg[1] is the note value as an int. This is the important one.
        // aMsg[2] is pressed or not (0/100), it sends 100 when they key goes down,
        // and 0 when the key is back up again. With a better keyboard it could maybe
        // send continuous values between then for how quickly it's pressed?
        // I'm only using VMPK for testing on the go, so it's either
        // clicked or not.
        var rotary = aMsg[0] == -70;
        var control = Integer.parseInt(String.valueOf(aMsg[1])) + (rotary ? ROTARY_CONTROL_START : 0);
        var value = Integer.parseInt(String.valueOf(aMsg[2]));
        var midiEvent = new AdapterMidiEvent(control, value);
        logger.debug(String.format("MidiAdapter::send gets %s, %s", aMsg[0], midiEvent));
        var previousValue = previousValues.put(control, value);
        var actionableMidiEvent = midiEvent.toMidiAction(previousValue, rotary);
        actionableMidiEvent.ifPresent(e -> midiEventReceiver.receive(rotary, e));
    }

    public void close() {
    }
}

