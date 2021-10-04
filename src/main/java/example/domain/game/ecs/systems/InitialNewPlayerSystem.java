package example.domain.game.ecs.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import example.domain.game.ecs.components.Client;
import example.domain.game.ecs.components.DirectionVector;
import example.domain.game.ecs.components.Player;
import example.domain.game.ecs.components.Position;
import example.domain.game.ecs.components.events.Initial;
import example.domain.game.ecs.components.events.SpawnEvent;
import example.domain.game.ecs.components.events.StateChangedEvent;
import example.domain.game.ecs.util.EntityBuilder;

/**
 * Система инициализации нового игрока-клиента
 */

@All({Initial.class, Client.class})
public class InitialNewPlayerSystem extends IteratingSystem {
    private ComponentMapper<Position> positionCM;
    private ComponentMapper<Initial> initialCM;

    private EntityBuilder entityBuilder;

    @Override
    protected void initialize() {
        entityBuilder = new EntityBuilder(world);
    }

    @Override
    protected void process(int entityId) {
        addNewPlayerToGame(entityId);
        initialCM.remove(entityId);
    }

    // Создаем базовые компоненты и события
    private void addNewPlayerToGame(int playerId){
        entityBuilder
                .begin(playerId, Player.class)
                .addComponent(StateChangedEvent.class)
                .addComponent(DirectionVector.class)
                .addComponent(Position.class)
                .addComponent(SpawnEvent.class)
                .end();
    }

}
