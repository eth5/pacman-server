package example.domain.game.ecs.systems.state;

import com.artemis.*;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.Bag;
import example.domain.game.ecs.components.events.DeleteEvent;
import example.domain.game.ecs.components.events.StateChangedEvent;
import example.domain.game.ecs.components.Client;
import example.domain.game.ecs.components.events.StateEvent;
import example.server.messages.StateEntityData;
import example.server.messages.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Система собирает все сущности с триггером компонентом StateChangedEvent. И запускает событие рассылки
 * стейта этих сущностей
 */

@One({StateChangedEvent.class})
public class UpdateStateSystem extends IteratingSystem {
    private ComponentMapper<Client> clientCM;
    private ComponentMapper<StateChangedEvent> stateChangedEventCM;
    private ComponentMapper<StateEvent> stateEventCM;
    private ComponentMapper<DeleteEvent> deleteEventCM;

    private final Bag<Component> bagComponents = new Bag<>();
    private final List<StateEntityData> entitiesData = new ArrayList<>(50);

    @Override
    protected void end() {
        if (entitiesData.size() > 0){
            State state = State.getInstance();
            state.addAll(entitiesData);
            entitiesData.clear();
            stateEventCM.create(world.create()).state = state;
        }
    }

    @Override
    protected void process(int entityId) {
        // Log.i("update event!");
        bagComponents.clear();

        world.getComponentManager().getComponentsFor(entityId, bagComponents);
        StateEntityData stateEntityData = StateEntityData.getInstance(entityId,deleteEventCM.has(entityId), bagComponents);
        entitiesData.add(stateEntityData);

        stateChangedEventCM.remove(entityId);
    }
}
