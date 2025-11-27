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
    @FXML private Label gameOverLabel;

    @FXML
    public void initialize() {
        restartButton.setOnAction(e -> restartGame());
        restartButton.setLayoutX((600 - restartButton.getPrefWidth()) / 2);
        gameOverLabel.setLayoutX(0); // car il a prefWidth="600"
    }

    private void restartGame() {
        try {
            // Charger le FXML du jeu principal avec un chemin ABSOLU
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gemcollector/entities/GameUI.fxml"));
            Stage stage = (Stage) restartButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();

            // Optionnel : si tu veux récupérer le contrôleur du jeu
            GameController gameController = loader.getController();
            // Tu peux réinitialiser des valeurs si besoin
            // gameController.resetGame(); // ajouter une méthode si nécessaire

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
