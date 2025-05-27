package co.edu.uptc.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import co.edu.uptc.server.models.GameWorld;
import co.edu.uptc.server.models.Player;
import co.edu.uptc.server.models.Proyectile;
import co.edu.uptc.server.models.enemy.Enemy;
import co.edu.uptc.server.network.ClientHadler;

public class GameEngine {

    private GameWorld gameWorld;
    private Map<String, ClientHadler> clients;
    private final int updatesPerSecond = 30;
    private final long updateIntervalNanos = 1_000_000_000L / updatesPerSecond;
    private ScheduledExecutorService scheduler;

    public GameEngine() {
        this.gameWorld = new GameWorld();
        this.clients = new java.util.concurrent.ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        System.out.println("Game Engine iniciado en Tunja.");
        scheduler.scheduleAtFixedRate(this::gameLoop, 0, updateIntervalNanos, TimeUnit.NANOSECONDS);
    }

    public void stop() {
        scheduler.shutdown();
        System.out.println("Game Engine detenido.");
    }

    private void gameLoop() {
        long startTime = System.nanoTime();
        float deltaTime = (float) updateIntervalNanos / 1_000_000_000f;
        gameWorld.update(deltaTime);
        sendGameStateToClients();

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        if (duration > updateIntervalNanos) {
            System.err.println("¡Advertencia! El bucle de juego tardó " + (duration / 1_000_000) + " ms, excediendo el intervalo.");
        }
    }

    private void sendGameStateToClients() {
        Map<String, Player> players = gameWorld.getAllPlayers();
        GameState gameState = new GameState(players, gameWorld.getEnemies(), gameWorld.getProjectiles(), gameWorld.getWaveManager().getCurrentWave());

        for (ClientHadler client : clients.values()) {
            client.sendGameState(gameState);
        }
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public void addClient(String playerId, ClientHadler clientHandler) {
        clients.put(playerId, clientHandler);
    }

    public void removeClient(String playerId) {
        clients.remove(playerId);
    }

    public void movePlayerLeft(String playerId) {
        Player player = gameWorld.getPlayer(playerId);
        if (player != null) {
            player.moveLeft(1.0f / updatesPerSecond); 
        }
    }

    public void movePlayerRight(String playerId) {
        Player player = gameWorld.getPlayer(playerId);
        if (player != null) {
            player.moveRight(1.0f / updatesPerSecond); 
        }
    }

    public void playerShoot(String playerId) {
        Player player = gameWorld.getPlayer(playerId);
        if (player != null) {
            player.shoot(200); 
        }
    }

    private static class GameState {
        private Map<String, Player> players;
        private List<Enemy> enemies;
        private List<Proyectile> projectiles;
        private int currentWave;

        public GameState(Map<String, Player> players, List<Enemy> enemies, List<Proyectile> projectiles, int currentWave) {
            this.players = players;
            this.enemies = enemies;
            this.projectiles = projectiles;
            this.currentWave = currentWave;
        }

    }
}
