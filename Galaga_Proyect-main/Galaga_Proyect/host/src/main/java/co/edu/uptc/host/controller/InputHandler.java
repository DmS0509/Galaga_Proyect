package co.edu.uptc.host.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.edu.uptc.server.network.ClientAction;

/**
 * Maneja la entrada del teclado para el juego Galaga.
 * Convierte los eventos del teclado en acciones del juego.
 */
public final class InputHandler implements KeyListener {
    private static final Logger LOGGER = Logger.getLogger(InputHandler.class.getName());
    private final GameClient gameClient;
    private volatile boolean isMovingLeft = false;
    private volatile boolean isMovingRight = false;

    /**
     * Constructor del manejador de entrada.
     * @param gameClient El cliente del juego que recibirá las acciones
     * @throws IllegalArgumentException si gameClient es null
     */
    public InputHandler(GameClient gameClient) {
        if (gameClient == null) {
            throw new IllegalArgumentException("GameClient no puede ser null");
        }
        this.gameClient = gameClient;
        LOGGER.log(Level.INFO, "InputHandler inicializado correctamente");
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No se necesita implementación
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e == null) {
            LOGGER.log(Level.WARNING, "Se recibió un evento de teclado nulo");
            return;
        }
        
        try {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_LEFT:
                    if (!isMovingLeft) {
                        isMovingLeft = true;
                        gameClient.sendAction(ClientAction.MOVE_LEFT);
                        LOGGER.log(Level.FINE, "Iniciado movimiento a la izquierda");
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (!isMovingRight) {
                        isMovingRight = true;
                        gameClient.sendAction(ClientAction.MOVE_RIGHT);
                        LOGGER.log(Level.FINE, "Iniciado movimiento a la derecha");
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    gameClient.sendAction(ClientAction.SHOOT);
                    LOGGER.log(Level.FINE, "Acción de disparo enviada");
                    break;
                case KeyEvent.VK_ESCAPE:
                    gameClient.sendAction(ClientAction.DISCONNECT);
                    LOGGER.log(Level.INFO, "Solicitud de desconexión enviada");
                    break;
                default:
                    LOGGER.log(Level.FINE, "Tecla no manejada: {0}", KeyEvent.getKeyText(key));
                    break;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al procesar tecla presionada: {0}", KeyEvent.getKeyText(e.getKeyCode()));
            LOGGER.log(Level.SEVERE, "Detalles del error:", ex);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e == null) {
            LOGGER.log(Level.WARNING, "Se recibió un evento de liberación de tecla nulo");
            return;
        }
        
        try {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_LEFT:
                    isMovingLeft = false;
                    LOGGER.log(Level.FINE, "Detenido movimiento a la izquierda");
                    break;
                case KeyEvent.VK_RIGHT:
                    isMovingRight = false;
                    LOGGER.log(Level.FINE, "Detenido movimiento a la derecha");
                    break;
                default:
                    LOGGER.log(Level.FINE, "Liberación de tecla no manejada: {0}", KeyEvent.getKeyText(key));
                    break;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al procesar tecla liberada: {0}", KeyEvent.getKeyText(e.getKeyCode()));
            LOGGER.log(Level.SEVERE, "Detalles del error:", ex);
        }
    }
}
