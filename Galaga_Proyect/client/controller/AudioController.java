package uptc.edu.co.client.controller;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioController {
    private Map<String, Clip> clips = new HashMap<>();
    private FloatControl gainControl;
    private float volume = 1.0f; 

    public void loadSound(String soundName, String path) {
        try {
            URL audioUrl = getClass().getResource(path);
            if (audioUrl != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioUrl);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clips.put(soundName, clip);
                if (gainControl == null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    setVolume(volume);
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

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (gainControl != null) {
            float dB = 20f * (float) Math.log10(this.volume);
            gainControl.setValue(dB);
        }
    }

    public float getVolume() {
        return volume;
    }
}
