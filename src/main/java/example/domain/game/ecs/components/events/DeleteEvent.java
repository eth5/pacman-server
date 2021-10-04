package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;

/**
 * Компонент-триггер. Помечает сущность которая будет удалена из системы в конце тика world мира
 */

public class DeleteEvent extends PooledComponent {
    @Override
    protected void reset() {

    }
}
