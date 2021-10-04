package example.domain.game.ecs.systems.move;

import com.artemis.ComponentMapper;
import com.artemis.annotations.One;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import example.domain.game.ecs.components.Position;
import example.domain.game.ecs.components.DirectionVector;
import example.domain.game.ecs.components.events.CollisionEvent;
import example.domain.game.ecs.components.events.NewPositionTarget;
import example.domain.game.ecs.components.events.StateChangedEvent;

/**
 * Система меняет позицию сущности на указанные координаты и актуализирует IntBag[][] fieldArray
 * для удобного поиска сущностей и определения коллизий по координатам
 */

@One({NewPositionTarget.class})
public class NewPositionSystem extends IteratingSystem {

    private ComponentMapper<Position> positionCM;
    private ComponentMapper<NewPositionTarget> newPositionCM;
    private ComponentMapper<StateChangedEvent> stateChangedCM;
    private ComponentMapper<DirectionVector> speedCM;
    private ComponentMapper<CollisionEvent> collisionEventCM;

    @Wire(name = "entitiesIdsByPosition")
    public IntBag[][] entitiesIdsByPosition;

    @Override
    protected void process(int entityId) {

        NewPositionTarget newPositionTarget = newPositionCM.get(entityId);
        Position position = positionCM.get(entityId);

        entitiesIdsByPosition[position.line][position.lineIndex].removeValue(entityId);

        position.set(newPositionTarget.line, newPositionTarget.lineIndex);

        entitiesIdsByPosition[position.line][position.lineIndex].add(entityId);

        newPositionCM.remove(entityId);

        collisionEventCM.create(entityId);
        stateChangedCM.create(entityId);
    }
}
