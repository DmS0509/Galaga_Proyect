
package uptc.edu.co.server.model.enemy;

import java.awt.Graphics2D;

public class Bee extends Enemy {
    public Bee() {
        super(0, 0, EnemyType.BEE.getBaseHealth(), 10, EnemyType.BEE.getSpritePath());
        setMovementPattern(EnemyType.BEE.getMovementPattern());
    }

    @Override
    public void update() {
        if (getMovementPattern() != null) {
            getMovementPattern().execute(this);
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        if (getSprite() != null) {
            g2d.drawImage(getSprite(), (int) getX(), (int) getY(), null);
        } else {
            g2d.setColor(java.awt.Color.YELLOW);
            g2d.fillRect((int) getX(), (int) getY(), 30, 30);
            System.out.println("No se pudo renderizar la abeja (sprite no cargado).");
        }
    }


}
