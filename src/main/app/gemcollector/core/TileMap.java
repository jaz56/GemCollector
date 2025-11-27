package gemcollector.core;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
public class TileMap extends CoreComponent {
    private int[][] grid;
    private int tileSize = 40;

    @Override
    public void init() {
        // Exemple simple : 15x10 grille, 0 = chemin, 1 = mur
        grid = new int[10][15];
        // créer quelques murs
        for (int i = 0; i < 15; i++) {
            grid[0][i] = 1;
            grid[9][i] = 1;
        }
        for (int i = 0; i < 10; i++) {
            grid[i][0] = 1;
            grid[i][14] = 1;
        }
        grid[5][5] = 1;
        grid[5][6] = 1;
    }

    @Override
    public void update() {
        // Rien pour l’instant
    }

    public void render(GraphicsContext gc) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == 1) {
                    gc.setFill(Color.GRAY);
                } else {
                    gc.setFill(Color.LIGHTGREEN);
                }
                gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
    }

    public boolean isWalkable(double x, double y) {
        int col = (int) x / tileSize;
        int row = (int) y / tileSize;

        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length)
            return false;

        return grid[row][col] == 0;
    }
}
