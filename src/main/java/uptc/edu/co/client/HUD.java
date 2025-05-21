package uptc.edu.co.client;

import uptc.edu.co.client.controller.GameState;
import javax.swing.*;
import java.awt.*;

public class HUD extends JPanel {
    private GameState gameState;

    public HUD() {
        setOpaque(false);
        setPreferredSize(new Dimension(800, 50));
    }

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));

        if (gameState != null) {

            if (gameState.getPlayers() != null && !gameState.getPlayers().isEmpty()) {
                gameState.getPlayers().values().stream().findFirst().ifPresent(player -> {
                    g2d.drawString("Puntuación: " + player.getScore(), 10, 30);
                    g2d.drawString("Vidas: " + player.getLives(), 200, 30);
                });
            } else {
                g2d.drawString("Puntuación: 0", 10, 30);
                g2d.drawString("Vidas: 0", 200, 30);
            }

            g2d.drawString("Oleada: " + gameState.getCurrentWave(), 400, 30);
        } else {
            g2d.drawString("Cargando...", 10, 30);
        }
    }
}