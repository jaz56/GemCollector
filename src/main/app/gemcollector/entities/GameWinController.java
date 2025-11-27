package gemcollector.entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class GameWinController {

    @FXML
    private Label finalScoreLabel;

    @FXML
    private Button playAgainButton;

    @FXML
    private Button quitButton;

    private int finalScore;

    public void setFinalScore(int score) {
        this.finalScore = score;
        finalScoreLabel.setText("Score final : " + finalScore);
    }

    @FXML
    public void initialize() {

        // Bouton Restart -> relancer une nouvelle session propre
        playAgainButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gemcollector/entities/GameUI.fxml"));
                Scene gameScene = new Scene(loader.load());

                // Récupérer le GameController pour réinitialiser la partie
                GameController controller = loader.getController();

                // Lancer une nouvelle session (score = 0, vies = réinitialisées)
                controller.launchNewTrial();

                Stage stage = (Stage) playAgainButton.getScene().getWindow();
                stage.setScene(gameScene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Bouton Quit -> quitter
        quitButton.setOnAction(event -> {
            Stage stage = (Stage) quitButton.getScene().getWindow();
            stage.close();
        });
    }
}
