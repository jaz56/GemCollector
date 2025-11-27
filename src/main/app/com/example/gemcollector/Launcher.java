package com.example.gemcollector;

import gemcollector.entities.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Charger le FXML principal du jeu (GameUI)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gemcollector/entities/GameUI.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Gem Collector - Test Complet");
        stage.setScene(scene);
        stage.show();

        // Assurer que le canvas peut recevoir le focus
        scene.getRoot().requestFocus();

        // Récupérer le GameController pour debug si nécessaire
        GameController gameController = loader.getController();

        // OPTIONNEL : debug pour tester GameOver directement
        // gameController.triggerGameOver(); // décommente pour tester l'écran GameOver immédiatement
    }

    public static void main(String[] args) {
        launch();
    }
}
