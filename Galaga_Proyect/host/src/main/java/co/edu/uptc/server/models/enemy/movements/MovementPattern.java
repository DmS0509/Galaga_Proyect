package co.edu.uptc.server.models.enemy.movements;

import co.edu.uptc.server.models.enemy.Enemy;

public interface MovementPattern {

    void execute(Enemy enemy);

    default void reset() {
    }

    default boolean isCompleted() {
        return false;
    }

    default float getTargetX() {
        return Float.NaN;
    }

    default float getTargetY() {
        return Float.NaN;
    }
}
