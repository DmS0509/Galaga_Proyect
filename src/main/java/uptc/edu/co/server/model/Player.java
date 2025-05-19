package uptc.edu.co.server.model;

import java.util.ArrayList;
import java.util.List;


public class Player {
    private String playerId; 
    private float x;
    private float y;
    private int health;
    private int score;
    private float speed;
    private long lastShootTime;
    private long shootCooldown = 500; 
    private List<Proyectile> projectiles;
    private final float width = 32; 
    private final float height = 32;

    public Player(String playerId, float initialX, float initialY, float speed) {
        this.playerId = playerId;
        this.x = initialX;
        this.y = initialY;
        this.speed = speed;
        this.health = 3; 
        this.score = 0;
        this.projectiles = new ArrayList<>();
        this.lastShootTime = 0;
    }

    public void moveLeft(float deltaTime) {
        x -= speed * deltaTime;
        if (x < 0) {
            x = 0;
        }
    }

    public void moveRight(float deltaTime) {
        x += speed * deltaTime;
    }

    public void shoot(float projectileSpeed) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShootTime >= shootCooldown) {
            Proyectile projectile = new Proyectile(x, y - 10, -projectileSpeed, playerId); // Asumiendo constructor con ownerId
            projectiles.add(projectile);
            lastShootTime = currentTime;
        }
    }

    public boolean checkCollision(float targetX, float targetY, float targetWidth, float targetHeight) {
        return x < targetX + targetWidth &&
               x + width > targetX &&
               y < targetY + targetHeight &&
               y + height > targetY;
    }

    public void updateProjectiles(float deltaTime) {
        List<Proyectile> toRemove = new ArrayList<>();
        for (Proyectile projectile : projectiles) {
            projectile.move(deltaTime);
            if (projectile.getY() < -50) {
                toRemove.add(projectile);
            }
        }
        projectiles.removeAll(toRemove);
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            System.out.println("Jugador " + playerId + " ha sido derrotado.");
        }
    }

    public void addScore(int points) {
        this.score += points;
    }

    public String getPlayerId() {
        return playerId;
    }

    public float getX() {
        return x;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public List<Proyectile> getProjectiles() {
        return projectiles;
    }

    public void setProjectiles(List<Proyectile> projectiles) {
        this.projectiles = projectiles;
    }
}