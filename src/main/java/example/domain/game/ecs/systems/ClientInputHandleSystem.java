package example.domain.game.ecs.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import example.domain.game.ecs.components.events.ClientPressKeysEvent;
import example.domain.game.ecs.components.events.MoveCommandEvent;
import example.log.Log;

import java.util.Arrays;

/**
 * Система трансформирует полученные нажатые клавиши клиента в команды движения клиентской сущности
 *
 */

@One(ClientPressKeysEvent.class)
public class ClientInputHandleSystem extends IteratingSystem {
    private final static int LEFT = 21, RIGHT = 22, UP = 19, DOWN = 20;
    private ComponentMapper<ClientPressKeysEvent> userPressKeysEventCM;
    private ComponentMapper<MoveCommandEvent> moveCommandEventCM;

    @Override
    protected void process(int entityId) {
        ClientPressKeysEvent clientPressKeysEvent = userPressKeysEventCM.get(entityId);

        // убеждаемся, что сущность, на которую ссылается событие, обрабатывается системой
        if ( world.getEntityManager().isActive(clientPressKeysEvent.entityId) ){
            MoveCommandEvent moveCommandEvent = moveCommandEventCM.create(clientPressKeysEvent.entityId);

            for (int i = 0; i < clientPressKeysEvent.size; i++ ){
                switch (clientPressKeysEvent.keys[i]){
                    case LEFT -> moveCommandEvent.left = true;
                    case RIGHT -> moveCommandEvent.right = true;
                    case UP -> moveCommandEvent.up = true;
                    case DOWN -> moveCommandEvent.down = true;
                }
            }
        }

        world.delete(entityId);
    }
}
