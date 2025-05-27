package co.edu.uptc.host.models;

public class Player {

    private String playerId;
    private float x;
    private float y;
    private int score;
    private int lives;

    public Player(String playerId, float x, float y, int score, int lives) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.score = score;
        this.lives = lives;
    }

    public String getPlayerId() {
        return playerId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
}
