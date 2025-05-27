package co.edu.uptc.server.network;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Protocolo de comunicación para el juego Galaga.
 * Maneja la interpretación y formateo de mensajes entre cliente y servidor.
 */
@SuppressWarnings("serial")
public final class GameProtocol {
    private static final Logger LOGGER = Logger.getLogger(GameProtocol.class.getName());
    private static final String CLIENT_ACTION_PREFIX = "CLIENT_ACTION:";
    public static final String GAME_STATE_UPDATE_PREFIX = "GAME_STATE:";

    // Constructor privado para evitar instanciación
    private GameProtocol() {
        throw new AssertionError("No se debe instanciar esta clase de utilidad");
    }

    /**
     * Interpreta una acción del cliente a partir de un mensaje de entrada.
     * @param input El mensaje de entrada del cliente
     * @return La acción del cliente interpretada
     */
    public static ClientAction interpretClientAction(String input) {
        if (input == null || input.isEmpty()) {
            LOGGER.log(Level.WARNING, "Se recibió un input nulo o vacío");
            return ClientAction.UNKNOWN;
        }

        if (!input.startsWith(CLIENT_ACTION_PREFIX)) {
            LOGGER.log(Level.WARNING, "Formato de mensaje inválido: {0}", input);
            return ClientAction.UNKNOWN;
        }

        try {
            String action = input.substring(CLIENT_ACTION_PREFIX.length()).trim();
            switch (action.toUpperCase()) {
                case "MOVE_LEFT":
                    return ClientAction.MOVE_LEFT;
                case "MOVE_RIGHT":
                    return ClientAction.MOVE_RIGHT;
                case "SHOOT":
                    return ClientAction.SHOOT;
                case "DISCONNECT":
                    return ClientAction.DISCONNECT;
                default:
                    LOGGER.log(Level.WARNING, "Acción desconocida recibida: {0}", action);
                    return ClientAction.UNKNOWN;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al interpretar la acción del cliente: {0}", input);
            LOGGER.log(Level.SEVERE, "Detalles del error:", e);
            return ClientAction.UNKNOWN;
        }
    }

    /**
     * Crea un mensaje de actualización del estado del juego.
     * @param gameStateJson El estado del juego en formato JSON
     * @return El mensaje formateado
     */
    public static String createGameStateUpdate(String gameStateJson) {
        if (gameStateJson == null || gameStateJson.isEmpty()) {
            LOGGER.log(Level.WARNING, "Intento de crear actualización de estado con JSON nulo o vacío");
            return GAME_STATE_UPDATE_PREFIX + "{}";
        }
        return GAME_STATE_UPDATE_PREFIX + gameStateJson;
    }

    /**
     * Extrae el estado del juego de un mensaje.
     * @param message El mensaje que contiene el estado del juego
     * @return El estado del juego en formato JSON o null si el formato es inválido
     */
    public static String extractGameState(String message) {
        if (message == null || message.isEmpty()) {
            LOGGER.log(Level.WARNING, "Se recibió un mensaje nulo o vacío para extracción de estado");
            return null;
        }

        if (!message.startsWith(GAME_STATE_UPDATE_PREFIX)) {
            LOGGER.log(Level.WARNING, "Formato de mensaje de estado inválido: {0}", message);
            return null;
        }

        return message.substring(GAME_STATE_UPDATE_PREFIX.length());
    }

    /**
     * Formatea una acción del cliente para su transmisión.
     * @param action La acción del cliente a formatear
     * @return El mensaje formateado
     */
    public static String formatClientAction(ClientAction action) {
        if (action == null) {
            LOGGER.log(Level.WARNING, "Intento de formatear una acción nula");
            return CLIENT_ACTION_PREFIX + ClientAction.UNKNOWN;
        }
        return CLIENT_ACTION_PREFIX + action.toString();
    }
}
