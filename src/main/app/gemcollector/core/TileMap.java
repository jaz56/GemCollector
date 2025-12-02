package gemcollector.core;

import gemcollector.entities.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TileMap extends CoreComponent {

    private final List<Wall> walls;
    private final double cellSize;
    private final int rows;
    private final int cols;
    private int[][] grid;
    private Random random = new Random();

    private static final Color SIDI_BOU_SAID_BLUE = Color.rgb(52, 152, 219);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_BLUE = Color.rgb(174, 214, 241);
    private static final Color SAND_BEIGE = Color.rgb(245, 240, 230);

    public TileMap(double canvasWidth, double canvasHeight, double cellSize) {
        this.cellSize = cellSize;
        this.rows = (int) (canvasHeight / cellSize);
        this.cols = (int) (canvasWidth / cellSize);
        this.walls = new ArrayList<>();

        init();
    }

    @Override
    public void init() {
        createMazePattern();

        convertGridToWalls();
    }

    @Override
    public void update() {
    }


    private void createMazePattern() {
        int[][] mazePattern = { //0 = passage, 1 = mur
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,1,1,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,1,1,0,1},
                {1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
                {1,0,1,0,1,1,0,1,1,0,1,1,1,1,1,0,1,1,0,1,1,0,1,0,1},
                {1,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,1},
                {1,0,1,1,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,1,1,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,1,1,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,1,1,0,1},
                {1,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,1},
                {1,0,1,0,1,1,0,1,1,0,1,1,1,1,1,0,1,1,0,1,1,0,1,0,1},
                {1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
                {1,0,1,1,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,1,1,0,1},
                {1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };

        grid = new int[rows][cols];

        for (int row = 0; row < mazePattern.length && row < rows; row++) {
            for (int col = 0; col < mazePattern[row].length && col < cols; col++) {
                grid[row][col] = mazePattern[row][col];
            }
        }
    }


    private void convertGridToWalls() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] == 1) {
                    try {
                        Wall wall = new Wall(
                                col * cellSize,
                                row * cellSize,
                                cellSize,
                                cellSize
                        );
                        walls.add(wall);
                    } catch (InvalidPositionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void render(GraphicsContext gc) {
        gc.setFill(SAND_BEIGE);
        gc.fillRect(0, 0, cols * cellSize, rows * cellSize);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] == 0) {
                    gc.setFill(Color.rgb(255, 250, 240));
                    gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

                    gc.setFill(Color.rgb(255, 200, 100, 0.3));
                    gc.fillOval(
                            col * cellSize + cellSize / 2 - 3,
                            row * cellSize + cellSize / 2 - 3,
                            6, 6
                    );
                }
            }
        }

        for (int i = 0; i < walls.size(); i++) {
            Wall wall = walls.get(i);

            int colorIndex = (int)(wall.getX() / cellSize + wall.getY() / cellSize) % 3;

            switch (colorIndex) {
                case 0 -> gc.setFill(SIDI_BOU_SAID_BLUE);
                case 1 -> gc.setFill(WHITE);
                default -> gc.setFill(LIGHT_BLUE);
            }

            gc.fillRect(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());

            gc.setStroke(Color.rgb(41, 128, 185));
            gc.setLineWidth(2);
            gc.strokeRect(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());

            gc.setFill(Color.rgb(255, 255, 255, 0.3));
            gc.fillRoundRect(
                    wall.getX() + 2,
                    wall.getY() + 2,
                    wall.getWidth() - 4,
                    wall.getHeight() - 4,
                    5, 5
            );
        }
    }


    public List<Wall> getWalls() {
        return walls;
    }


    public boolean isValidPosition(double x, double y, double width, double height) {
        for (Wall wall : walls) {
            if (x < wall.getX() + wall.getWidth() &&
                    x + width > wall.getX() &&
                    y < wall.getY() + wall.getHeight() &&
                    y + height > wall.getY()) {
                return false;
            }
        }
        return true;
    }


    public boolean isWalkable(double x, double y) {
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        return grid[row][col] == 0;
    }


    public Position findValidStartPosition(double entityWidth, double entityHeight) {
        for (int row = 1; row < Math.min(10, rows - 1); row++) {
            for (int col = 1; col < Math.min(10, cols - 1); col++) {
                if (grid[row][col] == 0) {
                    double x = col * cellSize + (cellSize - entityWidth) / 2;
                    double y = row * cellSize + (cellSize - entityHeight) / 2;

                    if (isValidPosition(x, y, entityWidth, entityHeight)) {
                        return new Position(x, y);
                    }
                }
            }
        }

        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < cols - 1; col++) {
                if (grid[row][col] == 0) {
                    double x = col * cellSize + (cellSize - entityWidth) / 2;
                    double y = row * cellSize + (cellSize - entityHeight) / 2;

                    if (isValidPosition(x, y, entityWidth, entityHeight)) {
                        return new Position(x, y);
                    }
                }
            }
        }

        return new Position(cellSize * 1.5, cellSize * 1.5);
    }


    public Position findRandomValidPosition(double width, double height) {
        int maxAttempts = 100;
        int attempts = 0;

        while (attempts < maxAttempts) {
            int row = 1 + random.nextInt(rows - 2);
            int col = 1 + random.nextInt(cols - 2);

            if (grid[row][col] == 0) {
                double x = col * cellSize + (cellSize - width) / 2;
                double y = row * cellSize + (cellSize - height) / 2;

                if (isValidPosition(x, y, width, height)) {
                    return new Position(x, y);
                }
            }
            attempts++;
        }

        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < cols - 1; col++) {
                if (grid[row][col] == 0) {
                    double x = col * cellSize + (cellSize - width) / 2;
                    double y = row * cellSize + (cellSize - height) / 2;

                    if (isValidPosition(x, y, width, height)) {
                        return new Position(x, y);
                    }
                }
            }
        }

        return new Position(cellSize * 2, cellSize * 2);
    }


    public double getCellSize() {
        return cellSize;
    }


    public int getRows() {
        return rows;
    }


    public int getCols() {
        return cols;
    }
    public int[][] getGrid() {
        return grid;
    }
}