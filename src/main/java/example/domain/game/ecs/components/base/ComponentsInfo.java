package example.domain.game.ecs.components.base;

import com.artemis.Component;
import example.domain.game.ecs.components.Client;
import example.domain.game.ecs.components.PlayerSpawn;
import example.domain.game.ecs.components.events.ClientPressKeysEvent;
import example.domain.game.ecs.components.events.StateChangedEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Содержит типы элементов которые не нужно отправлять игрокам при передаче стейта
 */

public class ComponentsInfo {
    public static final Set<Class<? extends Component>> notSendComponents = new HashSet<>();
    static {
        notSendComponents.add(Client.class);
        notSendComponents.add(StateChangedEvent.class);
        notSendComponents.add(ClientPressKeysEvent.class);
        notSendComponents.add(PlayerSpawn.class);
    }
}
