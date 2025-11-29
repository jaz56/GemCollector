package gemcollector.entities;

import gemcollector.core.TileMap;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameController implements Updatable {

    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label livesLabel;
    @FXML private Button startButton;
    @FXML private Label messageLabel;

    private TileMap tileMap;
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Gem> gems = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();

    private final List<Updatable> entities = new ArrayList<>();

    // ⭐ SOLUTION : Set pour tracker les touches enfoncées
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    private int score = 0;
    private int lives = 3;
    private boolean gameOver = false;

    private Timeline gameLoop;
    private boolean playerHit = false;
    private double hitEffectTime = 0;

    private AudioClip gameOverSound;
    private MediaPlayer bgMusicPlayer;

    private Random random = new Random();
    private static final double CELL_SIZE = 32;

    @FXML
    public void initialize() {
        tileMap = new TileMap(gameCanvas.getWidth(), gameCanvas.getHeight(), CELL_SIZE);
        walls = tileMap.getWalls();

        initEntities();
        render();

        setupStartButton();
        setupSounds();
        setupBackgroundMusic();
    }

    private void setupSounds() {
        try {
            gameOverSound = new AudioClip(
                    getClass().getResource("/com/example/gemcollector/entities/sounds/pacman_fail_glitch_long.wav").toExternalForm()
            );
        } catch (Exception e) {
            System.out.println("Son Game Over non trouvé");
        }
    }

    private void setupBackgroundMusic() {
        try {
            String path = Paths.get("src/main/resources/com/example/gemcollector/entities/sounds/playing-pac-man-6783.mp3").toUri().toString();
            Media bgMusic = new Media(path);
            bgMusicPlayer = new MediaPlayer(bgMusic);
            bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgMusicPlayer.setVolume(0.3);
            bgMusicPlayer.play();
        } catch (Exception e) {
            System.out.println("Musique de fond non trouvée");
        }
    }

    // ⭐ VERSION ALTERNATIVE avec gems TRÈS DENSES comme Pac-Man
// Remplace initEntities() dans GameController si tu veux plus de gems

    private void initEntities() {
        try {
            // Player - position de départ valide
            Position playerStart = tileMap.findValidStartPosition(28, 28);
            player = new Player(playerStart.x(), playerStart.y(), 28, 28);
            player.setWalls(walls);
            entities.add(player);

            // Ennemis - trouver des positions valides aléatoires
            for (int i = 0; i < 4; i++) {
                Position enemyPos = tileMap.findRandomValidPosition(26, 26);
                Enemy enemy = new Enemy(enemyPos.x(), enemyPos.y(), 26, 26, walls);
                enemies.add(enemy);
                entities.add(enemy);
            }

            // ⭐ GEMS DENSES : Placer un gem dans CHAQUE passage libre
            int gemCount = 0;
            int[][] grid = tileMap.getGrid();
            double gemSize = 8; // Petits gems comme Pac-Man

            for (int row = 1; row < tileMap.getRows() - 1; row++) {
                for (int col = 1; col < tileMap.getCols() - 1; col++) {
                    // Si c'est un passage (pas un mur)
                    if (grid[row][col] == 0) {
                        double x = col * tileMap.getCellSize() + (tileMap.getCellSize() - gemSize) / 2;
                        double y = row * tileMap.getCellSize() + (tileMap.getCellSize() - gemSize) / 2;

                        // Vérifier que c'est loin du joueur
                        double distToPlayer = Math.sqrt(
                                Math.pow(x - playerStart.x(), 2) +
                                        Math.pow(y - playerStart.y(), 2)
                        );

                        // Vérifier que c'est loin des ennemis
                        boolean tooCloseToEnemy = false;
                        for (Enemy enemy : enemies) {
                            double distToEnemy = Math.sqrt(
                                    Math.pow(x - enemy.getX(), 2) +
                                            Math.pow(y - enemy.getY(), 2)
                            );
                            if (distToEnemy < 40) {
                                tooCloseToEnemy = true;
                                break;
                            }
                        }

                        // Placer le gem si conditions OK
                        if (distToPlayer > 60 && !tooCloseToEnemy) {
                            Gem.GemType type = (gemCount % 2 == 0) ? Gem.GemType.BAMBALOUNI : Gem.GemType.HARISSA;
                            Gem gem = new Gem(x, y, gemSize, gemSize, type);
                            gems.add(gem);
                            entities.add(gem);
                            gemCount++;
                        }
                    }
                }
            }

            System.out.println("✅ " + gemCount + " gems placés dans le labyrinthe (mode dense)");

        } catch (InvalidPositionException e) {
            e.printStackTrace();
        }
    }

    private void setupStartButton() {
        startButton.setOnAction(event -> {
            startButton.setVisible(false);

            setupKeyboardInput();

            gameCanvas.setFocusTraversable(true);
            gameCanvas.requestFocus();

            startGameLoop();

            javafx.application.Platform.runLater(() -> {
                gameCanvas.requestFocus();
            });
        });
    }

    // ⭐ Gestion du clavier avec Set<KeyCode>
    private void setupKeyboardInput() {
        gameCanvas.setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            updatePlayerDirection();
            event.consume();
        });

        gameCanvas.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            updatePlayerDirection();
            event.consume();
        });
    }

    // Met à jour la direction selon les touches enfoncées
    private void updatePlayerDirection() {
        if (pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.Z)) {
            player.setDirection(Player.Direction.UP);
        } else if (pressedKeys.contains(KeyCode.DOWN) || pressedKeys.contains(KeyCode.S)) {
            player.setDirection(Player.Direction.DOWN);
        } else if (pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.Q)) {
            player.setDirection(Player.Direction.LEFT);
        } else if (pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D)) {
            player.setDirection(Player.Direction.RIGHT);
        } else {
            player.setDirection(Player.Direction.NONE);
        }
    }

    private void startGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.seconds(0.016), e -> update(0.016)));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    @Override
    public void update(double deltaTime) {
        if (gameOver) return;

        // Effet visuel de collision
        if (playerHit) {
            hitEffectTime -= deltaTime;
            if (hitEffectTime <= 0) {
                playerHit = false;
                player.setShakeOffset(0, 0);
                player.setGlow(false);
            } else {
                double offsetX = (Math.random() - 0.5) * 10;
                double offsetY = (Math.random() - 0.5) * 10;
                player.setShakeOffset(offsetX, offsetY);
                player.setGlow(true);
            }
        }

        // Mise à jour du joueur
        player.update(deltaTime);

        // Mise à jour des ennemis
        enemies.forEach(enemy -> enemy.update(deltaTime));

        // Collision player <-> gems
        gems.stream()
                .filter(Gem::isVisible)
                .filter(g -> player.getBounds().intersects(g.getBounds()))
                .forEach(g -> {
                    g.collect();
                    score += 10;
                    scoreLabel.setText("Score: " + score);
                    showMessage("Gem collecté!", 1);
                });

        // Collision player <-> ennemis
        enemies.stream()
                .filter(enemy -> player.getBounds().intersects(enemy.getBounds()))
                .forEach(enemy -> {
                    lives--;
                    livesLabel.setText("Vies: " + lives);
                    showMessage("Touché par un fantôme!", 1.5);

                    playerHit = true;
                    hitEffectTime = 0.8;

                    // ⭐ FIX : Utiliser une position valide pour respawn
                    Position respawnPos = tileMap.findValidStartPosition(28, 28);
                    player.moveTo(respawnPos);
                    System.out.println("🔄 Player respawn à (" + respawnPos.x() + ", " + respawnPos.y() + ")");

                    if (lives <= 0) triggerGameOver();
                });

        // Vérifier victoire
        boolean allGemsCollected = gems.stream().noneMatch(Gem::isVisible);
        if (allGemsCollected && lives > 0) {
            triggerWinGame();
        }

        render();
    }

    private void render() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        tileMap.render(gc);

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

        if (bgMusicPlayer != null) bgMusicPlayer.stop();
        if (gameOverSound != null) gameOverSound.play();

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/gemcollector/entities/GameOverUI.fxml"));
            AnchorPane root = loader.load();

            GameOverController controller = loader.getController();
            controller.setFinalScore(score);

            Scene scene = new Scene(root);
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void triggerWinGame() {
        stopGame();
        gameOver = true;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gemcollector/entities/GameWinUI.fxml"));
            Scene winScene = new Scene(loader.load());

            GameWinController controller = loader.getController();
            controller.setFinalScore(score);

            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(winScene);
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
        score = 0;
        lives = 3;
        gameOver = false;
        pressedKeys.clear(); // ⭐ IMPORTANT : vider les touches

        scoreLabel.setText("Score: 0");
        livesLabel.setText("Vies: 3");

        entities.clear();
        enemies.clear();
        gems.clear();

        tileMap = new TileMap(gameCanvas.getWidth(), gameCanvas.getHeight(), CELL_SIZE);
        walls = tileMap.getWalls();
        initEntities();

        startButton.setVisible(true);
        render();
    }
}