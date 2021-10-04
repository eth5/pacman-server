package example.domain.game.ecs.util;

import com.artemis.*;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import example.domain.game.ecs.components.*;
import example.domain.game.ecs.components.events.Initial;
import example.server.messages.StateEntityData;
import example.server.messages.State;

public class FullState {
    private final World world;
    private final IntBag entities = new IntBag(100);
    private final Bag<Component> bagComponents = new Bag<>(50);

    public FullState(World world){
        this.world = world;
        world.getAspectSubscriptionManager()
                .get(Aspect.one(Wall.class, Road.class, Reward.class, Player.class).exclude(Initial.class))
                .addSubscriptionListener(
                        new EntitySubscription.SubscriptionListener() {
                            @Override
                            public void inserted(IntBag entities) {
                                FullState.this.entities.addAll(entities);
                            }
                            @Override
                            public void removed(IntBag entities) {
                                final int[] data = entities.getData();
                                final int size = entities.size();
                                for (int i = 0; i < size; i++) {
                                    FullState.this.entities.removeValue(data[i]);
                                }
                            }
                        });
    }

    public State getFullState(){
        State state = State.getInstance();

        final int[] data = entities.getData();
        final int size = entities.size();
        int entityId;
        for (int i = 0; i < size; i++) {
            bagComponents.clear();
            entityId = data[i];
            world.getComponentManager().getComponentsFor(entityId, bagComponents);
            StateEntityData stateEntityData = StateEntityData.getInstance(entityId, false, bagComponents);
            state.add(stateEntityData);
        }
        bagComponents.clear();

        return state;
    }

}
