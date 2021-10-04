package example.domain.game.ecs.systems.collision;

import com.artemis.ComponentMapper;
import com.artemis.annotations.One;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import example.domain.game.ecs.components.Position;
import example.domain.game.ecs.components.events.CollisionEvent;
import example.domain.game.ecs.components.events.StateChangedEvent;

/**
 * Система определения коллизий
 * Запускается по событию CollisionEvent помещает список сущностей с такими же координатами как у сущности
 * на которой находится компонент CollisionEvent в CollisionEvent.collisionBag.
 */


@One(CollisionEvent.class)
public class CollisionDetectSystem extends IteratingSystem {
    private ComponentMapper<CollisionEvent> collisionEventCM;
    private ComponentMapper<Position> positionCM;
    private ComponentMapper<StateChangedEvent> stateChangedCM;

    @Wire(name = "entitiesIdsByPosition")
    public IntBag[][] entitiesIdsByPosition;

    @Override
    protected void process(int entityId) {
        Position position = positionCM.get(entityId);

        if (position.line < 0 || position.lineIndex < 0) removeCollisionEvent(entityId);
        else{
            if (position.line < entitiesIdsByPosition.length){
                IntBag[] intBags = entitiesIdsByPosition[position.line];
                if (position.lineIndex < intBags.length){
                    collisionEventCM.get(entityId).collisionBag = intBags[position.lineIndex];
                }
                else removeCollisionEvent(entityId);
            }
            else removeCollisionEvent(entityId);
        }
    }
    private void removeCollisionEvent(int entityId){
        collisionEventCM.remove(entityId);
    }
}
