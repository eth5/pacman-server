package example.domain.game.ecs.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import example.domain.game.ecs.components.events.DeleteEvent;

/**
 * Система удалит любую сущность с компонентом DeleteEvent
 */

@One(DeleteEvent.class)
public class DeleteSystem extends IteratingSystem {
    private ComponentMapper<DeleteEvent> deleteCM;
    @Override
    protected void process(int entityId) {
        world.delete(entityId);
    }
}
