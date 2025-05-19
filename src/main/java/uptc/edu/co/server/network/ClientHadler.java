package uptc.edu.co.server.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import uptc.edu.co.server.GameEngine;
import uptc.edu.co.server.model.GameWorld;
import uptc.edu.co.server.model.Player;

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
            playerId = in.readLine();
            System.out.println("Cliente conectado con ID: " + playerId);
            Player newPlayer = new Player(playerId, 350, 500, 100); // Posición y velocidad iniciales
            gameWorld.addPlayer(playerId, newPlayer);
            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                processClientInput(clientInput);
            }

        } catch (IOException e) {
            System.err.println("Error en la comunicación con el cliente " + playerId + ": " + e.getMessage());
        } finally {
            gameWorld.removePlayer(playerId);
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                clientSocket.close();
                System.out.println("Cliente " + playerId + " se ha desconectado.");
            } catch (IOException e) {
                System.err.println("Error al cerrar la conexión con el cliente " + playerId + ": " + e.getMessage());
            }
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
