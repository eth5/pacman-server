package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;

/**
 * Компонент-комманда. Содержит в себе направление по которому должна быть перемещена сущность
 *
 */

public class MoveCommandEvent extends PooledComponent {
    public boolean up,down,left,right;

    @Override
    protected void reset() {
        up = false;
        down = false;
        left = false;
        right = false;
    }
}
