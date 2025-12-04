package gemcollector.entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PauseMenuController {

    @FXML private Button resumeButton;
    @FXML private Button restartButton;
    @FXML private Button menuButton;
    @FXML private Button quitButton;
    @FXML private Button soundButton;
    @FXML private Button musicButton;

    private boolean soundOn = true;
    private boolean musicOn = true;
    private GameController gameController;

    @FXML
    private void initialize() {
        // Hover effects pour tous les boutons
        setupHover(resumeButton, "#27ae60", "#2ecc71");
        setupHover(restartButton, "#3498db", "#5dade2");
        setupHover(menuButton, "#95a5a6", "#bdc3c7");
        setupHover(quitButton, "#e74c3c", "#ec7063");
        setupHover(soundButton, "#3498db", "#5dade2");
        setupHover(musicButton, "#9b59b6", "#bb8fce");
    }

    private void setupHover(Button button, String defaultColor, String hoverColor) {
        String baseStyle = "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 12;";

        button.setStyle("-fx-background-color: " + defaultColor + "; " + baseStyle);

        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: " + hoverColor + "; " + baseStyle +
                        "-fx-scale-x: 1.05; -fx-scale-y: 1.05;")
        );

        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: " + defaultColor + "; " + baseStyle)
        );
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    @FXML
    private void resumeAction() {
        // Reprendre le jeu
        if (gameController != null) {
            gameController.resumeGame();
        }
        closeWindow();
        System.out.println("▶️ Jeu repris!");
    }

    @FXML
    private void restartAction() {
        try {
            closeWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gemcollector/entities/GameUI.fxml")
            );
            Parent root = loader.load();

            if (gameController != null) {
                Stage stage = gameController.getStage();
                if (stage != null) {
                    stage.setScene(new Scene(root, 800, 600));
                    System.out.println("🔄 Jeu redémarré!");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du redémarrage:");
            e.printStackTrace();
        }
    }

    @FXML
    private void backToMenuAction() {
        try {
            closeWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gemcollector/entities/MainMenu.fxml")
            );
            Parent root = loader.load();

            if (gameController != null) {
                Stage gameStage = gameController.getStage();
                if (gameStage != null) {
                    gameStage.setScene(new Scene(root, 700, 700));
                    System.out.println("🏠 Retour au menu principal!");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du retour au menu:");
            e.printStackTrace();
        }
    }

    @FXML
    private void quitAction() {
        System.out.println("👋 Fermeture du jeu...");
        System.exit(0);
    }

    @FXML
    private void toggleSound() {
        soundOn = !soundOn;
        soundButton.setText(soundOn ? "🔊" : "🔇");

        if (gameController != null) {
            gameController.toggleSound(soundOn);
        }

        System.out.println("🔊 Son: " + (soundOn ? "ON" : "OFF"));
    }

    @FXML
    private void toggleMusic() {
        musicOn = !musicOn;
        musicButton.setText(musicOn ? "🎵" : "🔇");

        if (gameController != null) {
            gameController.toggleMusic(musicOn);
        }

        System.out.println("🎵 Musique: " + (musicOn ? "ON" : "OFF"));
    }

    private void closeWindow() {
        Stage stage = (Stage) resumeButton.getScene().getWindow();
        stage.close();
    }
}