package co.edu.uptc.server.models.enemy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import co.edu.uptc.server.models.enemy.movements.MovementPattern;

public abstract class Enemy {

     private float x;
    private float y;
    private int health;
    private final int killPoints;
    private MovementPattern movementPattern;
    private String spritePath;
    private BufferedImage sprite;

    public Enemy(float initialX, float initialY, int health, int killPoints, String spritePath) {
        this.x = initialX;
        this.killPoints=killPoints;
        this.y = initialY;
        this.health = health;
        this.spritePath = spritePath;
        this.sprite = loadImage(spritePath);
    }

    public abstract void update();
    public abstract void render(Graphics2D g2d);

    public boolean checkCollision(float targetX, float targetY, float targetWidth, float targetHeight) {
    return x < targetX + targetWidth &&
           x + getWidth() > targetX &&
           y < targetY + targetHeight &&
           y + getHeight() > targetY;
}

    public float getWidth() {
    if (sprite != null) {
        return sprite.getWidth();
    }
    return 30; 
}

public float getHeight() {
    if (sprite != null) {
        return sprite.getHeight();
    }
    return 30;
}

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    private BufferedImage loadImage(String path) {
        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl != null) {
                return ImageIO.read(imageUrl);
            } else {
                System.err.println("No se pudo cargar la imagen: " + path);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error al cargar la imagen: " + path + ": " + e.getMessage());
            return null;
        }
    }

    public float getX() {
        return x;
    }

    public int getKillPoints() {
        return killPoints;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public MovementPattern getMovementPattern() {
        return movementPattern;
    }

    public void setMovementPattern(MovementPattern movementPattern) {
        this.movementPattern = movementPattern;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
        this.sprite = loadImage(spritePath);
    }

}
