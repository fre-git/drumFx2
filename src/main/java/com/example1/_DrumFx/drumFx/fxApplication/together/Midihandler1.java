package com.example1._DrumFx.drumFx.fxApplication.together;

import javax.sound.midi.*;

import java.io.*;

import static javax.sound.midi.ShortMessage.NOTE_OFF;
import static javax.sound.midi.ShortMessage.NOTE_ON;

public class Midihandler1 {
    private int tempo = 90;
    private float tempoFact;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private final int[] instruments = {35, 42, 46, 38, 49, 39, 50, 47};

    public Midihandler1(Sequencer sequencer, Sequence sequence) {
        this.sequencer = sequencer;
        this.sequence = sequence;
        setUpMidi();
    }

    private void setUpMidi() {
        try {
            track = sequence.createTrack();
            sequencer.setTempoInBPM(tempo);
            tempoFact = sequencer.getTempoFactor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart(boolean[] checkboxState) {
        int[] trackList;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 8; i++) {
            trackList = new int[16];
            int key = instruments[i];

            for (int j = 0; j < 16; j++) {
                trackList[j] = checkboxState[j + 16 * i] ? key : 0;
            }

            makeTrack(trackList, i);
            track.add(makeEvent(ShortMessage.CONTROL_CHANGE, 1, 127, 0, 16 * (i + 1)));
        }

        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 9, 1, 0, 15));
        addTempoEvent(track, tempo);

        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopStartPoint(0);
            sequencer.setLoopEndPoint(sequence.getTickLength() - 1);  // Correctly set the loop endpoint
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            System.out.println("Tempo: " + tempo);

            sequencer.start();
            sequencer.setTempoInBPM(tempo);
            System.out.println(sequencer.getTempoInBPM() + " BPM");
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void changeTempo(float tempoMultiplier) {
        tempo = Math.round(tempo * tempoMultiplier);
        sequencer.setTempoInBPM(tempo);
        updateTempoInSequence(tempo);
    }

    private void makeTrack(int[] list, int instrumentIndex) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];
            if (key != 0) {
                track.add(makeEvent(NOTE_ON, 9, key, 100, i + 16 * instrumentIndex));
                track.add(makeEvent(NOTE_OFF, 9, key, 100, (i + 1) + 16 * instrumentIndex));
            }
        }
    }

    private MidiEvent makeEvent(int cmd, int chnl, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage msg = new ShortMessage();
            msg.setMessage(cmd, chnl, one, two);
            event = new MidiEvent(msg, tick);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return event;
    }

    public void stop() {
        if (sequencer.isRunning()) {
            sequencer.stop();
        }
        sequencer.setTickPosition(0);
    }

    // Save and load methods...

    public void saveSequenceToFile(boolean[] checkboxState, File file) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(checkboxState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean[] loadSequenceFromFile(File file) {
        boolean[] checkboxState = new boolean[128];
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            checkboxState = (boolean[]) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return checkboxState;
    }

    public void exportSequenceToMidiFile(File file) {
        try {
            int[] allowedTypes = MidiSystem.getMidiFileTypes(sequence);
            if (allowedTypes.length == 0) {
                throw new IOException("No available MIDI file types.");
            }
            int type = allowedTypes[0];
            MidiSystem.write(sequence, type, file);
            System.out.println("MIDI file saved successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMidiFromFile(File file) {
        try {
            Sequence loadedSequence = MidiSystem.getSequence(file);
            sequencer.setSequence(loadedSequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } catch (InvalidMidiDataException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addTempoEvent(Track track, int bpm) {
        MetaMessage tempoMessage = new MetaMessage();
        int tempo = 60000000 / bpm;
        byte[] data = {(byte) ((tempo >> 16) & 0xFF), (byte) ((tempo >> 8) & 0xFF), (byte) (tempo & 0xFF)};
        try {
            tempoMessage.setMessage(0x51, data, data.length);
            MidiEvent tempoEvent = new MidiEvent(tempoMessage, 0);
            track.add(tempoEvent);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void updateTempoInSequence(int bpm) {
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                if (event.getMessage() instanceof MetaMessage) {
                    MetaMessage metaMessage = (MetaMessage) event.getMessage();
                    if (metaMessage.getType() == 0x51) {
                        track.remove(event);
                    }
                }
            }
        }
        addTempoEvent(track, bpm);
    }

    private void addEndOfTrackEvent(Track track, long tick) {
        MetaMessage endOfTrackMessage = new MetaMessage();
        try {
            endOfTrackMessage.setMessage(0x2F, new byte[]{}, 0);
            MidiEvent endOfTrackEvent = new MidiEvent(endOfTrackMessage, tick);
            track.add(endOfTrackEvent);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
}

