package gemcollector.entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private Button playButton, levelsButton, optionsButton, quitButton;

    // Hover effect pour boutons principaux
    @FXML
    public void handleMouseEnter(MouseEvent event) {
        ((Button) event.getSource()).setStyle("-fx-background-color:#80c1ff; -fx-font-size:18px; -fx-background-radius:12; -fx-text-fill:white;");
    }

    @FXML
    public void handleMouseExit(MouseEvent event) {
        ((Button) event.getSource()).setStyle("-fx-background-color:#4da6ff; -fx-font-size:18px; -fx-background-radius:12; -fx-text-fill:white;");
    }

    // Hover effect pour bouton QUIT
    @FXML
    public void handleMouseEnterQuit(MouseEvent event) {
        ((Button) event.getSource()).setStyle("-fx-background-color:#ff4d4d; -fx-font-size:18px; -fx-background-radius:12; -fx-text-fill:white;");
    }

    @FXML
    public void handleMouseExitQuit(MouseEvent event) {
        ((Button) event.getSource()).setStyle("-fx-background-color:#ff6666; -fx-font-size:18px; -fx-background-radius:12; -fx-text-fill:white;");
    }

    // ⭐ PLAY - Lance le jeu Pac-Man
    @FXML
    public void handlePlayClick() {
        loadGameScene();
    }

    // LEVELS - À implémenter plus tard
    @FXML
    public void handleLevelsClick() {
        System.out.println("🎯 Levels - Coming soon!");
        // TODO: Créer différents niveaux de difficulté
    }

    // ⭐ OPTIONS - Ouvre le menu des options
    @FXML
    public void handleOptionsClick() {
        loadOptionsScene();
    }

    // QUIT
    @FXML
    public void handleQuitClick() {
        System.out.println("👋 Fermeture du jeu...");
        System.exit(0);
    }

    // ⭐ Charge la scène du jeu Pac-Man
    private void loadGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gemcollector/entities/GameUI.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) playButton.getScene().getWindow();
            Scene gameScene = new Scene(root, 800, 600);
            stage.setScene(gameScene);
            stage.setTitle("Pac-Man Tunisien - Sidi Bou Said");

            System.out.println("✅ Jeu lancé!");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du jeu:");
            e.printStackTrace();
        }
    }

    // ⭐ NOUVEAU : Charge la scène des options
    private void loadOptionsScene() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gemcollector/entities/Options.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) optionsButton.getScene().getWindow();
            Scene optionsScene = new Scene(root, 700, 700);
            stage.setScene(optionsScene);
            stage.setTitle("Options - Pac-Man Tunisien");

            System.out.println("⚙️ Menu Options ouvert!");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des options:");
            e.printStackTrace();
        }
    }
}