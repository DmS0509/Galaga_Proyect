package co.edu.uptc.server.models;

public class Proyectile {

    private float x;
    private float y;
    private float speedY;
    private String ownerId; 
    private boolean active = true;

    public Proyectile(float initialX, float initialY, float speedY, String ownerId) {
        this.x = initialX;
        this.y = initialY;
        this.speedY = speedY;
        this.ownerId = ownerId;
    }

    public void move(float deltaTime) {
        this.y += speedY * deltaTime;
    }

    public boolean checkCollision(float targetX, float targetY, float targetWidth, float targetHeight) {
        return x >= targetX && x < targetX + targetWidth &&
               y >= targetY && y < targetY + targetHeight &&
               active;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
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

    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
