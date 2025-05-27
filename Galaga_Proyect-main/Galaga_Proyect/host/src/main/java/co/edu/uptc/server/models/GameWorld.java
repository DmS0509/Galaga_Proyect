package co.edu.uptc.server.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import co.edu.uptc.server.models.enemy.Enemy;

public class GameWorld {
    private static final Logger LOGGER = Logger.getLogger(GameWorld.class.getName());
    
    // Constantes del mundo
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 600f;
    private static final float PROJECTILE_BOUNDS_MARGIN = 50f;
    
    private final Map<String, Player> players;
    private final List<Enemy> enemies;
    private final List<Proyectile> projectiles;
    private final WaveManager waveManager;

    public GameWorld() {
        this.players = new ConcurrentHashMap<>();
        this.enemies = new CopyOnWriteArrayList<>();
        this.projectiles = new CopyOnWriteArrayList<>();
        this.waveManager = new WaveManager(this);
        LOGGER.info("Nuevo mundo de juego creado");
    }

    public void addPlayer(String playerId, Player player) {
        players.put(playerId, player);
        LOGGER.info("Jugador " + playerId + " se ha conectado al mundo");
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public void removePlayer(String playerId) {
        Player player = players.remove(playerId);
        if (player != null) {
            LOGGER.info("Jugador " + playerId + " se ha desconectado del mundo con " + player.getScore() + " puntos");
        }
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        LOGGER.fine("Nuevo enemigo añadido al mundo");
    }

    public void removeEnemy(Enemy enemy) {
        if (enemies.remove(enemy)) {
            LOGGER.fine("Enemigo eliminado del mundo");
        }
    }

    public void addProjectile(Proyectile projectile) {
        projectiles.add(projectile);
    }

    public void removeProjectile(Proyectile projectile) {
        projectiles.remove(projectile);
    }

    public void update(float deltaTime) {
        updatePlayers(deltaTime);
        updateEnemies(deltaTime);
        updateProjectiles(deltaTime);
        handleCollisions();
        checkWaveStatus();
    }

    private void updatePlayers(float deltaTime) {
        players.values().forEach(player -> player.updateProjectiles(deltaTime));
    }

    private void updateEnemies(float deltaTime) {
        enemies.forEach(Enemy::update);
    }

    private void updateProjectiles(float deltaTime) {
        List<Proyectile> outOfBoundsProjectiles = new ArrayList<>();
        for (Proyectile projectile : projectiles) {
            projectile.move(deltaTime);
            if (isProjectileOutOfBounds(projectile)) {
                outOfBoundsProjectiles.add(projectile);
            }
        }
        if (!outOfBoundsProjectiles.isEmpty()) {
            projectiles.removeAll(outOfBoundsProjectiles);
        }
    }

    private boolean isProjectileOutOfBounds(Proyectile projectile) {
        return projectile.getY() < -PROJECTILE_BOUNDS_MARGIN ||
               projectile.getY() > WORLD_HEIGHT + PROJECTILE_BOUNDS_MARGIN ||
               projectile.getX() < -PROJECTILE_BOUNDS_MARGIN ||
               projectile.getX() > WORLD_WIDTH + PROJECTILE_BOUNDS_MARGIN;
    }

    private void handleCollisions() {
        handleProjectileCollisions();
        handlePlayerEnemyCollisions();
    }

    private void handleProjectileCollisions() {
        List<Proyectile> projectilesToRemove = new ArrayList<>();
        
        for (Proyectile projectile : projectiles) {
            if (!projectile.isActive()) continue;

            if (players.containsKey(projectile.getOwnerId())) {
                handlePlayerProjectileCollisions(projectile, projectilesToRemove);
            } else {
                handleEnemyProjectileCollisions(projectile, projectilesToRemove);
            }
        }

        if (!projectilesToRemove.isEmpty()) {
            projectiles.removeAll(projectilesToRemove);
        }
    }

    private void handlePlayerProjectileCollisions(Proyectile projectile, List<Proyectile> projectilesToRemove) {
        for (Enemy enemy : new ArrayList<>(enemies)) {
            if (enemy.checkCollision(projectile.getX(), projectile.getY())) {
                enemy.takeDamage(1);
                projectile.deactivate();
                projectilesToRemove.add(projectile);

                if (enemy.getHealth() <= 0) {
                    Player player = getPlayer(projectile.getOwnerId());
                    if (player != null) {
                        player.addScore(enemy.getKillPoints());
                        LOGGER.info("Jugador " + player.getPlayerId() + " destruyó un enemigo");
                    }
                    removeEnemy(enemy);
                }
                break;
            }
        }
    }

    private void handleEnemyProjectileCollisions(Proyectile projectile, List<Proyectile> projectilesToRemove) {
        for (Player player : players.values()) {
            if (!projectile.getOwnerId().equals(player.getPlayerId()) &&
                player.checkCollision(projectile.getX(), projectile.getY(), 32, 32)) {
                player.takeDamage(1);
                projectile.deactivate();
                projectilesToRemove.add(projectile);
                LOGGER.info("Jugador " + player.getPlayerId() + " recibió daño");
                break;
            }
        }
    }

    private void handlePlayerEnemyCollisions() {
        for (Player player : players.values()) {
            for (Enemy enemy : new ArrayList<>(enemies)) {
                if (player.checkCollision(enemy.getX(), enemy.getY(), 30, 30)) {
                    player.takeDamage(1);
                    removeEnemy(enemy);
                    LOGGER.info("Colisión entre jugador " + player.getPlayerId() + " y enemigo");
                }
            }
        }
    }

    private void checkWaveStatus() {
        if (enemies.isEmpty()) {
            waveManager.checkAndStartNextWave();
        }
    }

    // Getters
    public Map<String, Player> getAllPlayers() { return players; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Proyectile> getProjectiles() { return projectiles; }
    public WaveManager getWaveManager() { return waveManager; }
    
    public void startNextWave() {
        waveManager.startWave();
    }
}

