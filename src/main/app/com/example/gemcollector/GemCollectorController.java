package com.example.gemcollector;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;

public class GemCollectorController {

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    public Canvas getGameCanvas() {
        return gameCanvas;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    @FXML
    public void initialize() {
        // Optional initialization
        scoreLabel.setText("Score: 0");
    }
}
