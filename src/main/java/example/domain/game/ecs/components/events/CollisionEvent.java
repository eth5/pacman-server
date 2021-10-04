package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;
import com.artemis.utils.IntBag;

/**
 * Компонент-событие. Содержит коллекцию id сущностей с которыми контактирует хозяин компонента
 */

public class CollisionEvent extends PooledComponent {
    public IntBag collisionBag;

    @Override
    protected void reset() {
        collisionBag = null;
    }
}
