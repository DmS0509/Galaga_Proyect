package co.edu.uptc.server.models.enemy.movements;

import co.edu.uptc.server.models.enemy.Enemy;

public class ZigZagPattern implements MovementPattern{

    private float speed, amplitude;
    private float phase = 0f;

    public ZigZagPattern(float speed, float amplitude) {
        this.speed = speed;
        this.amplitude = amplitude;
    }

    @Override
    public void execute(Enemy enemy) {
        phase += 0.1;
        enemy.setX(enemy.getX() + (float) (Math.sin(phase) * amplitude));
        enemy.setY(enemy.getY() + speed);
    }
}
