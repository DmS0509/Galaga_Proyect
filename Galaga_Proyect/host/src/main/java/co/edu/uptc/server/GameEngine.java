package co.edu.uptc.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import co.edu.uptc.server.models.GameWorld;
import co.edu.uptc.server.models.Player; // Server-side Player
import co.edu.uptc.server.models.Proyectile; // Server-side Projectile
import co.edu.uptc.server.models.enemy.Enemy; // Server-side Enemy
import co.edu.uptc.server.network.ClientHadler;

public class GameEngine {
    private volatile boolean gamePaused = false;
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
        if (!gamePaused) {
            gameWorld.update(2);
            sendGameStateToAllClients();
        }
    }

    public void pauseGame() {
        gamePaused = true;
        System.out.println("Juego pausado.");
    }

    public void resumeGame() {
        gamePaused = false;
        System.out.println("Juego reanudado.");
    }

    public void restartGame() {
        System.out.println("Reiniciando juego...");
        gameWorld = new GameWorld(); // Reset game world
        // Optionally, re-add existing players with default positions
        for (String playerId : clients.keySet()) {
            gameWorld.addPlayer(playerId, new Player(playerId, 350, 500, 100));
        }
        gamePaused = false; // Ensure game is not paused after restart
        System.out.println("Juego reiniciado.");
    }


    private void sendGameStateToAllClients() {
        GameState gameState = new GameState(
            gameWorld.getAllPlayers(),
            gameWorld.getEnemies(),
            gameWorld.getProjectiles(),
            gameWorld.getWaveManager().getCurrentWave()
        );

        for (ClientHadler client : clients.values()) {
            client.sendGameState(gameState);
        }
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public void addClient(String playerId, ClientHadler clientHandler) {
        clients.put(playerId, clientHandler);
        // Add player to game world when client connects
        gameWorld.addPlayer(playerId, new Player(playerId, 350, 500, 100)); // Default player position
    }

    public void removeClient(String playerId) {
        clients.remove(playerId);
        gameWorld.removePlayer(playerId); // Remove player from game world when client disconnects
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

    // This nested GameState class is what gets serialized on the server
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

        public Map<String, Player> getPlayers() { return players; }
        public List<Enemy> getEnemies() { return enemies; }
        public List<Proyectile> getProjectiles() { return projectiles; }
        public int getCurrentWave() { return currentWave; }
    }
}
