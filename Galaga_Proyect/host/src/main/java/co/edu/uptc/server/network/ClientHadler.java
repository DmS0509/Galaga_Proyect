package co.edu.uptc.server.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import co.edu.uptc.server.GameEngine;
import co.edu.uptc.server.models.GameWorld;
import co.edu.uptc.server.models.Player;

public class ClientHadler implements Runnable{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerId;
    private GameEngine gameEngine;
    private GameWorld gameWorld;
    private final Gson gson = new Gson();

    public ClientHadler(Socket socket, GameEngine engine) {
        this.clientSocket = socket;
        this.gameEngine = engine;
        this.gameWorld = engine.getGameWorld();
    }

    @Override
    public void run() {
       try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.playerId = "Player_" + System.currentTimeMillis(); // Generate a unique ID
            gameEngine.addClient(playerId, this); // Register client handler with the engine
            gameWorld.addPlayer(playerId, new Player(playerId, 350, 500, 100)); // Add player to game world
            out.println(playerId); // Send the generated playerId to the client
            System.out.println("Cliente conectado con ID: " + playerId);
            
            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                processClientInput(clientInput);
            }

        } catch (IOException e) {
            System.err.println("Error de I/O para el cliente " + playerId + ": " + e.getMessage());
        }
    
    }

    private void processClientInput(String input) {
        switch (GameProtocol.interpretClientAction(input)) {
            case MOVE_LEFT:
                gameEngine.movePlayerLeft(playerId);
                break;
            case MOVE_RIGHT:
                gameEngine.movePlayerRight(playerId);
                break;
            case SHOOT:
                gameEngine.playerShoot(playerId);
                break;
            case PAUSE_GAME:
                gameEngine.pauseGame();
                break;
            case RESUME_GAME:
                gameEngine.resumeGame();
                break;
            case RESTART_GAME:
                gameEngine.restartGame();
                break;
            case UNKNOWN:
                System.out.println("Acción desconocida del cliente " + playerId + ": " + input);
                break;
            default:
                break;
        }
    }

    public void sendGameState(Object gameState) {
        String gameStateJson = gson.toJson(gameState);
        out.println(GameProtocol.createGameStateUpdate(gameStateJson));
    }

}
