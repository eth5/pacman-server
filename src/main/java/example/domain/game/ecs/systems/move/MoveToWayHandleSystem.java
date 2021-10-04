package example.domain.game.ecs.systems.move;

import com.artemis.ComponentMapper;
import com.artemis.annotations.One;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import example.domain.game.ecs.components.*;
import example.domain.game.ecs.components.events.CollisionEvent;
import example.domain.game.ecs.components.events.MoveCommandEvent;
import example.domain.game.ecs.components.events.NewPositionTarget;
import example.domain.game.ecs.components.events.StateChangedEvent;


/**
 * Система обрабатывает команду для движения сущности.
 * Если текущее время системы меньше чем время с которого сущности разрешено двигаться, то команда MoveCommandEvent
 * игнорируется и отменяется
 *
 */

@One(MoveCommandEvent.class)
public class MoveToWayHandleSystem extends IteratingSystem {
    private static final int UP = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    private static final int RIGHT = 4;

    private ComponentMapper<CollisionEvent> collisionEventCM;
    private ComponentMapper<MoveCommandEvent> moveCommandEventCM;
    private ComponentMapper<Position> positionCM;
    private ComponentMapper<Wall> wallCM;
    private ComponentMapper<DirectionVector> speedCM;
    private ComponentMapper<NewPositionTarget> newPositionTargetCM;
    private ComponentMapper<StateChangedEvent> stateChangeEventCM;

    @Wire(name = "entitiesIdsByPosition")
    public IntBag[][] entitiesIdsByPosition;

    public final Position firstPosition = new Position();
    public final Position secondPosition = new Position();

    private Position position;
    private DirectionVector directionVector;
    private int entityId;

    @Override
    protected void process(int entityId) {
        position = positionCM.get(entityId);

        if (System.currentTimeMillis() < position.blockTime){
            moveCommandEventCM.remove(entityId);
            return;
        }

        MoveCommandEvent moveCommand = moveCommandEventCM.get(entityId);

        this.entityId = entityId;

        directionVector = speedCM.get(entityId);

        if (moveCommand.left && moveCommand.right) {

            if (directionVector.x >= 0 && isFreeWay(RIGHT)) moveTo(RIGHT);
            else if (isFreeWay(LEFT)) moveTo(LEFT);
            else if (isFreeWay(RIGHT)) moveTo(RIGHT);

        } else if (moveCommand.up && moveCommand.down) {

            if (directionVector.y >= 0 && isFreeWay(UP)) moveTo(UP);
            else if (isFreeWay(DOWN)) moveTo(DOWN);
            else if (isFreeWay(UP)) moveTo(UP);

        } else if (directionVector.x != 0 && (moveCommand.up || moveCommand.down)) {

            if (moveCommand.up && isFreeWay(UP)) moveTo(UP);
            else if (moveCommand.down && isFreeWay(DOWN)) moveTo(DOWN);
            else if (moveCommand.left && isFreeWay(LEFT)) moveTo(LEFT);
            else if (moveCommand.right && isFreeWay(RIGHT)) moveTo(RIGHT);

        } else if (directionVector.y != 0 && (moveCommand.right || moveCommand.left)) {

            if (moveCommand.left && isFreeWay(LEFT)) moveTo(LEFT);
            else if (moveCommand.right && isFreeWay(RIGHT)) moveTo(RIGHT);
            else if (moveCommand.up && isFreeWay(UP)) moveTo(UP);
            else if (moveCommand.down && isFreeWay(DOWN)) moveTo(DOWN);

        } else if (moveCommand.right && isFreeWay(RIGHT)) moveTo(RIGHT);
            else if (moveCommand.left && isFreeWay(LEFT)) moveTo(LEFT);
            else if (moveCommand.up && isFreeWay(UP)) moveTo(UP);
            else if (moveCommand.down && isFreeWay(DOWN)) moveTo(DOWN);

        moveCommandEventCM.remove(entityId);
    }

    // Создаем компонент-событие для перемещения сущности
    // так же задаем вертор направления
    private void moveTo(int route) {
        NewPositionTarget newPositionTarget = newPositionTargetCM.create(entityId);
        switch (route) {
            case UP -> {
                newPositionTarget.set(position.line - 1, position.lineIndex);
                directionVector.set(0, 1);
            }
            case DOWN -> {
                newPositionTarget.set(position.line + 1, position.lineIndex);
                directionVector.set(0, -1);
            }
            case LEFT -> {
                newPositionTarget.set(position.line, position.lineIndex - 1);
                directionVector.set(-1, 0);
            }
            case RIGHT -> {
                newPositionTarget.set(position.line, position.lineIndex + 1);
                directionVector.set(1, 0);
            }
        }
    }


    private final Position tmpPosition = new Position();
    // проверяем, что по указанному маршруту нет стены или края карты
    private boolean isFreeWay(int route) {
        switch (route) {
            case UP -> tmpPosition.set(position.line - 1, position.lineIndex);
            case DOWN -> tmpPosition.set(position.line + 1, position.lineIndex);
            case LEFT -> tmpPosition.set(position.line, position.lineIndex - 1);
            case RIGHT -> tmpPosition.set(position.line, position.lineIndex + 1);
        }

        int line = tmpPosition.line;
        int lineIndex = tmpPosition.lineIndex;

        boolean isWay = true;
        if (line < 0 || lineIndex < 0) isWay = false;
        else {
            if (line < entitiesIdsByPosition.length) {
                IntBag[] intBags = entitiesIdsByPosition[line];
                if (lineIndex < intBags.length) {
                    IntBag entitiesInPosition = intBags[lineIndex];
                    final int[] data = entitiesInPosition.getData();
                    final int size = entitiesInPosition.size();
                    int id;
                    for (int i = 0; i < size; i++) {
                        id = data[i];
                        boolean wall = wallCM.has(id);
                        if (wall) {
                            isWay = false;
                            break;
                        }
                    }

                } else isWay = false;
            } else isWay = false;
        }

        return isWay;
    }
}
