package com.example1._DrumFx.drumFx.fxApplication.finalEstForm.model;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MidiToAudioRecorder {
    private Synthesizer synthesizer;
    private Sequencer sequencer;
    private TargetDataLine line;

    public MidiToAudioRecorder(Sequencer sequencer) throws MidiUnavailableException {
        this.sequencer = sequencer;
        this.synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
    }

    public void startRecording(String outputFilePath) throws LineUnavailableException, IOException {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        Thread recordingThread = new Thread(() -> {
            try (AudioInputStream ais = new AudioInputStream(line)) {
                File file = new File(outputFilePath);
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        recordingThread.start();
    }

    public void stopRecording() {
        line.stop();
        line.close();
    }
}
