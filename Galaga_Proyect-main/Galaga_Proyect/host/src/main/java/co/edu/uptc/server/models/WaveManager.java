package co.edu.uptc.server.models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import co.edu.uptc.server.models.enemy.Enemy;
import co.edu.uptc.server.models.enemy.EnemyType;

public class WaveManager {
    private static final Logger LOGGER = Logger.getLogger(WaveManager.class.getName());
    
    // Constantes de configuración de oleadas
    private static final long TIME_BETWEEN_WAVES_MS = 5000;
    private static final float INITIAL_X = 100f;
    private static final float INITIAL_Y = 50f;
    private static final float SPACING_X = 50f;
    private static final float SPACING_Y = 40f;
    
    private final GameWorld gameWorld;
    private int currentWave;
    private boolean waveInProgress;
    private long waveStartTimestamp;

    public WaveManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.currentWave = 0;
        this.waveInProgress = false;
        this.waveStartTimestamp = 0;
        LOGGER.info("WaveManager inicializado");
    }

    public void startWave() {
        currentWave++;
        waveInProgress = true;
        waveStartTimestamp = System.currentTimeMillis();
        LOGGER.info("Iniciando oleada " + currentWave);
        generateEnemiesForWave(currentWave);
    }

    public void checkAndStartNextWave() {
        if (!waveInProgress && System.currentTimeMillis() - waveStartTimestamp >= TIME_BETWEEN_WAVES_MS) {
            startWave();
        }
    }

    private void generateEnemiesForWave(int waveNumber) {
        try {
            switch (waveNumber) {
                case 1:
                    generateBasicWave();
                    break;
                case 2:
                    generateIntermediateWave();
                    break;
                case 3:
                    generateBossWave();
                    break;
                default:
                    generateScaledWave(waveNumber);
                    break;
            }
        } catch (Exception e) {
            LOGGER.severe("Error al generar oleada " + waveNumber + ": " + e.getMessage());
            waveInProgress = false;
        }
    }

    private void generateBasicWave() {
        int rows = 3;
        int cols = 8;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Enemy bee = EnemyType.BEE.createEnemy();
                positionEnemy(bee, j, i, false);
                gameWorld.addEnemy(bee);
            }
        }
        LOGGER.info("Oleada básica generada: " + (rows * cols) + " enemigos");
    }

    private void generateIntermediateWave() {
        int rows = 4;
        int cols = 6;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Enemy spi = EnemyType.SPI.createEnemy();
                positionEnemy(spi, j, i, true);
                gameWorld.addEnemy(spi);
            }
        }
        LOGGER.info("Oleada intermedia generada: " + (rows * cols) + " enemigos");
    }

    private void generateBossWave() {
        // Generar el jefe
        Enemy boss = EnemyType.BOSS.createEnemy();
        float bossX = gameWorld.getAllPlayers().values().stream()
                .findFirst()
                .map(p -> p.getX())
                .orElse(350f);
        boss.setX(bossX);
        boss.setY(INITIAL_Y);
        gameWorld.addEnemy(boss);

        // Generar escoltas
        int escortRows = 2;
        int escortCols = 4;
        for (int i = 0; i < escortRows; i++) {
            for (int j = 0; j < escortCols; j++) {
                Enemy bee = EnemyType.BEE.createEnemy();
                bee.setX(INITIAL_X + j * SPACING_X + 100);
                bee.setY(INITIAL_Y + 150 + i * SPACING_Y);
                gameWorld.addEnemy(bee);
            }
        }
        LOGGER.info("Oleada de jefe generada con " + (escortRows * escortCols) + " escoltas");
    }

    private void generateScaledWave(int waveNumber) {
        int baseEnemies = 10 + (waveNumber - 3) * 2;
        int rows = Math.min(5, (baseEnemies + 7) / 8);
        int cols = Math.min(8, (baseEnemies + rows - 1) / rows);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Enemy enemy = (Math.random() < 0.7) ? 
                    EnemyType.BEE.createEnemy() : 
                    EnemyType.SPI.createEnemy();
                positionEnemy(enemy, j, i, waveNumber % 2 == 0);
                gameWorld.addEnemy(enemy);
            }
        }
        LOGGER.info("Oleada escalada " + waveNumber + " generada: " + (rows * cols) + " enemigos");
    }

    private void positionEnemy(Enemy enemy, int col, int row, boolean staggered) {
        float x = INITIAL_X + col * SPACING_X;
        if (staggered) {
            x += (row % 2) * (SPACING_X / 2);
        }
        float y = INITIAL_Y + row * SPACING_Y;
        enemy.setX(x);
        enemy.setY(y);
    }

    // Getters
    public int getCurrentWave() { return currentWave; }
    public boolean isWaveInProgress() { return waveInProgress; }
    
    // Setters
    public void setWaveInProgress(boolean waveInProgress) { 
        this.waveInProgress = waveInProgress;
        LOGGER.fine("Estado de oleada actualizado: " + (waveInProgress ? "en progreso" : "finalizada"));
    }
}
