package gemcollector.entities;

import gemcollector.entities.GameOverController ;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameController implements Updatable {

    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label livesLabel;
    @FXML private Button startButton;
    @FXML private Label messageLabel;

    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Gem> gems = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();

    private final List<Updatable> entities = new ArrayList<>();

    private int score = 0;
    private int lives = 3;
    private boolean gameOver = false;

    private Timeline gameLoop;
    // Effet visuel de collision
    private boolean playerHit = false;
    private double hitEffectTime = 0;

    private AudioClip gameOverSound;
    private MediaPlayer bgMusicPlayer;

    @FXML
    public void initialize() {
        initWalls();
        initEntities();
        render();
        setupStartButton();
        gameOverSound = new AudioClip(
                getClass().getResource("/com/example/gemcollector/entities/sounds/pacman_fail_glitch_long.wav").toExternalForm()
        );
        setupBackgroundMusic();
    }
    private void setupBackgroundMusic() {
        try {
            String path = Paths.get("src/main/resources/com/example/gemcollector/entities/sounds/playing-pac-man-6783.mp3").toUri().toString();
            Media bgMusic = new Media(path);
            bgMusicPlayer = new MediaPlayer(bgMusic);
            bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // boucle infinie
            bgMusicPlayer.setVolume(0.3); // ajuster le volume
            bgMusicPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initWalls() {
        try {
            walls.add(new Wall(0, 0, gameCanvas.getWidth(), 20)); // haut
            walls.add(new Wall(0, gameCanvas.getHeight() - 20, gameCanvas.getWidth(), 20)); // bas
            walls.add(new Wall(0, 0, 20, gameCanvas.getHeight())); // gauche
            walls.add(new Wall(gameCanvas.getWidth() - 20, 0, 20, gameCanvas.getHeight())); // droite

            entities.addAll(walls);
        } catch (InvalidPositionException e) {
            e.printStackTrace();
        }
    }

    private void initEntities() {
        try {
            // Player
            player = new Player(120, 180, 40, 40);
            player.setWalls(walls);
            entities.add(player);

            // Ennemis
            Enemy e1 = new Enemy(300, 180, 30, 30, walls);
            Enemy e2 = new Enemy(500, 250, 30, 30, walls);
            Enemy e3 = new Enemy(400, 100, 30, 30, walls);
            enemies.add(e1);
            enemies.add(e2);
            enemies.add(e3);
            entities.addAll(enemies);

            // Gems
            Gem g1 = new Gem(600, 200, 20, 20 , Gem.GemType.BAMBALOUNI);
            Gem g2 = new Gem(150, 400, 20, 20,  Gem.GemType.HARISSA);
            Gem g3 = new Gem(700, 350, 20, 20 ,  Gem.GemType.BAMBALOUNI);
            Gem g4 = new Gem(300, 500, 20, 20 ,  Gem.GemType.HARISSA);

            gems.add(g1);
            gems.add(g2);
            gems.add(g3);
            gems.add(g4);
            entities.addAll(gems);

        } catch (InvalidPositionException e) {
            e.printStackTrace();
        }
    }

    private void setupStartButton() {
        startButton.setOnAction(event -> {
            startGameLoop();
            gameCanvas.setFocusTraversable(true);
            gameCanvas.requestFocus();
            setupKeyboardInput();
        });
    }

    private void setupKeyboardInput() {
        gameCanvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> player.setDirection(Player.Direction.UP);
                case DOWN -> player.setDirection(Player.Direction.DOWN);
                case LEFT -> player.setDirection(Player.Direction.LEFT);
                case RIGHT -> player.setDirection(Player.Direction.RIGHT);
            }
        });

        gameCanvas.setOnKeyReleased(event -> player.setDirection(Player.Direction.NONE));
    }

    private void startGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.seconds(0.016), e -> update(0.016)));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    @Override
    public void update(double deltaTime) {
        if (gameOver) return;

        // Effet visuel si le joueur a été touché
        if (playerHit) {
            hitEffectTime -= deltaTime;
            if (hitEffectTime <= 0) {
                playerHit = false;
                player.setShakeOffset(0, 0);
            } else {
                // Apply shake effect : oscillation rapide
                double offsetX = (Math.random() - 0.5) * 10;
                double offsetY = (Math.random() - 0.5) * 10;
                player.setShakeOffset(offsetX, offsetY);
            }
        }

        // Mise à jour du player
        player.update(deltaTime);

        // Mise à jour des ennemis
        enemies.forEach(enemy -> enemy.update(deltaTime));

        // Collision player <-> gems
        gems.stream()
                .filter(Gem::isVisible)
                .filter(g -> player.getBounds().intersects(g.getBounds()))
                .forEach(g -> {
                    g.collect();
                    score++;
                    scoreLabel.setText("Score: " + score);
                    showMessage("Gem collected!", 1.5);
                });

        // Collision player <-> ennemis
        enemies.stream()
                .filter(enemy -> player.getBounds().intersects(enemy.getBounds()))
                .forEach(enemy -> {
                    lives--;
                    livesLabel.setText("Lives: " + lives);
                    showMessage("Enemy hit!", 1);

                    // ★ Effets visuels déclenchés ici ★
                    playerHit = true;
                    hitEffectTime = 0.6; // durée de l'effet

                    player.moveTo(new Position(120, 180));

                    if (lives <= 0) triggerGameOver();
                });

        // Tous les gems collectés ?
        boolean allGemsCollected = gems.stream().noneMatch(Gem::isVisible);
        if (allGemsCollected) {
            if (lives > 0) triggerWinGame();
            else triggerGameOver();
        }

        render();
    }


    private void triggerWinGame() {
        stopGame();
        gameOver = true;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gemcollector/entities/GameWinUI.fxml"));
            Scene winScene = new Scene(loader.load());

            // Récupérer le contrôleur et lui envoyer le score final
            GameWinController controller = loader.getController();
            controller.setFinalScore(score);

            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(winScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void render() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        entities.stream()
                .filter(e -> e instanceof Entity entity && entity.isVisible())
                .forEach(e -> ((Entity) e).render(gc));
    }

    private void stopGame() {
        if (gameLoop != null) gameLoop.stop();
    }

    private void triggerGameOver() {
        stopGame();
        gameOver = true;

        // 🔥 Stopper la musique de fond
        if (bgMusicPlayer != null) {
            bgMusicPlayer.stop();
        }

        // 🔊 Jouer le son d'échec
        if (gameOverSound != null) {
            gameOverSound.play();
        }

        try {
            // Charger le FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/gemcollector/entities/GameOverUI.fxml"));
            AnchorPane root = loader.load();

            // Récupérer le contrôleur et passer le score final
            GameOverController controller = loader.getController();
            controller.setFinalScore(score);

            // Créer la scène et l'afficher
            Scene scene = new Scene(root);
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void showMessage(String message, double durationSeconds) {
        messageLabel.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(durationSeconds), e -> messageLabel.setText("")));
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void launchNewTrial() {
        if (lives <= 0) return;

        score = 0;
        scoreLabel.setText("Score: " + score);
        player.moveTo(new Position(120, 180));

        gems.forEach(g -> g.setVisible(true));

        showMessage("Nouvelle épreuve !", 2);
        startGameLoop();
    }
}
