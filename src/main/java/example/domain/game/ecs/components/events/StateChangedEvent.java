package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;

/**
 * Компонент-маркер помечает сущность у которой было изменено внутренне состояние
 *
 */

public class StateChangedEvent extends PooledComponent {

    @Override
    protected void reset() {

    }
}
