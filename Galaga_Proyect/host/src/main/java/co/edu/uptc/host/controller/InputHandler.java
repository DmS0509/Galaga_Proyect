package co.edu.uptc.host.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import co.edu.uptc.server.network.ClientAction;

public class InputHandler implements KeyListener{

     private GameClient gameClient;

    public InputHandler(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            gameClient.sendAction(ClientAction.MOVE_LEFT);
        } else if (key == KeyEvent.VK_RIGHT) {
            gameClient.sendAction(ClientAction.MOVE_RIGHT);
        } else if (key == KeyEvent.VK_SPACE) {
            gameClient.sendAction(ClientAction.SHOOT);
        } else if (key == KeyEvent.VK_P) {
            gameClient.sendAction(ClientAction.PAUSE_GAME);
        } else if (key == KeyEvent.VK_ESCAPE) {
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
        } else if (key == KeyEvent.VK_RIGHT) {

        }
    }
}
