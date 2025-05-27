package co.edu.uptc.server.models;

import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.server.models.enemy.Enemy;
import co.edu.uptc.server.models.enemy.EnemyType;

public class WaveManager {

    private GameWorld gameWorld;
    private int currentWave = 0;
    private boolean waveInProgress = false;
    private long waveStartTimestamp;
    private long timeBetweenWaves = 5000; 

    public WaveManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void startWave() {
        currentWave++;
        waveInProgress = true;
        waveStartTimestamp = System.currentTimeMillis();
        System.out.println("Iniciando oleada " + currentWave);
        generateEnemiesForWave(currentWave);
    }

    public void checkAndStartNextWave() {
        if (!waveInProgress && System.currentTimeMillis() - waveStartTimestamp >= timeBetweenWaves) {
            startWave();
        }
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }

    public void setWaveInProgress(boolean waveInProgress) {
        this.waveInProgress = waveInProgress;
    }

    private void generateEnemiesForWave(int waveNumber) {
        List<Enemy> newEnemies = new ArrayList<>();
        float startX = 100;
        float startY = 50;
        float spacingX = 50;
        float spacingY = 40;
        int rows = 0;
        int cols = 0;

        switch (waveNumber) {
            case 1:
                rows = 3;
                cols = 8;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        Enemy bee = EnemyType.BEE.createEnemy();
                        bee.setX(startX + j * spacingX);
                        bee.setY(startY + i * spacingY);
                        gameWorld.addEnemy(bee);
                    }
                }
                break;
            case 2:
                rows = 4;
                cols = 6;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        Enemy spi = EnemyType.SPI.createEnemy();
                        spi.setX(startX + j * spacingX + spacingX / 2 * (i % 2)); // Formación escalonada
                        spi.setY(startY + i * spacingY);
                        gameWorld.addEnemy(spi);
                    }
                }
                break;
            case 3:
                Enemy boss = EnemyType.BOSS.createEnemy();
                boss.setX(gameWorld.getAllPlayers().values().stream().findFirst().map(p -> p.getX()).orElse(350f)); // Centrado en el jugador inicial
                boss.setY(startY);
                gameWorld.addEnemy(boss);
                rows = 2;
                cols = 4;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        Enemy bee = EnemyType.BEE.createEnemy();
                        bee.setX(startX + j * spacingX + 100);
                        bee.setY(startY + 150 + i * spacingY);
                        gameWorld.addEnemy(bee);
                    }
                }
                break;
            default:
                System.out.println("Oleada " + waveNumber + ": Sin enemigos definidos.");
                waveInProgress = false; 
                break;
        }

        if (!newEnemies.isEmpty()) {
            waveInProgress = true;
        } else if (waveNumber > 0) {
            waveInProgress = false; 
        }
    }
}
