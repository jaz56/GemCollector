package gemcollector.entities;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;

/**
 * 🔊 Gestionnaire audio global (Singleton)
 * Gère la musique de fond et les effets sonores du jeu
 */
public class AudioManager {

    private static AudioManager instance;

    private MediaPlayer bgMusicPlayer;
    private AudioClip gameOverSound;
    private AudioClip collectSound;

    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private double volume = 0.3;

    private AudioManager() {
        // Constructeur privé pour Singleton
    }

    /**
     * 🎯 Obtenir l'instance unique du gestionnaire audio
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * 🎵 Initialiser la musique de fond
     */
    public void initBackgroundMusic() {
        try {
            String path = Paths.get("src/main/resources/com/example/gemcollector/entities/sounds/playing-pac-man-6783.mp3")
                    .toUri().toString();
            Media bgMusic = new Media(path);
            bgMusicPlayer = new MediaPlayer(bgMusic);
            bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgMusicPlayer.setVolume(volume);

            if (musicEnabled) {
                bgMusicPlayer.play();
            }

            System.out.println("🎵 Musique de fond initialisée");
        } catch (Exception e) {
            System.err.println("❌ Musique de fond non trouvée: " + e.getMessage());
        }
    }

    /**
     * 🔊 Initialiser les effets sonores
     */
    public void initSoundEffects() {
        try {
            gameOverSound = new AudioClip(
                    getClass().getResource("/com/example/gemcollector/entities/sounds/pacman_fail_glitch_long.wav")
                            .toExternalForm()
            );
            gameOverSound.setVolume(volume);

            System.out.println("🔊 Effets sonores initialisés");
        } catch (Exception e) {
            System.err.println("❌ Effets sonores non trouvés: " + e.getMessage());
        }
    }

    /**
     * ▶️ Démarrer la musique de fond
     */
    public void playBackgroundMusic() {
        if (bgMusicPlayer != null && musicEnabled) {
            bgMusicPlayer.play();
            System.out.println("▶️ Musique démarrée");
        }
    }

    /**
     * ⏸️ Mettre en pause la musique de fond
     */
    public void pauseBackgroundMusic() {
        if (bgMusicPlayer != null) {
            bgMusicPlayer.pause();
            System.out.println("⏸️ Musique en pause");
        }
    }

    /**
     * ⏹️ Arrêter la musique de fond
     */
    public void stopBackgroundMusic() {
        if (bgMusicPlayer != null) {
            bgMusicPlayer.stop();
            System.out.println("⏹️ Musique arrêtée");
        }
    }

    /**
     * 🎮 Jouer le son Game Over
     */
    public void playGameOverSound() {
        if (gameOverSound != null && soundEnabled) {
            gameOverSound.play();
        }
    }

    /**
     * 🎵 Activer/désactiver la musique
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;

        if (bgMusicPlayer != null) {
            if (enabled) {
                bgMusicPlayer.play();
            } else {
                bgMusicPlayer.pause();
            }
        }

        System.out.println("🎵 Musique: " + (enabled ? "ON" : "OFF"));
    }

    /**
     * 🔊 Activer/désactiver les effets sonores
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        System.out.println("🔊 Sons: " + (enabled ? "ON" : "OFF"));
    }

    /**
     * 🔉 Changer le volume global (0.0 à 1.0)
     */
    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));

        if (bgMusicPlayer != null) {
            bgMusicPlayer.setVolume(this.volume);
        }

        if (gameOverSound != null) {
            gameOverSound.setVolume(this.volume);
        }

        System.out.println("🔉 Volume: " + (int)(this.volume * 100) + "%");
    }

    /**
     * 📊 Getters
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public double getVolume() {
        return volume;
    }

    public MediaPlayer getBgMusicPlayer() {
        return bgMusicPlayer;
    }

    /**
     * 🛑 Libérer les ressources
     */
    public void dispose() {
        if (bgMusicPlayer != null) {
            bgMusicPlayer.stop();
            bgMusicPlayer.dispose();
        }
        System.out.println("🛑 AudioManager disposed");
    }
}