package gemcollector.entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOverController {

    @FXML
    private Button restartButton;

    @FXML
    private Button quitButton;

    @FXML
    private Label gameOverLabel;

    @FXML
    private Label scoreLabel;

    private int finalScore;

    public void setFinalScore(int score) {
        this.finalScore = score;
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + finalScore);
        }
    }
    @FXML
    public void initialize() {
        // Centrer les boutons en code si nécessaire
        restartButton.setLayoutX((800 - restartButton.getPrefWidth()) / 2);
        quitButton.setLayoutX((800 - quitButton.getPrefWidth()) / 2);

        // Action bouton restart
        restartButton.setOnAction(event -> restartGame());

        // Action bouton quit
        quitButton.setOnAction(event -> {
            Stage stage = (Stage) quitButton.getScene().getWindow();
            stage.close();
        });
    }

    /** =======================
     *  Permet au GameController
     *  d'envoyer le score final
     *  ======================= */


    /** ========= RESTART GAME ========= */
    private void restartGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gemcollector/entities/GameUI.fxml"));
            Stage stage = (Stage) restartButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            // Récupérer le controller du jeu pour lancer une nouvelle partie
            GameController gameController = loader.getController();
            gameController.launchNewTrial();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** ========= QUIT GAME ========= */
    private void quitGame() {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
    }
}
