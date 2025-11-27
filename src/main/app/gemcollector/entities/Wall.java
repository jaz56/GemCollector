
package gemcollector.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public final class Wall extends Entity {
    public Wall(double x, double y, double width, double height) throws InvalidPositionException {
        super(x, y, width, height);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.DARKBLUE); // couleur du mur
        gc.fillRect(position.x(), position.y(), size.width(), size.height());
    }

    @Override
    public void update(double deltaTime) {
        // les murs ne bougent pas
    }

}
