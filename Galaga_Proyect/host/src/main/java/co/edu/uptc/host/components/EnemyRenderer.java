package co.edu.uptc.host.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class EnemyRenderer {

     private Map<String, BufferedImage> enemySprites = new HashMap<>();

    public EnemyRenderer() {
        loadSprites();
    }

    private void loadSprites() {
        try {
            enemySprites.put("Bee", loadImage("/Images/spi.png"));
            enemySprites.put("Boss", loadImage("/Images/boss.png"));
        } catch (IOException e) {
            System.err.println("Error al cargar los sprites de los enemigos: " + e.getMessage());
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        URL imageUrl = getClass().getResource(path);
        if (imageUrl != null) {
            return ImageIO.read(imageUrl);
        } else {
            throw new IOException("No se pudo cargar la imagen: " + path);
        }
    }

    public void render(Graphics2D g2d, co.edu.uptc.server.models.enemy.Enemy enemy) {
    BufferedImage sprite = enemy.getSprite(); // Asumiendo que Enemy tiene un getSprite() público
    if (sprite != null) {
        g2d.drawImage(sprite, (int) enemy.getX(), (int) enemy.getY(), null);
    } else {
        g2d.setColor(Color.RED); // Color de fallback si no hay sprite
        g2d.fillRect((int) enemy.getX(), (int) enemy.getY(), 30, 30); // Dibuja un rectángulo de fallback
        System.err.println("Sprite nulo para el enemigo en X:" + enemy.getX() + ", Y:" + enemy.getY());
    }
}
}
