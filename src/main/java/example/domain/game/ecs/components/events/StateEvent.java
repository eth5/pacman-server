package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;
import example.server.messages.State;

/**
 * Компонент-событие. Запускает  расслылку обновленных стетйтов клиентам
 */

public class StateEvent extends PooledComponent {
    public State state;

    @Override
    protected void reset() {
        state = null;
    }
}
