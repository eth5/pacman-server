package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;
import example.interfaces.Action;

/**
 * Компонент событие. Нужен для отложенного запуска Action. Отложенный запуск измеряется в тиках world мира
 */

public class DelayRun extends PooledComponent {
    public int skipFrames;
    public Action action;
    @Override
    protected void reset() {
        skipFrames = 0;
        action = null;
    }
}
