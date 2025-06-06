package co.edu.uptc.server.models.enemy.movements;

import co.edu.uptc.server.models.enemy.Enemy;

public class DirectDivePattern implements MovementPattern{

    private float speed;
    private float targetY = Float.MAX_VALUE;

    public DirectDivePattern(float speed) {
        this.speed = speed;
    }

    public DirectDivePattern(float speed, float targetY) {
        this.speed = speed;
        this.targetY = targetY;
    }

    @Override
    public void execute(Enemy enemy) {
        enemy.setY(enemy.getY() + speed);
        if (enemy.getY() >= targetY && targetY != Float.MAX_VALUE) {
            enemy.setY(targetY);
        }
    }
}
