package gemcollector.entities;

import javafx.application.Platform;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 💾 Gestionnaire de sauvegarde automatique avec threads
 * Sauvegarde périodiquement les statistiques du jeu en arrière-plan
 */
public class GameSaveManager {

    private final ScheduledExecutorService saveExecutor;
    private final ExecutorService loadExecutor;
    private final AtomicBoolean isSaving = new AtomicBoolean(false);

    private static final String SAVE_DIR = "game_saves";
    private static final String SAVE_FILE = "player_stats.dat";
    private static final String HIGH_SCORE_FILE = "highscore.dat";

    // Statistiques du joueur (thread-safe)
    private final AtomicInteger currentScore = new AtomicInteger(0);
    private final AtomicInteger highScore = new AtomicInteger(0);
    private final AtomicInteger totalGems = new AtomicInteger(0);
    private final AtomicInteger gamesPlayed = new AtomicInteger(0);
    private final AtomicInteger totalDeaths = new AtomicInteger(0);

    private GameController gameController;

    public GameSaveManager(GameController controller) {
        this.gameController = controller;

        // Thread pool pour les sauvegardes périodiques
        saveExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "GameSave-Thread");
            t.setDaemon(true); // Ne bloque pas la fermeture de l'app
            return t;
        });

        // Thread pool pour les chargements asynchrones
        loadExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "GameLoad-Thread");
            t.setDaemon(true);
            return t;
        });

        // Créer le dossier de sauvegarde
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
        } catch (IOException e) {
            System.err.println("❌ Impossible de créer le dossier de sauvegarde: " + e.getMessage());
        }
    }

    /**
     * 🚀 Démarre la sauvegarde automatique toutes les 10 secondes
     */
    public void startAutoSave() {
        saveExecutor.scheduleAtFixedRate(
                this::saveGameStats,
                10, // Délai initial (secondes)
                10, // Intervalle (secondes)
                TimeUnit.SECONDS
        );
        System.out.println("💾 Auto-save activé (toutes les 10s)");
    }

    /**
     * 💾 Sauvegarde les statistiques du jeu (thread séparé)
     */
    private void saveGameStats() {
        if (isSaving.get()) {
            System.out.println("⏳ Sauvegarde déjà en cours, on attend...");
            return;
        }

        isSaving.set(true);

        try {
            Path savePath = Paths.get(SAVE_DIR, SAVE_FILE);

            // Préparer les données à sauvegarder
            StringBuilder data = new StringBuilder();
            data.append("# Pac-Man Tunisien - Statistiques\n");
            data.append("# Dernière sauvegarde: ").append(getCurrentTimestamp()).append("\n");
            data.append("currentScore=").append(currentScore.get()).append("\n");
            data.append("highScore=").append(highScore.get()).append("\n");
            data.append("totalGems=").append(totalGems.get()).append("\n");
            data.append("gamesPlayed=").append(gamesPlayed.get()).append("\n");
            data.append("totalDeaths=").append(totalDeaths.get()).append("\n");

            // Écriture asynchrone sur disque
            Files.writeString(savePath, data.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("✅ Sauvegarde réussie - Score: " + currentScore.get() +
                    " | High Score: " + highScore.get());

        } catch (IOException e) {
            System.err.println("❌ Erreur de sauvegarde: " + e.getMessage());
        } finally {
            isSaving.set(false);
        }
    }

    /**
     * 📂 Charge les statistiques depuis le fichier (asynchrone)
     */
    public void loadGameStatsAsync(Runnable onComplete) {
        loadExecutor.submit(() -> {
            try {
                Path savePath = Paths.get(SAVE_DIR, SAVE_FILE);

                if (Files.exists(savePath)) {
                    String content = Files.readString(savePath);

                    // Parser les données
                    for (String line : content.split("\n")) {
                        if (line.startsWith("#") || line.trim().isEmpty()) continue;

                        String[] parts = line.split("=");
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            int value = Integer.parseInt(parts[1].trim());

                            switch (key) {
                                case "currentScore" -> currentScore.set(value);
                                case "highScore" -> highScore.set(value);
                                case "totalGems" -> totalGems.set(value);
                                case "gamesPlayed" -> gamesPlayed.set(value);
                                case "totalDeaths" -> totalDeaths.set(value);
                            }
                        }
                    }

                    System.out.println("✅ Statistiques chargées - High Score: " + highScore.get());
                } else {
                    System.out.println("ℹ️ Aucune sauvegarde trouvée, nouveau joueur !");
                }

                // Callback sur le thread JavaFX
                if (onComplete != null) {
                    Platform.runLater(onComplete);
                }

            } catch (IOException | NumberFormatException e) {
                System.err.println("❌ Erreur de chargement: " + e.getMessage());
            }
        });
    }

    /**
     * 🏆 Sauvegarde le meilleur score (immédiat)
     */
    public void saveHighScore(int score) {
        if (score > highScore.get()) {
            highScore.set(score);

            // Sauvegarde immédiate du high score
            CompletableFuture.runAsync(() -> {
                try {
                    Path path = Paths.get(SAVE_DIR, HIGH_SCORE_FILE);
                    Files.writeString(path, String.valueOf(score));
                    System.out.println("🏆 Nouveau record sauvegardé: " + score);
                } catch (IOException e) {
                    System.err.println("❌ Erreur sauvegarde high score: " + e.getMessage());
                }
            });
        }
    }

    /**
     * 📊 Met à jour les statistiques (thread-safe)
     */
    public void updateStats(int score, int gemsCollected, boolean gameOver, boolean death) {
        currentScore.set(score);
        totalGems.addAndGet(gemsCollected);

        if (gameOver) {
            gamesPlayed.incrementAndGet();
            saveHighScore(score);
        }

        if (death) {
            totalDeaths.incrementAndGet();
        }
    }

    /**
     * 🔄 Réinitialise le score actuel
     */
    public void resetCurrentScore() {
        currentScore.set(0);
    }

    /**
     * 📈 Getters thread-safe
     */
    public int getCurrentScore() { return currentScore.get(); }
    public int getHighScore() { return highScore.get(); }
    public int getTotalGems() { return totalGems.get(); }
    public int getGamesPlayed() { return gamesPlayed.get(); }
    public int getTotalDeaths() { return totalDeaths.get(); }

    /**
     * 🕒 Timestamp actuel
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 🛑 Arrêt propre des threads
     */
    public void shutdown() {
        System.out.println("🛑 Arrêt du système de sauvegarde...");

        // Sauvegarde finale
        saveGameStats();

        // Arrêt des executors
        saveExecutor.shutdown();
        loadExecutor.shutdown();

        try {
            if (!saveExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                saveExecutor.shutdownNow();
            }
            if (!loadExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                loadExecutor.shutdownNow();
            }
            System.out.println("✅ Système de sauvegarde arrêté proprement");
        } catch (InterruptedException e) {
            saveExecutor.shutdownNow();
            loadExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 📋 Affiche les statistiques
     */
    public void printStats() {
        System.out.println("\n📊 === STATISTIQUES DU JOUEUR ===");
        System.out.println("🎯 Score actuel: " + currentScore.get());
        System.out.println("🏆 Meilleur score: " + highScore.get());
        System.out.println("💎 Gems collectés: " + totalGems.get());
        System.out.println("🎮 Parties jouées: " + gamesPlayed.get());
        System.out.println("💀 Morts totales: " + totalDeaths.get());
        System.out.println("================================\n");
    }
}