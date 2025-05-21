package uptc.edu.co.client.model;

public class Proyectile {
    private float x;
    private float y;
    private String ownerId; 

    public Proyectile(float x, float y, String ownerId) {
        this.x = x;
        this.y = y;
        this.ownerId = ownerId;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}