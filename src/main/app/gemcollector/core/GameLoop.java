package gemcollector.core;
import javafx.animation.AnimationTimer;

public class GameLoop extends CoreComponent {
    private Game game;

    public GameLoop(Game game) {
        this.game = game;
    }

    @Override
    public void init() {
        // Rien à initialiser pour le GameLoop lui-même
    }

    @Override
    public void update() {
        // Appelé chaque frame
        game.update();
        game.render();
    }

    public void start() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        }.start();
    }
}
