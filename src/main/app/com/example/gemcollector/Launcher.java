package com.example.gemcollector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // ⭐ Charger le menu principal au démarrage
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gemcollector/entities/MainMenu.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 700, 700);

            primaryStage.setTitle("Pac-Man Tunisien - Menu Principal");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            System.out.println("✅ Menu principal chargé avec succès!");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du menu:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}