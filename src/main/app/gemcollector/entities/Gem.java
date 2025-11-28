package gemcollector.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public final class Gem extends Entity implements Updatable {

    private boolean collected = false;
    private Image sprite;
    private GemType type;

    // Gem types
    public enum GemType {
        HARISSA("/com/example/gemcollector/entities/images/harrissa-removebg-preview.png"),
        BAMBALOUNI("/com/example/gemcollector/entities/images/bambalouni-removebg-preview.png");

        private final String path;

        GemType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    // Constructor
    public Gem(double x, double y, double width, double height, GemType type)
            throws InvalidPositionException {
        super(x, y, width * 2.5, height * 2.5);  // bigger gems

        this.type = type;

        // Load sprite depending on type
        sprite = new Image(getClass().getResourceAsStream(type.getPath()));
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!visible || collected) return;

        if (sprite != null) {
            gc.drawImage(sprite, position.x(), position.y(), size.width(), size.height());
        } else {
            gc.setFill(Color.GREEN);
            gc.fillOval(position.x(), position.y(), size.width(), size.height());
        }
    }

    @Override
    public void update(double deltaTime) {
        // No movement for gems
    }

    public void collect() {
        collected = true;
        setVisible(false);
    }

    public boolean isCollected() {
        return collected;
    }

    public void reset() {
        collected = false;
        setVisible(true);
    }

    public GemType getType() {
        return type;
    }
}
