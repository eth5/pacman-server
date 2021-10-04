package example.domain.game.ecs.components.base;

import com.artemis.PooledComponent;

/**
 * Для оптимизации... не реализовано
 */

public class BaseComponent extends PooledComponent {
    public int type;
    @Override
    protected void reset() {
        type = -1;
    }
}
