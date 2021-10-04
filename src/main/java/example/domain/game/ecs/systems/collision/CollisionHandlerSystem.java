package example.domain.game.ecs.systems.collision;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.One;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import example.domain.game.ecs.components.Player;
import example.domain.game.ecs.components.Reward;
import example.domain.game.ecs.components.events.CollisionEvent;
import example.domain.game.ecs.components.events.DeleteEvent;
import example.domain.game.ecs.components.events.StateChangedEvent;
import example.interfaces.Action;

/**
 * Обрабатывает коллизии сущностей. И завершает событие CollisionEvent
 */


@One(CollisionEvent.class)
public class CollisionHandlerSystem extends IteratingSystem {
    private ComponentMapper<CollisionEvent> collisionEventCM;
    private ComponentMapper<DeleteEvent> deleteEventCM;
    private ComponentMapper<Reward> rewardCM;
    private ComponentMapper<Player> playerCM;
    private ComponentMapper<StateChangedEvent> stateChangedEventCM;

    // содержит id всех сущностей с Reward в случае когда размер станет равным нулю вызывается onEndGame
    private final IntBag rewards = new IntBag(200);
    @Wire(name = "onEndGame")
    private Action onEndGame;

    @Override
    protected void initialize() {
        //подписываемся на сущности награды
        world.getAspectSubscriptionManager().get(Aspect.all(Reward.class))
                .addSubscriptionListener( new EntitySubscription.SubscriptionListener()
                {
                    @Override
                    public void inserted( IntBag entities )
                    {
                        rewards.addAll(entities);
                    }
                    @Override
                    public void removed( IntBag entities )
                    {
                        final int[] entitiesIds = entities.getData();
                        final int size = entities.size();
                        for (int i = 0; i < size; i++)
                        {
                            int entityId = entitiesIds[i];
                            rewards.removeValue(entityId);
                        }
                        if (rewards.isEmpty()) onEndGame.invoke();
                    }

                } );
    }

    @Override
    protected void process(int entityId) {
        if (deleteEventCM.has(entityId)) return;

        CollisionEvent collisionEvent = collisionEventCM.get(entityId);

        final int[] data = collisionEvent.collisionBag.getData();
        final int size = collisionEvent.collisionBag.size();
        int collisonId;
        for (int i = 0; i < size; i++) {
            collisonId = data[i];
            collisionHandler(entityId, collisonId);
        }

        collisionEventCM.remove(entityId);
    }

    private void collisionHandler(int entityId, int collision){
        if (deleteEventCM.has(collision))return;
        if (playerCM.has(entityId) && rewardCM.has(collision)){

            playerCM.get(entityId).score++;

            deleteEventCM.create(collision);

            stateChangedEventCM.create(collision);
            stateChangedEventCM.create(entityId);
        }
    }
}
