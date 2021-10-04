package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;

/**
 * Компонент-событие, содержит в себе клавиши которые отправил клиент
 * keys - массив клавиш
 * size - количество полученных клавиш от клиента
 */

public class ClientPressKeysEvent extends PooledComponent {
    public int entityId;
    public final int[] keys = new int[5];
    public int size = 0;
    @Override
    protected void reset() {
        size = 0;
        entityId = -1;
    }
}
