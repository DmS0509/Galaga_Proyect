package co.edu.uptc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import co.edu.uptc.server.network.ClientHandler;

public class GameServer {
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 10;
    
    private final int port;
    private final GameEngine gameEngine;
    private final ExecutorService clientExecutor;
    private volatile boolean isRunning;
    private ServerSocket serverSocket;

    public GameServer(int port) {
        this.port = port;
        this.gameEngine = new GameEngine();
        this.clientExecutor = Executors.newCachedThreadPool();
        this.isRunning = false;
    }

    public void start() {
        if (isRunning) {
            LOGGER.warning("Intento de iniciar un servidor que ya está en ejecución");
            return;
        }

        gameEngine.start();
        isRunning = true;

        try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("Servidor Galaga iniciado, escuchando en el puerto " + port);
            
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.info("Cliente conectado desde " + clientSocket.getInetAddress().getHostAddress());
                    
                    ClientHandler clientHandler = new ClientHandler(clientSocket, gameEngine);
                    clientExecutor.execute(clientHandler);
                } catch (IOException e) {
                    if (isRunning) {
                        LOGGER.severe("Error al aceptar conexión de cliente: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error al iniciar el servidor: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void stop() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        shutdown();
    }

    private void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            gameEngine.stop();
            
            clientExecutor.shutdown();
            if (!clientExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                clientExecutor.shutdownNow();
            }
            
            LOGGER.info("Servidor detenido correctamente");
        } catch (IOException e) {
            LOGGER.severe("Error al cerrar el socket del servidor: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.severe("Interrupción durante el apagado del servidor: " + e.getMessage());
            clientExecutor.shutdownNow();
        }
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        GameServer server = new GameServer(port);
        
        // Agregar shutdown hook para cierre graceful
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        server.start();
    }
}
