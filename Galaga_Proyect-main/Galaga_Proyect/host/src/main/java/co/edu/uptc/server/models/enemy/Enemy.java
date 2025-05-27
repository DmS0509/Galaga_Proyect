package co.edu.uptc.server.models.enemy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import co.edu.uptc.server.models.enemy.movements.MovementPattern;

public abstract class Enemy {
    private static final Logger LOGGER = Logger.getLogger(Enemy.class.getName());
    
    // Constantes
    protected static final float DEFAULT_WIDTH = 30f;
    protected static final float DEFAULT_HEIGHT = 30f;
    
    private float x;
    private float y;
    private int health;
    private final int killPoints;
    private MovementPattern movementPattern;
    private String spritePath;
    private BufferedImage sprite;
    private final float width;
    private final float height;

    public Enemy(float initialX, float initialY, int health, int killPoints, String spritePath) {
        this.x = initialX;
        this.y = initialY;
        this.health = health;
        this.killPoints = killPoints;
        this.spritePath = spritePath;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.sprite = loadImage(spritePath);
        LOGGER.fine("Enemigo creado en posición (" + x + ", " + y + ")");
    }

    public abstract void update();
    public abstract void render(Graphics2D g2d);

    public boolean checkCollision(float targetX, float targetY) {
        return x <= targetX + width && 
               x + width >= targetX && 
               y <= targetY + height && 
               y + height >= targetY;
    }

    public void takeDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
        LOGGER.fine("Enemigo recibió " + damage + " de daño. Salud restante: " + health);
    }

    private BufferedImage loadImage(String path) {
        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                return ImageIO.read(imageUrl);
            }
            LOGGER.warning("No se pudo encontrar la imagen: " + path);
            return null;
        } catch (IOException e) {
            LOGGER.severe("Error al cargar la imagen " + path + ": " + e.getMessage());
            return null;
        }
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHealth() { return health; }
    public int getKillPoints() { return killPoints; }
    public MovementPattern getMovementPattern() { return movementPattern; }
    public BufferedImage getSprite() { return sprite; }
    public String getSpritePath() { return spritePath; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    // Setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setHealth(int health) { this.health = Math.max(0, health); }
    public void setMovementPattern(MovementPattern movementPattern) { this.movementPattern = movementPattern; }
    
    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
        BufferedImage newSprite = loadImage(spritePath);
        if (newSprite != null) {
            this.sprite = newSprite;
        }
    }
    
    protected void updatePosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
        LOGGER.finest("Enemigo actualizado a posición (" + x + ", " + y + ")");
    }
}
