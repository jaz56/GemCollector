package gemcollector.entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GameOverController {

    @FXML
    private Label finalScoreLabel;

    public void setFinalScore(int score) {
        if (finalScoreLabel != null) {
            finalScoreLabel.setText("Score Final: " + score);
        }
    }

    @FXML
    public void handleRestartClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gemcollector/entities/GameUI.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) finalScoreLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));

            System.out.println("🔄 Jeu redémarré!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ⭐ NOUVEAU : Retour au menu principal
    @FXML
    public void handleBackToMenuClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gemcollector/entities/MainMenu.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) finalScoreLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 700, 700));

            System.out.println("🏠 Retour au menu principal!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}