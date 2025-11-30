package gemcollector.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.List;
import java.util.Random;

public non-sealed class Enemy extends Entity implements Updatable {

    private final Random random = new Random();

    private double speed = 1.5; // ⭐ Réduit de 2 à 1.5 (plus lent que le player)
    private double dx = 0;
    private double dy = 0;
    private double changeDirectionCooldown = 0;

    private Image sprite;
    private final List<Wall> walls;

    public Enemy(double x, double y, double width, double height, List<Wall> walls)
            throws InvalidPositionException {

        super(x, y, width, height);
        this.walls = walls;

        try {
            sprite = new Image(getClass().getResourceAsStream("/com/example/gemcollector/entities/images/ghost_blue-removebg-preview.png"));
        } catch (Exception e) {
            sprite = null;
        }

        chooseNewDirection();
        changeDirectionCooldown = 1 + random.nextDouble() * 2;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;

        if (sprite != null) {
            gc.drawImage(sprite, position.x(), position.y(), size.width(), size.height());
        } else {
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(position.x(), position.y(), size.width(), size.height());
        }
    }

    @Override
    public void update(double deltaTime) {
        move(dx * speed, dy * speed);

        for (Wall wall : walls) {
            if (getBounds().intersects(wall.getBounds())) {
                move(-dx * speed, -dy * speed);
                chooseNewDirection();
                break;
            }
        }

        changeDirectionCooldown -= deltaTime;
        if (changeDirectionCooldown <= 0) {
            chooseNewDirection();
            changeDirectionCooldown = 1 + random.nextDouble() * 2;
        }
    }

    private void chooseNewDirection() {
        int dir = random.nextInt(4);

        switch (dir) {
            case 0 -> { dx = 0; dy = -1; }
            case 1 -> { dx = 0; dy = 1; }
            case 2 -> { dx = -1; dy = 0; }
            case 3 -> { dx = 1; dy = 0; }
        }
    }
}