package co.edu.uptc.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import co.edu.uptc.server.models.GameWorld;
import co.edu.uptc.server.models.Player;
import co.edu.uptc.server.models.Proyectile;
import co.edu.uptc.server.models.enemy.Enemy;
import co.edu.uptc.server.network.ClientHandler;

public class GameEngine {
    private static final Logger LOGGER = Logger.getLogger(GameEngine.class.getName());
    private static final int UPDATES_PER_SECOND = 30;
    private static final long UPDATE_INTERVAL_NANOS = 1_000_000_000L / UPDATES_PER_SECOND;
    
    private final GameWorld gameWorld;
    private final Map<String, ClientHandler> clients;
    private final ScheduledExecutorService scheduler;
    private volatile boolean isRunning;

    public GameEngine() {
        this.gameWorld = new GameWorld();
        this.clients = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.isRunning = false;
    }

    public void start() {
        if (isRunning) {
            LOGGER.warning("Intento de iniciar un motor de juego que ya está en ejecución");
            return;
        }
        
        isRunning = true;
        LOGGER.info("Game Engine iniciado en el puerto configurado.");
        scheduler.scheduleAtFixedRate(this::gameLoop, 0, UPDATE_INTERVAL_NANOS, TimeUnit.NANOSECONDS);
    }

    public void stop() {
        if (!isRunning) {
            return;
        }
        
        isRunning = false;
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            LOGGER.info("Game Engine detenido correctamente.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.severe("Error al detener el Game Engine: " + e.getMessage());
        }
    }

    private void gameLoop() {
        if (!isRunning) {
            return;
        }

        long startTime = System.nanoTime();
        try {
            float deltaTime = (float) UPDATE_INTERVAL_NANOS / 1_000_000_000f;
            updateGameState(deltaTime);
            sendGameStateToClients();
            
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            if (duration > UPDATE_INTERVAL_NANOS) {
                LOGGER.warning("Advertencia: El bucle de juego tardó " + 
                    (duration / 1_000_000) + " ms, excediendo el intervalo esperado.");
            }
        } catch (Exception e) {
            LOGGER.severe("Error en el bucle de juego: " + e.getMessage());
        }
    }

    private void updateGameState(float deltaTime) {
        gameWorld.update(deltaTime);
        // Aquí podrían ir más actualizaciones del estado del juego
    }

    private void sendGameStateToClients() {
        GameState gameState = new GameState(
            gameWorld.getAllPlayers(),
            gameWorld.getEnemies(),
            gameWorld.getProjectiles(),
            gameWorld.getWaveManager().getCurrentWave()
        );

        clients.values().forEach(client -> {
            try {
                client.sendGameState(gameState);
            } catch (Exception e) {
                LOGGER.warning("Error al enviar estado al cliente " + client.getPlayerId() + ": " + e.getMessage());
            }
        });
    }

    public void addClient(String playerId, ClientHandler clientHandler) {
        clients.put(playerId, clientHandler);
        LOGGER.info("Cliente conectado: " + playerId);
    }

    public void removeClient(String playerId) {
        clients.remove(playerId);
        LOGGER.info("Cliente desconectado: " + playerId);
    }

    public void movePlayerLeft(String playerId) {
        Player player = gameWorld.getPlayer(playerId);
        if (player != null) {
            player.moveLeft(1.0f / UPDATES_PER_SECOND);
        }
    }

    public void movePlayerRight(String playerId) {
        Player player = gameWorld.getPlayer(playerId);
        if (player != null) {
            player.moveRight(1.0f / UPDATES_PER_SECOND);
        }
    }

    public void playerShoot(String playerId) {
        Player player = gameWorld.getPlayer(playerId);
        if (player != null) {
            player.shoot(200);
        }
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public static class GameState {
        private final Map<String, Player> players;
        private final List<Enemy> enemies;
        private final List<Proyectile> projectiles;
        private final int currentWave;

        public GameState(Map<String, Player> players, List<Enemy> enemies, 
                        List<Proyectile> projectiles, int currentWave) {
            this.players = players;
            this.enemies = enemies;
            this.projectiles = projectiles;
            this.currentWave = currentWave;
        }

        // Getters
        public Map<String, Player> getPlayers() { return players; }
        public List<Enemy> getEnemies() { return enemies; }
        public List<Proyectile> getProjectiles() { return projectiles; }
        public int getCurrentWave() { return currentWave; }
    }
}
