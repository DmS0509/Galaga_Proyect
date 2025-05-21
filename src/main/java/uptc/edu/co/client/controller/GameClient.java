package uptc.edu.co.client.controller;

import uptc.edu.co.client.View.GameScreen;
import uptc.edu.co.server.network.ClientAction;
import uptc.edu.co.server.network.GameProtocol; 
import com.google.gson.Gson; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            playerId = in.readLine();
            System.out.println("Conectado al servidor como jugador: " + playerId);
            Thread receiveThread = new Thread(this::receiveUpdates);
            receiveThread.start();
            return true;
        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Desconectado del servidor.");
            }
        } catch (IOException e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
    }

    public void sendAction(ClientAction action) {
        if (out != null) {
            out.println(GameProtocol.interpretClientAction(action.toString()));
        }
    }

    private void receiveUpdates() {
        String serverMessage;
        try {
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith(GameProtocol.GAME_STATE_UPDATE_PREFIX)) {
                    String gameStateJson = GameProtocol.extractGameState(serverMessage);
                    if (gameStateJson != null) {
                        GameState gameState = gson.fromJson(gameStateJson, GameState.class);
                        if (gameScreen != null) {
                            gameScreen.updateGameState(gameState); 
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
