package gemcollector.entities;

public class InvalidPositionException extends EntityException {
    public InvalidPositionException(double x, double y) {
        super("Invalid position: (" + x + ", " + y + ")");
    }
}
