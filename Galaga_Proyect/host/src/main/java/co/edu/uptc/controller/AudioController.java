package co.edu.uptc.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioController {
    private Map<String, Clip> clips = new HashMap<>();
    private Map<String, FloatControl> gainControls = new HashMap<>();
    private float volume = 1.0f; 

    public void loadSound(String soundName, String path) {
        try {
            URL audioUrl = getClass().getResource(path);
            if (audioUrl != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioUrl);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clips.put(soundName, clip);
                
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControls.put(soundName, gainControl);
                }
            } else {
                System.err.println("No se pudo cargar el sonido: " + path);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error al cargar el sonido " + path + ": " + e.getMessage());
        }
    }

    public void playSound(String soundName) {
        if (clips.containsKey(soundName)) {
            Clip clip = clips.get(soundName);
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        } else {
            System.err.println("Sonido no cargado: " + soundName);
        }
    }

    public void loopSound(String soundName) {
        if (clips.containsKey(soundName)) {
            Clip clip = clips.get(soundName);
            if (clip != null) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } else {
            System.err.println("Sonido no cargado: " + soundName);
        }
    }

    public void stopSound(String soundName) {
        if (clips.containsKey(soundName)) {
            Clip clip = clips.get(soundName);
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.setFramePosition(0); 
            }
        }
    }

    public void setVolume(String soundName, float volume) {
        volume = Math.max(0.0f, Math.min(1.0f, volume)); 
        
        if (gainControls.containsKey(soundName)) {
            FloatControl gainControl = gainControls.get(soundName);
            if (gainControl != null) {
                if (volume == 0.0f) {
                    gainControl.setValue(gainControl.getMinimum());
                } else {
                    float dB = 20f * (float) Math.log10(volume);
                    dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                    gainControl.setValue(dB);
                }
            }
        } else {
            System.err.println("Control de volumen no disponible para: " + soundName);
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        for (String soundName : gainControls.keySet()) {
            setVolume(soundName, this.volume);
        }
    }

    public float getVolume() {
        return volume;
    }

    public float getVolume(String soundName) {
        if (gainControls.containsKey(soundName)) {
            FloatControl gainControl = gainControls.get(soundName);
            if (gainControl != null) {
                float dB = gainControl.getValue();
                return (float) Math.pow(10.0, dB / 20.0);
            }
        }
        return 0.0f;
    }

    public void pauseSound(String soundName) {
        if (clips.containsKey(soundName)) {
            Clip clip = clips.get(soundName);
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
    }

    public void resumeSound(String soundName) {
        if (clips.containsKey(soundName)) {
            Clip clip = clips.get(soundName);
            if (clip != null && !clip.isRunning()) {
                clip.start();
            }
        }
    }

    public boolean isPlaying(String soundName) {
        if (clips.containsKey(soundName)) {
            Clip clip = clips.get(soundName);
            return clip != null && clip.isRunning();
        }
        return false;
    }

    public void dispose() {
        for (Clip clip : clips.values()) {
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.close();
            }
        }
        clips.clear();
        gainControls.clear();
    }
}
