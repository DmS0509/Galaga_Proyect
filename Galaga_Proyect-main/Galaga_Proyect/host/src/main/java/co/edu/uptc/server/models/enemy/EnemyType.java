package co.edu.uptc.server.models.enemy;

import java.util.logging.Logger;

import co.edu.uptc.server.models.enemy.movements.BossPattern;
import co.edu.uptc.server.models.enemy.movements.DirectDivePattern;
import co.edu.uptc.server.models.enemy.movements.MovementPattern;
import co.edu.uptc.server.models.enemy.movements.ZigZagPattern;

/**
 * Define los diferentes tipos de enemigos disponibles en el juego.
 * Cada tipo tiene sus propias características y patrones de movimiento.
 */
public enum EnemyType {
    BEE(
        new EnemyStats(1, 50, 0.8f),
        "/Images/bee.png"
    ),
    SPI(
        new EnemyStats(1, 80, 1.0f),
        "/Images/spi.png"
    ),
    BOSS(
        new EnemyStats(10, 1000, 0.5f),
        "/Images/boss.png",
        new BossStats(2.0f, 100.0f, 150.0f, 80.0f, 300, 240)
    );

    private static final Logger LOGGER = Logger.getLogger(EnemyType.class.getName());

    private final EnemyStats stats;
    private final String spritePath;
    private final BossStats bossStats;

    /**
     * Constructor para enemigos normales
     */
    EnemyType(EnemyStats stats, String spritePath) {
        this(stats, spritePath, null);
    }

    /**
     * Constructor para jefes
     */
    EnemyType(EnemyStats stats, String spritePath, BossStats bossStats) {
        this.stats = stats;
        this.spritePath = spritePath;
        this.bossStats = bossStats;
    }

    public Enemy createEnemy() {
        Enemy enemy = switch (this) {
            case BEE -> new Bee();
            case SPI -> new Spi();
            case BOSS -> new Boss();
        };
        LOGGER.fine("Creado nuevo enemigo de tipo " + this.name());
        return enemy;
    }

    public MovementPattern getMovementPattern() {
        return switch (this) {
            case BEE -> new ZigZagPattern(0.1f * stats.speed(), 30f);
            case SPI -> new DirectDivePattern(stats.speed());
            case BOSS -> new BossPattern(
                bossStats.speed(),
                bossStats.entryTargetY(),
                bossStats.patrolAmplitudeX(),
                bossStats.circleRadius(),
                bossStats.patrolDuration(),
                bossStats.circleStrafeDuration()
            );
        };
    }

    public long getAttackCooldown() {
        return switch (this) {
            case BEE -> 2000L;
            case SPI -> 1500L;
            case BOSS -> 3000L;
        };
    }

    // Getters
    public int getBaseHealth() { return stats.health(); }
    public int getKillPoints() { return stats.points(); }
    public float getBaseSpeed() { return stats.speed(); }
    public String getSpritePath() { return spritePath; }
}

/**
 * Record que contiene las estadísticas básicas de un enemigo
 */
record EnemyStats(int health, int points, float speed) {
    EnemyStats {
        if (health <= 0) throw new IllegalArgumentException("La salud debe ser positiva");
        if (points < 0) throw new IllegalArgumentException("Los puntos no pueden ser negativos");
        if (speed <= 0) throw new IllegalArgumentException("La velocidad debe ser positiva");
    }
}

/**
 * Record que contiene las estadísticas específicas del jefe
 */
record BossStats(
    float speed,
    float entryTargetY,
    float patrolAmplitudeX,
    float circleRadius,
    int patrolDuration,
    int circleStrafeDuration
) {
    BossStats {
        if (speed <= 0) throw new IllegalArgumentException("La velocidad del jefe debe ser positiva");
        if (patrolDuration <= 0) throw new IllegalArgumentException("La duración de patrulla debe ser positiva");
        if (circleStrafeDuration <= 0) throw new IllegalArgumentException("La duración del círculo debe ser positiva");
    }
}
