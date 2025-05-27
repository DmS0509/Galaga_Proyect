package co.edu.uptc.host.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

import java.net.ConnectException; // Importa ConnectException

import com.google.gson.Gson;

import co.edu.uptc.host.view.GameScreen;
import co.edu.uptc.server.network.ClientAction;
import co.edu.uptc.server.network.GameProtocol;

public class GameClient {

    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameScreen gameScreen;
    private Gson gson = new Gson();
    private String playerId;

    public GameClient(String serverAddress, int serverPort, GameScreen gameScreen) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.gameScreen = gameScreen;
    }

    public boolean connect() {
        try {
            System.out.println("GameClient: Intentando conectar a " + serverAddress + ":" + serverPort + "...");
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            playerId = in.readLine();
            if (playerId != null) {
                System.out.println("GameClient: Conectado al servidor como jugador: " + playerId);
                Thread receiveThread = new Thread(this::receiveUpdates, "ClientReceiveThread-" + playerId); // Nombre de hilo para depuración
                receiveThread.start();
                return true;
            } else {
                System.err.println("GameClient: El servidor no envió un ID de jugador.");
                disconnect(); 
                return false;
            }
        } catch (ConnectException e) {
            System.err.println("GameClient: No se pudo conectar al servidor. Asegúrese de que el servidor esté en ejecución. " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("GameClient: Error de E/S al conectar o inicializar flujos: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace completo para más detalles
            return false;
        }
    }

    public void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("GameClient: Desconectado del servidor.");
            }
        } catch (IOException e) {
            System.err.println("GameClient: Error al desconectar: " + e.getMessage());
        }
    }

    public void sendAction(ClientAction action) {
        if (out != null) {
            out.println(GameProtocol.formatClientAction(action)); // Usa GameProtocol.formatClientAction
        }
    }

     private void receiveUpdates() {
        String serverMessage;
        try {
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith(GameProtocol.GAME_STATE_UPDATE_PREFIX)) {
                    String gameStateJson = GameProtocol.extractGameState(serverMessage);
                    if (gameStateJson != null) {
                        try {
                            System.out.println("GameClient: Recibiendo actualización del estado del juego.");
                            GameState gameState = gson.fromJson(gameStateJson, GameState.class);
                            if (gameScreen != null) {
                                gameScreen.updateGameState(gameState);
                            }
                        } catch (com.google.gson.JsonSyntaxException jse) {
                            System.err.println("Error de sintaxis JSON al deserializar GameState: " + jse.getMessage());
                            System.err.println("JSON recibido: " + gameStateJson);
                        }
                    }
                } else {
                    System.out.println("Mensaje del servidor: " + serverMessage);
                }
            }
        } catch (IOException e) {
            System.err.println("Conexión con el servidor perdida: " + e.getMessage());
            disconnect();
        }
    }

    public String getPlayerId() {
        return playerId;
    }
}