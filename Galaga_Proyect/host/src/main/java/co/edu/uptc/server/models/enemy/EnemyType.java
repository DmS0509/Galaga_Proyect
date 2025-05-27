package co.edu.uptc.server.models.enemy;

import co.edu.uptc.server.models.enemy.movements.BossPattern;
import co.edu.uptc.server.models.enemy.movements.DirectDivePattern;
import co.edu.uptc.server.models.enemy.movements.MovementPattern;
import co.edu.uptc.server.models.enemy.movements.ZigZagPattern;

public enum EnemyType {

    BEE(
            1,
            50,
            0.8f,
            "/Images/bee.png"
    ),
    SPI(
            1,
            80,
            1.0f,
            "/Images/spi.png"
    ),
    BOSS(
            10,
            1000,
            0.5f,
            "/Images/boss.png",
            2.0f, 
            100.0f, 
            150.0f,
            80.0f, 
            300, 
            240 
    );
    private final int baseHealth;
    private final int killPoints;
    private final float baseSpeed;
    private final String spritePath;
    private final float bossSpeed;
    private final float entryTargetY;
    private final float patrolAmplitudeX;
    private final float circleRadius;
    private final int patrolDuration;
    private final int circleStrafeDuration;

    EnemyType(int health, int points, float speed, String spritePath) {
        this.baseHealth = health;
        this.killPoints = points;
        this.baseSpeed = speed;
        this.spritePath = spritePath;
        this.bossSpeed = 0f;
        this.entryTargetY = 0f;
        this.patrolAmplitudeX = 0f;
        this.circleRadius = 0f;
        this.patrolDuration = 0;
        this.circleStrafeDuration = 0;
    }

    EnemyType(int health, int points, float speed, String spritePath,
              float bossSpeed, float entryTargetY, float patrolAmplitudeX,
              float circleRadius, int patrolDuration, int circleStrafeDuration) {
        this.baseHealth = health;
        this.killPoints = points;
        this.baseSpeed = speed;
        this.spritePath = spritePath;
        this.bossSpeed = bossSpeed;
        this.entryTargetY = entryTargetY;
        this.patrolAmplitudeX = patrolAmplitudeX;
        this.circleRadius = circleRadius;
        this.patrolDuration = patrolDuration;
        this.circleStrafeDuration = circleStrafeDuration;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public int getKillPoints() {
        return killPoints;
    }

    public float getBaseSpeed() {
        return baseSpeed;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public long getAttackCooldown() {
        return switch (this) {
            case BEE -> 2000L;
            case SPI -> 1500L;
            case BOSS -> 3000L;
        };
    }

    public Enemy createEnemy() {
        return switch (this) {
            case BEE -> new Bee();
            case SPI -> new Spi();
            case BOSS -> new Boss();
        };
    }

    public MovementPattern getMovementPattern() {
        return switch (this) {
            case BEE -> new ZigZagPattern(0.1f * getBaseSpeed(), 30f); 
            case SPI -> new DirectDivePattern(1.0f * getBaseSpeed()); 
            case BOSS -> new BossPattern(bossSpeed, entryTargetY, patrolAmplitudeX, circleRadius, patrolDuration, circleStrafeDuration);
        };
    }
}
