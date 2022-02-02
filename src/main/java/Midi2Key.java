import javax.sound.midi.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Midi2Key
{

    public static void main(String[] args)
    {
        MidiDevice device;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            try {
                device = MidiSystem.getMidiDevice(infos[i]);
                //does the device have any transmitters?
                //if it does, add it to the device list
                System.out.println(infos[i]);

                //get all transmitters
                List<Transmitter> transmitters = device.getTransmitters();
                //and for each transmitter

                for(int j = 0; j<transmitters.size();j++) {
                    //create a new receiver
                    transmitters.get(j).setReceiver(
                            //using my own MidiInputReceiver
                            new MidiInputReceiver(device.getDeviceInfo().toString())
                    );
                }

                Transmitter trans = device.getTransmitter();
                trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));

                //open each device
                device.open();
                //if code gets this far without throwing an exception
                //print a success message
                System.out.println(device.getDeviceInfo()+" Was Opened");


            } catch (MidiUnavailableException e) {}
        }


    }
    //tried to write my own class. I thought the send method handles an MidiEvents sent to it
    public static class MidiInputReceiver implements Receiver {
        public String name;
        public MidiInputReceiver(String name) {
            this.name = name;
        }

        public void send(MidiMessage msg, long timeStamp) {

            byte[] aMsg = msg.getMessage();
            // take the MidiMessage msg and store it in a byte array

            // msg.getLength() returns the length of the message in bytes
            for(int i=0;i<msg.getLength();i++){
                System.out.println(aMsg[i]);
                // aMsg[0] is something, velocity maybe? Not 100% sure.
                // aMsg[1] is the note value as an int. This is the important one.
                // aMsg[2] is pressed or not (0/100), it sends 100 when they key goes down,
                // and 0 when the key is back up again. With a better keyboard it could maybe
                // send continuous values between then for how quickly it's pressed?
                // I'm only using VMPK for testing on the go, so it's either
                // clicked or not.
            }
            System.out.println();
            keypress();
        }

        public void close() {}

        public void keypress() {
            try {
                Robot robot = new Robot();

//                // Simulate a mouse click
//                robot.mousePress(InputEvent.BUTTON1_MASK);
//                robot.mouseRelease(InputEvent.BUTTON1_MASK);

                // Simulate a key press
                robot.keyPress(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_A);

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

}
