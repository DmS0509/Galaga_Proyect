package co.edu.uptc.host.controller;

import java.util.List;
import java.util.Map;

import co.edu.uptc.server.models.Player;
import co.edu.uptc.server.models.Proyectile;
import co.edu.uptc.server.models.enemy.Enemy;

public class GameState {
        private Map<String, Player> players;
    private List<Enemy> enemies;
    private List<Proyectile> projectiles;
    private int currentWave;
    public GameState() {
    }

    public GameState(Map<String, Player> players, List<Enemy> enemies, List<Proyectile> projectiles, int currentWave) {
        this.players = players;
        this.enemies = enemies;
        this.projectiles = projectiles;
        this.currentWave = currentWave;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public List<co.edu.uptc.server.models.enemy.Enemy> getEnemies() {
        return enemies;
    }

    public List<Proyectile> getProjectiles() {
        return projectiles;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public void setProjectiles(List<Proyectile> projectiles) {
        this.projectiles = projectiles;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

}
