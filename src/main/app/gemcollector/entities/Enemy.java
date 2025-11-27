package gemcollector.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.List;
import java.util.Random;

public non-sealed class Enemy extends Entity implements Updatable {

    private final Random random = new Random();

    private double speed = 2;            // vitesse
    private double dx = 0;               // direction X
    private double dy = 0;               // direction Y
    private double changeDirectionCooldown = 0;

    private Image sprite;                // sprite du fantôme
    private final List<Wall> walls;      // murs pour collisions

    public Enemy(double x, double y, double width, double height, List<Wall> walls)
            throws InvalidPositionException {

        super(x, y, width, height);
        this.walls = walls;

        // Sprite fantôme par défaut (assure-toi qu'il existe)
        sprite = new Image(getClass().getResourceAsStream("/com/example/gemcollector/entities/images/ghost_blue-removebg-preview.png"));

        chooseNewDirection();
        changeDirectionCooldown = 1 + random.nextDouble() * 2;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;

        // Si le sprite existe, on l'affiche
        if (sprite != null) {
            gc.drawImage(sprite, position.x(), position.y(), size.width(), size.height());
        } else {
            // fallback si jamais l'image n'est pas trouvée
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(position.x(), position.y(), size.width(), size.height());
        }
    }

    @Override
    public void update(double deltaTime) {

        // Déplacement
        move(dx * speed, dy * speed);

        // Vérification collisions avec les murs
        for (Wall wall : walls) {
            if (getBounds().intersects(wall.getBounds())) {

                // Revenir en arrière
                move(-dx * speed, -dy * speed);

                // Changer de direction
                chooseNewDirection();
                break;
            }
        }

        // Changement automatique de direction
        changeDirectionCooldown -= deltaTime;
        if (changeDirectionCooldown <= 0) {
            chooseNewDirection();
            changeDirectionCooldown = 1 + random.nextDouble() * 2;
        }
    }

    /**
     * Choisir une direction aléatoire parmi UP / DOWN / LEFT / RIGHT.
     */
    private void chooseNewDirection() {
        int dir = random.nextInt(4);

        switch (dir) {
            case 0 -> { dx = 0; dy = -1; } // UP
            case 1 -> { dx = 0; dy = 1; }  // DOWN
            case 2 -> { dx = -1; dy = 0; } // LEFT
            case 3 -> { dx = 1; dy = 0; }  // RIGHT
        }
    }
}
