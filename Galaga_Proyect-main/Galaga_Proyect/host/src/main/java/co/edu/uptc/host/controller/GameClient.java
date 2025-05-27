package co.edu.uptc.host.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import co.edu.uptc.host.view.GameScreen;
import co.edu.uptc.server.network.ClientAction;
import co.edu.uptc.server.network.GameProtocol;

/**
 * Cliente del juego que maneja la comunicación con el servidor.
 */
public final class GameClient implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(GameClient.class.getName());
    
    private final String serverAddress;
    private final int serverPort;
    private final GameScreen gameScreen;
    private final Gson gson;
    private final AtomicBoolean isRunning;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerId;
    private Thread receiveThread;

    /**
     * Constructor del cliente del juego.
     * @param serverAddress Dirección del servidor
     * @param serverPort Puerto del servidor
     * @param gameScreen Pantalla del juego para actualizaciones
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public GameClient(String serverAddress, int serverPort, GameScreen gameScreen) {
        if (serverAddress == null || serverAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección del servidor no puede ser nula o vacía");
        }
        if (serverPort <= 0 || serverPort > 65535) {
            throw new IllegalArgumentException("Puerto inválido: " + serverPort);
        }
        if (gameScreen == null) {
            throw new IllegalArgumentException("GameScreen no puede ser null");
        }
        
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.gameScreen = gameScreen;
        this.gson = new Gson();
        this.isRunning = new AtomicBoolean(false);
    }

    /**
     * Conecta al cliente con el servidor.
     * @return true si la conexión fue exitosa, false en caso contrario
     */
    public boolean connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            playerId = in.readLine();
            if (playerId == null || playerId.trim().isEmpty()) {
                throw new IOException("No se recibió ID de jugador válido del servidor");
            }
            
            LOGGER.log(Level.INFO, "Conectado al servidor como jugador: {0}", playerId);
            
            isRunning.set(true);
            receiveThread = new Thread(this::receiveUpdates);
            receiveThread.setDaemon(true);
            receiveThread.start();
            
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar al servidor: {0}", e.getMessage());
            close();
            return false;
        }
    }

    @Override
    public void close() {
        if (isRunning.compareAndSet(true, false)) {
            LOGGER.info("Cerrando conexión con el servidor...");
            try {
                if (out != null) {
                    sendAction(ClientAction.DISCONNECT);
                    out.close();
                }
                if (in != null) in.close();
                if (socket != null && !socket.isClosed()) socket.close();
                
                LOGGER.info("Desconectado del servidor exitosamente");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar la conexión: {0}", e.getMessage());
            }
        }
    }

    /**
     * Envía una acción al servidor.
     * @param action La acción a enviar
     */
    public void sendAction(ClientAction action) {
        if (action == null) {
            LOGGER.warning("Intento de enviar acción nula");
            return;
        }
        
        if (out != null && isRunning.get()) {
            String formattedAction = GameProtocol.formatClientAction(action);
            out.println(formattedAction);
            LOGGER.log(Level.FINE, "Acción enviada: {0}", action);
        } else {
            LOGGER.warning("No se puede enviar la acción: cliente no conectado");
        }
    }

    private void receiveUpdates() {
        try {
            String serverMessage;
            while (isRunning.get() && (serverMessage = in.readLine()) != null) {
                processServerMessage(serverMessage);
            }
        } catch (IOException e) {
            if (isRunning.get()) {
                LOGGER.log(Level.SEVERE, "Error en la conexión con el servidor: {0}", e.getMessage());
                close();
            }
        }
    }

    private void processServerMessage(String serverMessage) {
        try {
            if (serverMessage.startsWith(GameProtocol.GAME_STATE_UPDATE_PREFIX)) {
                String gameStateJson = GameProtocol.extractGameState(serverMessage);
                if (gameStateJson != null) {
                    GameState gameState = gson.fromJson(gameStateJson, GameState.class);
                    gameScreen.updateGameState(gameState);
                    LOGGER.log(Level.FINE, "Estado del juego actualizado");
                }
            } else {
                LOGGER.log(Level.INFO, "Mensaje del servidor: {0}", serverMessage);
            }
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.WARNING, "Error al procesar estado del juego: {0}", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al procesar mensaje del servidor: {0}", e.getMessage());
        }
    }

    /**
     * Obtiene el ID del jugador asignado por el servidor.
     * @return el ID del jugador
     */
    public String getPlayerId() {
        return playerId;
    }
}
