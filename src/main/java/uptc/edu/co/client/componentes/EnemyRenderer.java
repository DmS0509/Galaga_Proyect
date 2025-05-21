package uptc.edu.co.client.componentes;

import uptc.edu.co.server.model.enemy.Enemy;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

    public void render(Graphics2D g2d, Enemy enemy) {
        BufferedImage sprite = enemySprites.get(enemy.getSpritePath());
        if (sprite != null) {
            g2d.drawImage(sprite, (int) enemy.getX(), (int) enemy.getY(), null);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect((int) enemy.getX(), (int) enemy.getY(), 30, 30);
            System.err.println("Sprite no encontrado para: " + enemy.getSpritePath());
        }
    }
}
