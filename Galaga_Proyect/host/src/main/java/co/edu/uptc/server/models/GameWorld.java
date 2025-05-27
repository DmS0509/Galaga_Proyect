package co.edu.uptc.server.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import co.edu.uptc.server.models.enemy.Enemy;

public class GameWorld {

    private Map<String, Player> players = new ConcurrentHashMap<>();
    private List<Enemy> enemies = new CopyOnWriteArrayList<>(); 
    private List<Proyectile> projectiles = new CopyOnWriteArrayList<>(); 
    private WaveManager waveManager;

    public GameWorld() {
        this.waveManager = new WaveManager(this);
    }

    public void addPlayer(String playerId, Player player) {
        players.put(playerId, player);
        System.out.println("Jugador " + playerId + " se ha conectado al mundo.");
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
        System.out.println("Jugador " + playerId + " se ha desconectado del mundo.");
    }

    public Map<String, Player> getAllPlayers() {
        return players;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    public void addProjectile(Proyectile projectile) {
        projectiles.add(projectile);
    }

    public List<Proyectile> getProjectiles() {
        return projectiles;
    }

    public void removeProjectile(Proyectile projectile) {
        projectiles.remove(projectile);
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    public void startNextWave() {
        waveManager.startWave();
    }

    public void update(float deltaTime) {
        for (Player player : players.values()) {
            player.updateProjectiles(deltaTime);
        }

        for (Enemy enemy : enemies) {
            enemy.update();
        }

        List<Proyectile> projectilesToRemove = new ArrayList<>();
        for (Proyectile projectile : projectiles) {
            projectile.move(deltaTime);
            if (projectile.getY() < -50 || projectile.getY() > 650 || projectile.getX() < -50 || projectile.getX() > 750) {
                projectilesToRemove.add(projectile);
            }
        }
        projectiles.removeAll(projectilesToRemove);

        handleCollisions();

        if (enemies.isEmpty()) {
            waveManager.checkAndStartNextWave();
        }
    }

    private void handleCollisions() {
        for (Proyectile projectile : new ArrayList<>(projectiles)) {
            if (players.containsKey(projectile.getOwnerId())) {
                for (Enemy enemy : new ArrayList<>(enemies)) {
                    if (projectile.isActive() && enemy.checkCollision(projectile.getX(), projectile.getY())) {
                        enemy.takeDamage(1);
                        projectile.deactivate();
                        removeProjectile(projectile);
                        if (enemy.getHealth() <= 0) {
                            if (getPlayer(projectile.getOwnerId()) != null) {
                                getPlayer(projectile.getOwnerId()).addScore(enemy.getKillPoints());
                            }
                            removeEnemy(enemy);
                        }
                    }
                }
            } else {
                for (Player player : players.values()) {
                    if (projectile.isActive() && !projectile.getOwnerId().equals(player.getPlayerId()) &&
                        player.checkCollision(projectile.getX(), projectile.getY(), 32, 32)) {
                        player.takeDamage(1);
                        projectile.deactivate();
                        removeProjectile(projectile);
                    }
                }
            }
        }

        for (Player player : players.values()) {
            for (Enemy enemy : new ArrayList<>(enemies)) {
                if (player.checkCollision(enemy.getX(), enemy.getY(), 30, 30)) {
                    player.takeDamage(1);
                    removeEnemy(enemy);
                }
            }
        }
    }
}
