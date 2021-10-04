package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;

/**
 * Компонент-событие. Активирует систему перемещаения сущности на точку спауна
 */

public class SpawnEvent extends PooledComponent {

    @Override
    protected void reset() {

    }
}
