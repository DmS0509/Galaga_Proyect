package co.edu.uptc.server.models.enemy.movements;

import co.edu.uptc.server.models.enemy.Enemy;

/**
 * Define el patrón de movimiento para los enemigos.
 * Cada implementación debe proporcionar una lógica específica de movimiento.
 */
public interface MovementPattern {
    /**
     * Ejecuta un paso del patrón de movimiento en el enemigo.
     * @param enemy El enemigo a mover
     */
    void execute(Enemy enemy);

    /**
     * Reinicia el patrón de movimiento a su estado inicial.
     */
    default void reset() {
        // Implementación por defecto vacía
    }

    /**
     * Indica si el patrón de movimiento ha completado su ciclo.
     * @return true si el patrón ha completado su ciclo, false en caso contrario
     */
    default boolean isCompleted() {
        return false;
    }

    /**
     * Obtiene la posición X objetivo del movimiento actual.
     * @return la coordenada X objetivo o Float.NaN si no hay objetivo específico
     */
    default float getTargetX() {
        return Float.NaN;
    }

    /**
     * Obtiene la posición Y objetivo del movimiento actual.
     * @return la coordenada Y objetivo o Float.NaN si no hay objetivo específico
     */
    default float getTargetY() {
        return Float.NaN;
    }

    /**
     * Calcula la distancia al objetivo actual.
     * @param currentX Posición X actual
     * @param currentY Posición Y actual
     * @return la distancia al objetivo o Float.POSITIVE_INFINITY si no hay objetivo
     */
    default float getDistanceToTarget(float currentX, float currentY) {
        float targetX = getTargetX();
        float targetY = getTargetY();
        if (Float.isNaN(targetX) || Float.isNaN(targetY)) {
            return Float.POSITIVE_INFINITY;
        }
        float dx = targetX - currentX;
        float dy = targetY - currentY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Indica si el patrón está en pausa.
     * @return true si el patrón está en pausa, false en caso contrario
     */
    default boolean isPaused() {
        return false;
    }

    /**
     * Pausa el patrón de movimiento.
     */
    default void pause() {
        // Implementación por defecto vacía
    }

    /**
     * Reanuda el patrón de movimiento.
     */
    default void resume() {
        // Implementación por defecto vacía
    }
}
