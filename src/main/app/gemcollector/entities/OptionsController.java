package gemcollector.entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class OptionsController {

    @FXML private Button btnMusic;
    @FXML private Button btnSound;
    @FXML private Button btnBack;
    @FXML private Button btnReset;
    @FXML private Slider sliderVolume;
    @FXML private Label volumeLabel;
    @FXML private Label statsLabel;

    private boolean musicOn = true;
    private boolean soundOn = true;

    private AudioManager audioManager;

    private final String btnOnStyle = "-fx-background-radius:12; -fx-background-color:#27ae60; " +
            "-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 0, 2);";

    private final String btnOffStyle = "-fx-background-radius:12; -fx-background-color:#e74c3c; " +
            "-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 0, 2);";

    private final String btnHoverOn = "-fx-background-radius:12; -fx-background-color:#2ecc71; " +
            "-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 7, 0.4, 0, 3);";

    private final String btnHoverOff = "-fx-background-radius:12; -fx-background-color:#ec7063; " +
            "-fx-text-fill:white; -fx-font-size:18px; -fx-font-weight:bold; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 7, 0.4, 0, 3);";

    @FXML
    private void initialize() {
        // ⭐ Récupérer l'instance AudioManager
        audioManager = AudioManager.getInstance();

        // ⭐ Synchroniser avec l'état actuel
        musicOn = audioManager.isMusicEnabled();
        soundOn = audioManager.isSoundEnabled();

        // Appliquer les styles initiaux
        btnMusic.setText(musicOn ? "ON" : "OFF");
        btnSound.setText(soundOn ? "ON" : "OFF");
        updateMusicButtonStyle();
        updateSoundButtonStyle();

        // Configurer le slider avec le volume actuel
        sliderVolume.setValue(audioManager.getVolume() * 100);
        updateVolumeLabel();

        // Listener pour le slider (mise à jour en temps réel)
        sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateVolumeLabel();
        });

        // Charger et afficher les statistiques
        loadStatistics();

        // Hover effects pour les boutons
        setupHoverToggle(btnMusic);
        setupHoverToggle(btnSound);
        setupHover(btnBack, "#3498db", "#5dade2");
        setupHover(btnReset, "#e74c3c", "#ec7063");
    }

    private void setupHoverToggle(Button button) {
        button.setOnMouseEntered(e -> {
            if (button.getText().equals("ON")) {
                button.setStyle(btnHoverOn);
            } else {
                button.setStyle(btnHoverOff);
            }
        });

        button.setOnMouseExited(e -> {
            if (button.getText().equals("ON")) {
                button.setStyle(btnOnStyle);
            } else {
                button.setStyle(btnOffStyle);
            }
        });
    }

    private void setupHover(Button button, String defaultColor, String hoverColor) {
        String baseStyle = "-fx-font-size:16px; -fx-background-radius:12; -fx-text-fill:white; " +
                "-fx-font-weight:bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 0, 2);";

        button.setStyle("-fx-background-color:" + defaultColor + "; " + baseStyle);
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color:" + hoverColor + "; " + baseStyle)
        );
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color:" + defaultColor + "; " + baseStyle)
        );
    }

    @FXML
    private void toggleMusic() {
        musicOn = !musicOn;
        btnMusic.setText(musicOn ? "ON" : "OFF");
        updateMusicButtonStyle();

        // ⭐ Appliquer le changement via AudioManager
        audioManager.setMusicEnabled(musicOn);

        System.out.println("🎵 Musique: " + (musicOn ? "ON" : "OFF"));
    }

    @FXML
    private void toggleSound() {
        soundOn = !soundOn;
        btnSound.setText(soundOn ? "ON" : "OFF");
        updateSoundButtonStyle();

        // ⭐ Appliquer le changement via AudioManager
        audioManager.setSoundEnabled(soundOn);

        System.out.println("🔊 Sons: " + (soundOn ? "ON" : "OFF"));
    }

    private void updateMusicButtonStyle() {
        btnMusic.setStyle(musicOn ? btnOnStyle : btnOffStyle);
    }

    private void updateSoundButtonStyle() {
        btnSound.setStyle(soundOn ? btnOnStyle : btnOffStyle);
    }

    @FXML
    private void changeVolume() {
        double volume = sliderVolume.getValue() / 100.0;
        updateVolumeLabel();

        // ⭐ Appliquer le volume via AudioManager
        audioManager.setVolume(volume);

        System.out.println("🔉 Volume: " + (int)sliderVolume.getValue() + "%");
    }

    private void updateVolumeLabel() {
        if (volumeLabel != null) {
            int vol = (int) sliderVolume.getValue();
            volumeLabel.setText("Volume: " + vol + "%");

            // Changer la couleur selon le volume
            if (vol == 0) {
                volumeLabel.setStyle("-fx-font-size:16px; -fx-text-fill:#e74c3c; -fx-font-weight:bold;");
            } else if (vol < 30) {
                volumeLabel.setStyle("-fx-font-size:16px; -fx-text-fill:#f39c12;");
            } else if (vol < 70) {
                volumeLabel.setStyle("-fx-font-size:16px; -fx-text-fill:#27ae60;");
            } else {
                volumeLabel.setStyle("-fx-font-size:16px; -fx-text-fill:#e74c3c; -fx-font-weight:bold;");
            }
        }
    }

    private void loadStatistics() {
        try {
            String savePath = "game_saves/player_stats.dat";

            if (Files.exists(Paths.get(savePath))) {
                String content = Files.readString(Paths.get(savePath));

                // Parser les statistiques
                int highScore = 0;
                int totalGems = 0;
                int gamesPlayed = 0;
                int totalDeaths = 0;

                for (String line : content.split("\n")) {
                    if (line.startsWith("#") || line.trim().isEmpty()) continue;

                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        int value = Integer.parseInt(parts[1].trim());

                        switch (key) {
                            case "highScore" -> highScore = value;
                            case "totalGems" -> totalGems = value;
                            case "gamesPlayed" -> gamesPlayed = value;
                            case "totalDeaths" -> totalDeaths = value;
                        }
                    }
                }

                // Afficher les stats
                String stats = String.format(
                        "🏆 Meilleur Score: %d\n" +
                                "💎 Gems Collectés: %d\n" +
                                "🎮 Parties Jouées: %d\n" +
                                "💀 Morts Totales: %d",
                        highScore, totalGems, gamesPlayed, totalDeaths
                );

                statsLabel.setText(stats);

            } else {
                statsLabel.setText("Aucune statistique disponible.\nJouez une partie pour commencer !");
            }

        } catch (IOException | NumberFormatException e) {
            statsLabel.setText("❌ Erreur de chargement des statistiques");
            e.printStackTrace();
        }
    }

    @FXML
    private void resetStats() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Réinitialiser les Statistiques");
        alert.setHeaderText("Êtes-vous sûr ?");
        alert.setContentText("Cette action supprimera TOUTES vos statistiques de manière définitive !\n\n" +
                "🏆 Meilleur score\n" +
                "💎 Gems collectés\n" +
                "🎮 Parties jouées\n" +
                "💀 Morts totales");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer les fichiers de sauvegarde
                Files.deleteIfExists(Paths.get("game_saves/player_stats.dat"));
                Files.deleteIfExists(Paths.get("game_saves/highscore.dat"));

                System.out.println("🗑️ Statistiques réinitialisées!");

                // Recharger l'affichage
                statsLabel.setText("Statistiques réinitialisées.\nJouez une partie pour commencer !");

                // Afficher confirmation
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succès");
                success.setHeaderText("Statistiques réinitialisées");
                success.setContentText("Toutes vos statistiques ont été supprimées avec succès !");
                success.showAndWait();

            } catch (IOException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText("Échec de la réinitialisation");
                error.setContentText("Impossible de supprimer les fichiers de statistiques.");
                error.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gemcollector/entities/MainMenu.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 700));

            System.out.println("🏠 Retour au menu principal");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Impossible de charger MainMenu.fxml");
        }
    }

    // ⭐ Getters pour l'état audio
    public boolean isMusicOn() {
        return musicOn;
    }

    public boolean isSoundOn() {
        return soundOn;
    }
}