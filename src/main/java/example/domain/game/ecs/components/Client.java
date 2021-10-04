package example.domain.game.ecs.components;

import com.artemis.PooledComponent;
import example.domain.game.Connection;

/**
 * Компонент содержит ссылку на соединение клиента
 */

public class Client extends PooledComponent {
    public Connection connection;

    @Override
    protected void reset() {
        connection = null;
    }
}
