package uptc.edu.co.server.model.enemy;

import java.awt.Graphics2D;

public class Spi extends Enemy{
    public Spi() {
        super(0, 0, EnemyType.SPI.getBaseHealth(), 50,EnemyType.SPI.getSpritePath());
        setMovementPattern(EnemyType.SPI.getMovementPattern());
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
            g2d.setColor(java.awt.Color.CYAN);
            g2d.fillRect((int) getX(), (int) getY(), 30, 30);
            System.out.println("No se pudo renderizar el Spi (sprite no cargado).");
        }
    }

}
