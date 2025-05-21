package uptc.edu.co.client.View;

import uptc.edu.co.client.componentes.EnemyRenderer;
import uptc.edu.co.client.controller.GameClient;
import uptc.edu.co.client.controller.GameState;
import uptc.edu.co.client.model.Player;
import uptc.edu.co.server.model.enemy.Enemy;
import uptc.edu.co.server.model.Proyectile;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GameScreen extends JPanel {
    private GameClient client;
    private GameState gameState;
    private EnemyRenderer enemyRenderer; 

    public GameScreen(GameClient client) {
        this.client = client;
        this.gameState = null;
        this.enemyRenderer = new EnemyRenderer();
        setFocusable(true);
        requestFocusInWindow();
    }

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (gameState != null) {
            Map<String, Player> players = gameState.getPlayers();
            if (players != null) {
                for (Player player : players.values()) {
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect((int) player.getX(), (int) player.getY(), 32, 32);
                }
            }

            List<Enemy> enemies = gameState.getEnemies();
            if (enemies != null) {
                for (uptc.edu.co.server.model.enemy.Enemy enemy : enemies) {
                    enemyRenderer.render(g2d, enemy); 
                }
            }
            List<uptc.edu.co.server.model.Proyectile> projectiles = gameState.getProjectiles();
            if (projectiles != null) {
                g2d.setColor(Color.WHITE); 
                for (Proyectile projectile : projectiles) {
                    g2d.fillRect((int) projectile.getX() - 2, (int) projectile.getY() - 5, 4, 10); // Ejemplo de proyectil
                }
            }
            g2d.setColor(Color.YELLOW);
            g2d.drawString("Oleada: " + gameState.getCurrentWave(), 10, 20);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Esperando el estado del juego...", getWidth() / 2 - 100, getHeight() / 2);
        }
    }

    public void startGame() {
        requestFocusInWindow();
    }
}