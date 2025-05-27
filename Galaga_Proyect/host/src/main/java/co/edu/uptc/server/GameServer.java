package co.edu.uptc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import co.edu.uptc.server.network.ClientHadler;

public class GameServer {

    private int port;
    private GameEngine gameEngine;

    public GameServer(int port) {
        this.port = port;
        this.gameEngine = new GameEngine();
    }

    public void start() {
        gameEngine.start();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Galaga iniciado en Tunja, escuchando en el puerto " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress().getHostAddress());
                ClientHadler clientHandler = new ClientHadler(clientSocket, gameEngine);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            gameEngine.stop();
        } finally {
            gameEngine.stop();
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        GameServer server = new GameServer(port);
        server.start();
    }
}
