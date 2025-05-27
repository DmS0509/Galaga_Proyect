package co.edu.uptc.host.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import co.edu.uptc.host.components.EnemyRenderer;
import co.edu.uptc.host.controller.GameClient;
import co.edu.uptc.host.controller.GameState;
import co.edu.uptc.server.models.Player;
import co.edu.uptc.server.models.Proyectile;
import co.edu.uptc.server.models.enemy.Enemy;

public class GameScreen extends JPanel {

    private GameClient client; // Ahora se puede pasar nulo y setear después
    private GameState gameState;
    private EnemyRenderer enemyRenderer;
    private Image playerSprite;

    public GameScreen(GameClient client) {
        this.client = client;
        this.gameState = null; // Inicialmente nulo, hasta que el servidor envíe el primer estado
        this.enemyRenderer = new EnemyRenderer();
        setFocusable(true); 
        requestFocusInWindow(); 

        try {
            playerSprite = new ImageIcon(getClass().getResource("/Images/naveGalaga.png")).getImage();
            if (playerSprite == null) {
                System.err.println("GameScreen: No se pudo cargar la imagen del jugador. Verifique la ruta /Images/naveGalaga.png");
            } else {
                System.out.println("GameScreen: Sprite del jugador cargado correctamente.");
            }
        } catch (Exception e) {
            System.err.println("GameScreen: Error al cargar la imagen del jugador: " + e.getMessage());
            playerSprite = null;
        }
        setBackground(Color.BLACK); // Asegura que el fondo sea negro
        System.out.println("GameScreen: Constructor iniciado.");
    }

    public void setGameClient(GameClient client) {
        this.client = client;
        System.out.println("GameScreen: GameClient asignado.");
    }

    public GameClient getGameClient() {
        return client;
    }


    public void updateGameState(GameState newGameState) {
        this.gameState = newGameState;
         SwingUtilities.invokeLater(() -> repaint()); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpia el panel
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.WHITE);
        g2d.drawString("GameScreen activo", getWidth() / 2 - 50, getHeight() / 2);

        if (gameState != null) {
            Map<String, Player> players = gameState.getPlayers();
            if (players != null && !players.isEmpty()) {
                for (Player player : players.values()) {
                    if (playerSprite != null) {
                        g2d.drawImage(playerSprite, (int) player.getX(), (int) player.getY(), 32, 32, null);
                    } else {
                        g2d.setColor(Color.GREEN); // Fallback si no hay sprite
                        g2d.fillRect((int) player.getX(), (int) player.getY(), 32, 32);
                        System.err.println("GameScreen: Sprite de jugador es nulo, dibujando rectángulo.");
                    }
                }
            } else {
            }

            List<Enemy> enemies = gameState.getEnemies();
            if (enemies != null && !enemies.isEmpty()) {
                for (co.edu.uptc.server.models.enemy.Enemy enemy : enemies) {
                    enemyRenderer.render(g2d, enemy);
                }
            } else {
            }

            List<co.edu.uptc.server.models.Proyectile> projectiles = gameState.getProjectiles();
            if (projectiles != null && !projectiles.isEmpty()) {
                g2d.setColor(Color.WHITE);
                // System.out.println("GameScreen: Dibujando proyectiles..."); // Comentar si es ruidoso
                for (Proyectile projectile : projectiles) {
                    g2d.fillRect((int) projectile.getX() - 2, (int) projectile.getY() - 5, 4, 10); // Ejemplo de proyectil
                }
            } else {
                // System.out.println("GameScreen: No hay proyectiles en GameState."); // Comentar si es ruidoso
            }

            g2d.setColor(Color.YELLOW);
            g2d.drawString("Oleada: " + gameState.getCurrentWave(), 10, 20);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Esperando el estado del juego...", getWidth() / 2 - 100, getHeight() / 2 + 30);
            System.out.println("GameScreen: GameState es nulo, mostrando mensaje de espera.");
        }
        g2d.dispose();
    }
}
