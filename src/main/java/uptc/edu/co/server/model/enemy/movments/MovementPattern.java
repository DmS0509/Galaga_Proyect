package uptc.edu.co.server.model.enemy.movments;

import uptc.edu.co.server.model.enemy.Enemy;

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
