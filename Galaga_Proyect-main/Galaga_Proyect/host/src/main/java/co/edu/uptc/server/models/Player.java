package co.edu.uptc.server.models;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Player {
    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
    
    // Constantes del jugador
    private static final float DEFAULT_WIDTH = 32f;
    private static final float DEFAULT_HEIGHT = 32f;
    private static final int INITIAL_HEALTH = 3;
    private static final long DEFAULT_SHOOT_COOLDOWN = 500L;
    private static final float DEFAULT_INITIAL_X = 400f;
    private static final float DEFAULT_INITIAL_Y = 550f;
    private static final float DEFAULT_SPEED = 200f;
    
    private final String playerId;
    private float x;
    private float y;
    private int health;
    private int score;
    private float speed;
    private long lastShootTime;
    private final long shootCooldown;
    private final List<Proyectile> projectiles;
    private final float width;
    private final float height;
    private boolean isAlive;

    public Player(String playerId) {
        this(playerId, DEFAULT_INITIAL_X, DEFAULT_INITIAL_Y, DEFAULT_SPEED);
    }

    public Player(String playerId, float initialX, float initialY, float speed) {
        this.playerId = playerId;
        this.x = initialX;
        this.y = initialY;
        this.speed = speed;
        this.health = INITIAL_HEALTH;
        this.score = 0;
        this.projectiles = new ArrayList<>();
        this.lastShootTime = 0;
        this.shootCooldown = DEFAULT_SHOOT_COOLDOWN;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.isAlive = true;
        LOGGER.info("Nuevo jugador creado: " + playerId);
    }

    public void moveLeft(float deltaTime) {
        if (!isAlive) return;
        x = Math.max(0, x - speed * deltaTime);
    }

    public void moveRight(float deltaTime) {
        if (!isAlive) return;
        x = Math.min(800 - width, x + speed * deltaTime); // Asumiendo un ancho de pantalla de 800
    }

    public void shoot(float projectileSpeed) {
        if (!isAlive) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShootTime >= shootCooldown) {
            Proyectile projectile = new Proyectile(
                x + (width / 2), // Centrar el proyectil
                y - 10,
                -projectileSpeed,
                playerId
            );
            projectiles.add(projectile);
            lastShootTime = currentTime;
            LOGGER.fine("Jugador " + playerId + " disparó un proyectil");
        }
    }

    public void updateProjectiles(float deltaTime) {
        if (projectiles.isEmpty()) return;
        
        List<Proyectile> toRemove = new ArrayList<>();
        for (Proyectile projectile : projectiles) {
            projectile.move(deltaTime);
            if (isProjectileOutOfBounds(projectile)) {
                toRemove.add(projectile);
            }
        }
        if (!toRemove.isEmpty()) {
            projectiles.removeAll(toRemove);
        }
    }

    private boolean isProjectileOutOfBounds(Proyectile projectile) {
        return projectile.getY() < -50 || projectile.getY() > 650 ||
               projectile.getX() < -50 || projectile.getX() > 850;
    }

    public boolean checkCollision(float targetX, float targetY, float targetWidth, float targetHeight) {
        return x < targetX + targetWidth &&
               x + width > targetX &&
               y < targetY + targetHeight &&
               y + height > targetY;
    }

    public void takeDamage(int damage) {
        if (!isAlive) return;
        
        this.health = Math.max(0, this.health - damage);
        if (this.health <= 0) {
            this.isAlive = false;
            LOGGER.info("Jugador " + playerId + " ha sido derrotado con " + score + " puntos");
        }
    }

    public void addScore(int points) {
        if (!isAlive) return;
        
        this.score += points;
        LOGGER.fine("Jugador " + playerId + " ganó " + points + " puntos. Total: " + score);
    }

    // Getters y setters
    public String getPlayerId() { return playerId; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHealth() { return health; }
    public int getScore() { return score; }
    public float getSpeed() { return speed; }
    public List<Proyectile> getProjectiles() { return projectiles; }
    public boolean isAlive() { return isAlive; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void setX(float x) { this.x = Math.max(0, Math.min(800 - width, x)); }
    public void setY(float y) { this.y = y; }
    public void setHealth(int health) { 
        this.health = Math.max(0, health);
        this.isAlive = this.health > 0;
    }
    public void setSpeed(float speed) { this.speed = Math.max(0, speed); }
}
