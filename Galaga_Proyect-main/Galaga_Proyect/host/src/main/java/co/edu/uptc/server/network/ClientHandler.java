package co.edu.uptc.server.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import co.edu.uptc.server.GameEngine;
import co.edu.uptc.server.models.Player;

/**
 * Maneja la conexión y comunicación con un cliente individual del juego.
 */
public final class ClientHandler implements Runnable, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    
    private final Socket clientSocket;
    private final GameEngine gameEngine;
    private final String playerId;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Gson gson;
    private final AtomicBoolean running;

    public ClientHandler(Socket socket, GameEngine gameEngine) throws IOException {
        if (socket == null || gameEngine == null) {
            throw new IllegalArgumentException("Socket y GameEngine no pueden ser null");
        }
        this.clientSocket = socket;
        this.gameEngine = gameEngine;
        this.playerId = UUID.randomUUID().toString();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.gson = new Gson();
        this.running = new AtomicBoolean(true);
        
        // Inicializar jugador
        gameEngine.getGameWorld().addPlayer(playerId, new Player(playerId));
        gameEngine.addClient(playerId, this);
        LOGGER.log(Level.INFO, "Nuevo cliente conectado con ID: {0}", playerId);
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                String input = in.readLine();
                if (input == null) {
                    LOGGER.log(Level.INFO, "Cliente {0} desconectado (EOF)", playerId);
                    break;
                }
                handleClientInput(input);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en la comunicación con el cliente: {0}", playerId);
            LOGGER.log(Level.SEVERE, "Detalles del error:", e);
        } finally {
            disconnect();
        }
    }

    private void handleClientInput(String input) {
        try {
            ClientAction action = GameProtocol.interpretClientAction(input);
            switch (action) {
                case MOVE_LEFT:
                    gameEngine.movePlayerLeft(playerId);
                    break;
                case MOVE_RIGHT:
                    gameEngine.movePlayerRight(playerId);
                    break;
                case SHOOT:
                    gameEngine.playerShoot(playerId);
                    break;
                case DISCONNECT:
                    disconnect();
                    break;
                case UNKNOWN:
                    LOGGER.log(Level.WARNING, "Acción desconocida recibida del cliente {0}", playerId);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al procesar la entrada del cliente: {0}", playerId);
            LOGGER.log(Level.SEVERE, "Detalles del error:", e);
        }
    }

    public void sendGameState(Object gameState) {
        try {
            String gameStateJson = gson.toJson(gameState);
            String formattedMessage = GameProtocol.createGameStateUpdate(gameStateJson);
            out.println(formattedMessage);
            out.flush();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al enviar el estado del juego al cliente: {0}", playerId);
            LOGGER.log(Level.SEVERE, "Detalles del error:", e);
        }
    }

    private void disconnect() {
        if (running.compareAndSet(true, false)) {
            LOGGER.log(Level.INFO, "Desconectando cliente: {0}", playerId);
            gameEngine.removeClient(playerId);
            gameEngine.getGameWorld().removePlayer(playerId);
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar la conexión del cliente: {0}", playerId);
                LOGGER.log(Level.SEVERE, "Detalles del error:", e);
            }
        }
    }

    @Override
    public void close() {
        disconnect();
    }

    public String getPlayerId() {
        return playerId;
    }
} 