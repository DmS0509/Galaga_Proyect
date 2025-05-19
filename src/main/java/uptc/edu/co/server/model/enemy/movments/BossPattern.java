package uptc.edu.co.server.model.enemy.movments;

import uptc.edu.co.server.model.enemy.Enemy;

public class BossPattern implements MovementPattern{
    
        private static final int PHASE_ENTERING = 0;
        private static final int PHASE_PATROLLING_HORIZONTAL = 1;
        private static final int PHASE_CIRCLE_STRAFE = 2;
        private int currentPhase;
        private float generalSpeed;
        private float entryTargetY;
        private float patrolCenterX;
        private float patrolAmplitudeX;
        private float patrolAngle; 
        private float patrolSpeedFactor; 
        private float circleCenterX, circleCenterY;
        private float circleRadius;
        private float circleAngle;
        private float circleSpeedFactor; 
        private int phaseTimer;
        private int patrolDuration; 
        private int circleStrafeDuration; 
    
        private boolean initializedPositions = false;
    
    
        public BossPattern(float speed, float entryTargetY, float patrolAmplitudeX, float circleRadius, int patrolDuration, int circleStrafeDuration) {
            this.generalSpeed = speed;
            this.entryTargetY = entryTargetY;
            this.patrolAmplitudeX = patrolAmplitudeX;
            this.circleRadius = circleRadius;
            this.patrolDuration = patrolDuration;
            this.circleStrafeDuration = circleStrafeDuration;
    
            this.currentPhase = PHASE_ENTERING;
            this.patrolAngle = 0f;
            this.patrolSpeedFactor = 0.05f; 
            this.circleAngle = 0f;
            this.circleSpeedFactor = 0.03f; 
            this.phaseTimer = 0;
        }
    
        private void initializePatrolAndCircleParams(Enemy enemy) {
            this.patrolCenterX = enemy.getX();
            this.circleCenterX = enemy.getX();
            this.circleCenterY = enemy.getY() + circleRadius + 10; // Que el centro del círculo esté más abajo
            this.initializedPositions = true;
        }
    
        @Override
        public void execute(Enemy enemy) {
            if (!initializedPositions && enemy.getY() >= entryTargetY) {
                initializePatrolAndCircleParams(enemy);
            }
            phaseTimer++;  
            switch (currentPhase) {
                case PHASE_ENTERING:
                    moveEnter(enemy);
                    if (enemy.getY() >= entryTargetY) {
                        if (!initializedPositions) {
                            initializePatrolAndCircleParams(enemy);
                        }
                        currentPhase = PHASE_PATROLLING_HORIZONTAL;
                        phaseTimer = 0;
                        enemy.setY(entryTargetY); 
                    }
                    break;
    
                case PHASE_PATROLLING_HORIZONTAL:
                    movePatrolHorizontal(enemy);
                    if (phaseTimer >= patrolDuration) {
                        currentPhase = PHASE_CIRCLE_STRAFE;
                        phaseTimer = 0;
                        this.circleCenterX = enemy.getX();
                        this.circleCenterY = enemy.getY() + circleRadius;
                        this.circleAngle = (float) (Math.PI / 2f);
                    }
                    break;
    
                case PHASE_CIRCLE_STRAFE:
                    moveCircleStrafe(enemy);
                    if (phaseTimer >= circleStrafeDuration) {
                        currentPhase = PHASE_PATROLLING_HORIZONTAL;
                        phaseTimer = 0;
                        this.patrolCenterX = enemy.getX(); 
                        enemy.setY(entryTargetY); 
                    }
                    break;
            }
        }
    
        private void moveEnter(Enemy enemy) {
            enemy.setY(enemy.getY() + generalSpeed);
        }
    
        private void movePatrolHorizontal(Enemy enemy) {
            patrolAngle += patrolSpeedFactor * generalSpeed;
            float newX = patrolCenterX + (float) (Math.sin(patrolAngle) * patrolAmplitudeX);
            enemy.setX(newX);
            enemy.setY(entryTargetY);
        }
    
        private void moveCircleStrafe(Enemy enemy) {
            circleAngle += circleSpeedFactor * generalSpeed;
            float newX = circleCenterX + (float) (Math.cos(circleAngle) * circleRadius);
            float newY = circleCenterY + (float) (Math.sin(circleAngle) * circleRadius);
            enemy.setX(newX);
            enemy.setY(newY);
        }
    
}
