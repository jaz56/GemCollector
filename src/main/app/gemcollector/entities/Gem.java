package gemcollector.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public final class Gem extends Entity implements Updatable {

    private boolean collected = false; // indique si la gemme a été collectée
    private Image sprite;

    // Constructeur
    public Gem(double x, double y, double width, double height) throws InvalidPositionException {
        super(x, y, width, height);

        // Charger l'image de la gemme
        sprite = new Image(getClass().getResourceAsStream("/com/example/gemcollector/entities/images/gem-removebg-preview.png"));
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!visible || collected) return;

        if (sprite != null) {
            gc.drawImage(sprite, position.x(), position.y(), size.width(), size.height());
        } else {
            // fallback si image non trouvée
            gc.setFill(Color.GREEN);
            gc.fillOval(position.x(), position.y(), size.width(), size.height());
        }
    }

    @Override
    public void update(double deltaTime) {
        // Les gemmes ne bougent pas
        // Optionnel : debug
        // if (!collected) System.out.println("Gem at x=" + position.x() + ", y=" + position.y());
    }

    public void collect() {
        collected = true;
        setVisible(false); // La gemme disparaît après collecte
    }

    public boolean isCollected() {
        return collected;
    }

    // Remettre la gemme visible (utile pour relancer une nouvelle épreuve)
    public void reset() {
        collected = false;
        setVisible(true);
    }
}
