package gemcollector.core;

import gemcollector.entities.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TileMap refactorisé - Labyrinthe Pac-Man style Sidi Bou Said
 * Compatible avec le système d'entités Wall
 */
public class TileMap extends CoreComponent {

    private final List<Wall> walls;
    private final double cellSize;
    private final int rows;
    private final int cols;
    private int[][] grid;
    private Random random = new Random();

    // Couleurs de Sidi Bou Said
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
        // Créer le pattern du labyrinthe Pac-Man
        createMazePattern();

        // Convertir le pattern en Wall objects
        convertGridToWalls();
    }

    @Override
    public void update() {
        // Les murs ne bougent pas, rien à mettre à jour
    }

    /**
     * Crée le pattern du labyrinthe (0 = passage, 1 = mur)
     */
    private void createMazePattern() {
        // Pattern de labyrinthe Pac-Man (25x19)
        int[][] mazePattern = {
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

        // Initialiser la grille
        grid = new int[rows][cols];

        // Copier le pattern dans la grille
        for (int row = 0; row < mazePattern.length && row < rows; row++) {
            for (int col = 0; col < mazePattern[row].length && col < cols; col++) {
                grid[row][col] = mazePattern[row][col];
            }
        }
    }

    /**
     * Convertit la grille en objets Wall
     */
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

    /**
     * Dessine le labyrinthe avec le style Sidi Bou Said
     */
    public void render(GraphicsContext gc) {
        // Fond beige/sable clair
        gc.setFill(SAND_BEIGE);
        gc.fillRect(0, 0, cols * cellSize, rows * cellSize);

        // Dessiner les passages (chemins)
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] == 0) {
                    // Chemins avec effet de dallage
                    gc.setFill(Color.rgb(255, 250, 240));
                    gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

                    // Petits points pour effet pellets Pac-Man
                    gc.setFill(Color.rgb(255, 200, 100, 0.3));
                    gc.fillOval(
                            col * cellSize + cellSize / 2 - 3,
                            row * cellSize + cellSize / 2 - 3,
                            6, 6
                    );
                }
            }
        }

        // Dessiner les murs avec style Sidi Bou Said
        for (int i = 0; i < walls.size(); i++) {
            Wall wall = walls.get(i);

            // Alternance des couleurs (carrelage tunisien)
            int colorIndex = (int)(wall.getX() / cellSize + wall.getY() / cellSize) % 3;

            switch (colorIndex) {
                case 0 -> gc.setFill(SIDI_BOU_SAID_BLUE);
                case 1 -> gc.setFill(WHITE);
                default -> gc.setFill(LIGHT_BLUE);
            }

            // Remplir le mur
            gc.fillRect(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());

            // Bordure pour effet 3D
            gc.setStroke(Color.rgb(41, 128, 185));
            gc.setLineWidth(2);
            gc.strokeRect(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());

            // Effet de profondeur (coins arrondis style tunisien)
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

    /**
     * Retourne la liste des murs pour la détection de collision
     */
    public List<Wall> getWalls() {
        return walls;
    }

    /**
     * Vérifie si une position est valide (pas de collision avec mur)
     */
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

    /**
     * Vérifie si une cellule de la grille est un passage (walkable)
     */
    public boolean isWalkable(double x, double y) {
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        return grid[row][col] == 0;
    }

    /**
     * Trouve une position de départ valide pour une entité
     */
    public Position findValidStartPosition(double entityWidth, double entityHeight) {
        // Chercher dans la zone en haut à gauche
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

        // Recherche étendue
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

        // Position par défaut sécurisée
        return new Position(cellSize * 1.5, cellSize * 1.5);
    }

    /**
     * Trouve une position aléatoire valide dans le labyrinthe
     */
    public Position findRandomValidPosition(double width, double height) {
        int maxAttempts = 100;
        int attempts = 0;

        // Stratégie 1 : Essayer des positions aléatoires dans les passages
        while (attempts < maxAttempts) {
            // Choisir une cellule aléatoire
            int row = 1 + random.nextInt(rows - 2);
            int col = 1 + random.nextInt(cols - 2);

            // Vérifier si c'est un passage (pas un mur)
            if (grid[row][col] == 0) {
                // Centrer l'entité dans la cellule
                double x = col * cellSize + (cellSize - width) / 2;
                double y = row * cellSize + (cellSize - height) / 2;

                // Vérifier qu'il n'y a pas de collision avec les murs
                if (isValidPosition(x, y, width, height)) {
                    return new Position(x, y);
                }
            }
            attempts++;
        }

        // Stratégie 2 : Si échec, scanner systématiquement
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

        // Fallback : position par défaut
        return new Position(cellSize * 2, cellSize * 2);
    }

    /**
     * Retourne la taille d'une cellule
     */
    public double getCellSize() {
        return cellSize;
    }

    /**
     * Retourne le nombre de lignes
     */
    public int getRows() {
        return rows;
    }

    /**
     * Retourne le nombre de colonnes
     */
    public int getCols() {
        return cols;
    }

    /**
     * Retourne la grille (pour debug ou extensions futures)
     */
    public int[][] getGrid() {
        return grid;
    }
}