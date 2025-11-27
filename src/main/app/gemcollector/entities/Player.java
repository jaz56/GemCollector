package gemcollector.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public non-sealed class Player extends Entity implements Updatable {

    private Image sprite;
    private double speed = 5; // pixels par frame
    private Direction direction = Direction.NONE;
    private List<Wall> walls; // murs pour collision
    private double shakeX = 0;
    private double shakeY = 0;
    private boolean glowEffect = false;

    public enum Direction { UP, DOWN, LEFT, RIGHT, NONE }

    // Constructeur
    public Player(double x, double y, double width, double height) throws InvalidPositionException {
        super(x, y, width, height);

        // Sprite Pac-Man (assure-toi que l'image existe)
        sprite = new Image(getClass().getResourceAsStream("/com/example/gemcollector/entities/images/packman-removebg-preview.png"));
    }

    // Setter et getter de la direction
    public void setDirection(Direction dir) {
        this.direction = dir;
    }

    public Direction getDirection() {
        return direction;
    }

    // Définir les murs pour la collision
    public void setWalls(List<Wall> walls) {
        this.walls = walls;
    }

    @Override
    public void update(double deltaTime) {
        double dx = 0, dy = 0;

        switch (direction) {
            case UP -> dy = -speed;
            case DOWN -> dy = speed;
            case LEFT -> dx = -speed;
            case RIGHT -> dx = speed;
            case NONE -> {}
        }

        // Vérifier collisions avec murs
        if (canMove(dx, dy)) {
            move(dx, dy);
        }
    }

    private boolean canMove(double dx, double dy) {
        if (walls == null) return true;

        // Rectangle futur si on bouge
        Rectangle2D future = new Rectangle2D(position.x() + dx, position.y() + dy,
                size.width(), size.height());

        for (Wall wall : walls) {
            if (future.intersects(wall.getBounds())) {
                return false;
            }
        }
        return true;
    }


    public void setShakeOffset(double x, double y) {
        this.shakeX = x;
        this.shakeY = y;
    }
    public void setGlow(boolean glow) {
        this.glowEffect = glow;
    }
    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;

        double x = position.x() + shakeX;
        double y = position.y() + shakeY;

        // ★ Glow rouge quand touché ★
        if (glowEffect) {
            gc.setFill(Color.rgb(255, 0, 0, 0.4));
            gc.fillOval(x - 10, y - 10, size.width() + 20, size.height() + 20);
        }

        // ★ Flash (clignotement) : visible seulement 1 frame sur 2 ★
        if (glowEffect && Math.random() < 0.5) return;

        // Dessin du sprite Pac-Man
        if (sprite != null) {
            gc.drawImage(sprite, x, y, size.width(), size.height());
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillOval(x, y, size.width(), size.height());
        }
    }


}
