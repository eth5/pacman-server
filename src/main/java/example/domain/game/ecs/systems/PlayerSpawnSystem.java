package example.domain.game.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import example.domain.game.ecs.components.PlayerSpawn;
import example.domain.game.ecs.components.Position;
import example.domain.game.ecs.components.events.NewPositionTarget;
import example.domain.game.ecs.components.events.SpawnEvent;


/**
 * Спауним сущность в точке респауна по событию SpawnEvent
 */

@One(SpawnEvent.class)
public class PlayerSpawnSystem extends IteratingSystem {
    private ComponentMapper<SpawnEvent> spawnEventCM;
    private ComponentMapper<Position> positionCM;
    private ComponentMapper<NewPositionTarget> newPositionTargetCM;

    private final IntBag spawnPoints = new IntBag(10);

    @Override
    protected void initialize() {
        // подписываемся на сущности с точками спауна в мире
        world.getAspectSubscriptionManager().get(Aspect.all(PlayerSpawn.class))
                .addSubscriptionListener( new EntitySubscription.SubscriptionListener()
                {
                    @Override
                    public void inserted( IntBag entities ) {
                        spawnPoints.addAll(entities);
                    }
                    @Override
                    public void removed( IntBag entities ) {
                        final int[] entitiesIds = entities.getData();
                        final int size = entities.size();
                        for (int i = 0; i < size; i++) {
                            int entityId = entitiesIds[i];
                            spawnPoints.removeValue(entityId);
                        }
                    }
                } );
    }

    @Override
    protected void process(int entityId) {
        Position spawnPosition = getSpawnPosition();

        NewPositionTarget newPositionTarget = newPositionTargetCM.create(entityId);
        newPositionTarget.line = spawnPosition.line;
        newPositionTarget.lineIndex = spawnPosition.lineIndex;

        spawnEventCM.remove(entityId);
    }
    private Position getSpawnPosition(){
        if (spawnPoints.isEmpty()) {
            Position position = new Position();
            position.set(0,0);
            return position;
        }
        int index = (int) (Math.random() * spawnPoints.size());
        int spawnEntityId = spawnPoints.get(index);
        return positionCM.get(spawnEntityId);
    }
}
