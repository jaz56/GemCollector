package gemcollector.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public sealed abstract class Entity implements GameEntity , Updatable permits Player, Enemy, Gem , Wall{//ajouter une classe sealed

    protected Position position;
    protected Size size;
    protected boolean visible = true;

    public Entity(double x, double y, double width, double height) throws InvalidPositionException {
        if (x < 0 || y < 0) {
            throw new InvalidPositionException(x, y);
        }

        this.position = new Position(x, y);
        this.size = new Size(width, height);
    }

    @Override
    public double getX() {
        return position.x();
    }

    @Override
    public double getY() {
        return position.y();
    }

    public double getWidth() {
        return size.width();
    }

    public double getHeight() {
        return size.height();
    }

    public void moveTo(Position newPos) {
        this.position = newPos;
    }

    public void move(double dx, double dy) {
        this.position = new Position(position.x() + dx, position.y() + dy);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean v) {
        this.visible = v;
    }
    public Rectangle2D getBounds() {
        return new Rectangle2D(getX(), getY(), getWidth(), getHeight());
    }
    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;
        gc.setFill(Color.BLUE);
        gc.fillRect(position.x(), position.y(), size.width(), size.height());
    }

    @Override
    public void update(double deltaTime) {
        // Exemple simple : affiche la position à chaque update
        System.out.println("Player position: x=" + position.x() + ", y=" + position.y());
    }
}
