package co.edu.uptc.server.models.enemy;

import java.awt.Graphics2D;

public class Boss extends Enemy{

     private int attackPhase = 1;

    public Boss() {
        super(0, 0, EnemyType.BOSS.getBaseHealth(), 100,EnemyType.BOSS.getSpritePath());
        setMovementPattern(EnemyType.BOSS.getMovementPattern());
    }

    public void nextAttackPhase() {
        attackPhase++;
    }

    public int getAttackPhase() {
        return attackPhase;
    }

    @Override
    public void update() {
        if (getMovementPattern() != null) {
            getMovementPattern().execute(this);
        }
        System.out.println("El jefe está actualizando su estado. Fase de ataque: " + attackPhase);
    }

    @Override
    public void render(Graphics2D g2d) {
        if (getSprite() != null) {
            g2d.drawImage(getSprite(), (int) getX(), (int) getY(), null);
        } else {
            g2d.setColor(java.awt.Color.RED);
            g2d.fillRect((int) getX(), (int) getY(), 60, 60);
            System.out.println("No se pudo renderizar el JEFE (sprite no cargado).");
        }
    }
}
