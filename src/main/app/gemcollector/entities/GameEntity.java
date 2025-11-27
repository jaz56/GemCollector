package gemcollector.entities;
import javafx.scene.canvas.GraphicsContext;

public interface GameEntity {
    void render(GraphicsContext gc);
    void update(double deltaTime);
    double getX();//imp pour les detc
    double getY();
}
