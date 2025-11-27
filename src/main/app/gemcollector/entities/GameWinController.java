package gemcollector.entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class GameWinController {

    @FXML
    private Label finalScoreLabel;

    @FXML
    private Button playAgainButton;

    @FXML
    private Button quitButton;

    @FXML
    private ImageView trophyImage;

    private int finalScore;

    /**
     * Initialise le score final à afficher.
     */
    public void setFinalScore(int score) {
        this.finalScore = score;
        finalScoreLabel.setText("Score: " + finalScore);
    }

    @FXML
    public void initialize() {
        // Action bouton Play Again
        playAgainButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gemcollector/entities/GameWinUI.fxml"));
                Stage stage = (Stage) playAgainButton.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                // Si tu veux réinitialiser le jeu, tu peux accéder au controller du GameUI
                GameController controller = loader.getController();
                controller.launchNewTrial();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Action bouton Quit
        quitButton.setOnAction(event -> {
            Stage stage = (Stage) quitButton.getScene().getWindow();
            stage.close();  // ferme la fenêtre
        });
    }
}
