package gemcollector.core;

import gemcollector.entities.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public class Game extends CoreComponent {

    private final Canvas canvas;
    private final GraphicsContext gc;

    private Player player;
    private Enemy enemy;
    private Gem gem;

    private final List<GameEntity> entities = new ArrayList<>();
    private final List<Wall> walls = new ArrayList<>();

    @Override
    public void init() {
        // Ici tu peux initialiser les entités si tu ne l'as pas déjà fait
        // Ou laisser vide si tout est déjà fait dans le constructeur
    }
    @Override
    public void update() {
        // Mettre à jour toutes les entités
        for (GameEntity entity : entities) {
            entity.update(0.016); // deltaTime approximatif pour 60fps
        }

        // Exemple de collision simple
        if (player.getBounds().intersects(gem.getBounds()) && gem.isVisible()) {
            System.out.println("Gem collected!");
            gem.setVisible(false);
        }
    }

    public Game(Canvas canvas, Scene scene) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        try {
            // Créer les murs
            walls.add(new Wall(0, 0, canvas.getWidth(), 20)); // haut
            walls.add(new Wall(0, canvas.getHeight() - 20, canvas.getWidth(), 20)); // bas
            walls.add(new Wall(0, 0, 20, canvas.getHeight())); // gauche
            walls.add(new Wall(canvas.getWidth() - 20, 0, 20, canvas.getHeight())); // droite

            // Créer les entités
            player = new Player(50, 50, 30, 30);
            enemy = new Enemy(200, 100, 30, 30, walls);
            gem = new Gem(300, 200, 20, 20);

            // Ajouter les entités à la liste
            entities.add(player);
            entities.add(enemy);
            entities.add(gem);
            entities.addAll(walls);

        } catch (InvalidPositionException e) {
            System.err.println("Erreur lors de la création d'une entité : " + e.getMessage());
            e.printStackTrace();
        }

        // Contrôle clavier pour le joueur
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> player.move(0, -5);
                case S -> player.move(0, 5);
                case A -> player.move(-5, 0);
                case D -> player.move(5, 0);
            }
        });
    }
    public void render() {  // doit être public
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (GameEntity entity : entities) {
            if (entity instanceof Entity e && e.isVisible()) {
                e.render(gc);
            }
        }
    }

}

