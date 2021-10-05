package example.domain.game.ecs.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import example.domain.game.ecs.components.Client;
import example.domain.game.ecs.components.DirectionVector;
import example.domain.game.ecs.components.Player;
import example.domain.game.ecs.components.Position;
import example.domain.game.ecs.components.events.Initial;
import example.domain.game.ecs.components.events.SpawnEvent;
import example.domain.game.ecs.components.events.StateChangedEvent;
import example.domain.game.ecs.util.EntityBuilder;
import example.domain.game.ecs.util.FullState;
import example.log.Log;
import example.server.messages.InitialGameDataMessage;
import example.server.messages.State;

/**
 * Система инициализации нового игрока-клиента
 */

@All({Initial.class, Client.class})
public class InitialNewPlayerSystem extends IteratingSystem {
    private FullState fullState;
    private ComponentMapper<Position> positionCM;
    private ComponentMapper<Initial> initialCM;
    private ComponentMapper<Client> clientCM;
    private EntityBuilder entityBuilder;

    @Wire(name = "columns")
    private int columns;
    @Wire(name = "rows")
    private int rows;

    @Override
    protected void initialize(){
        fullState = new FullState(world);
        entityBuilder = new EntityBuilder(world);
    }

    @Override
    protected void process(int entityId) {
        Client client = clientCM.get(entityId);
        client.connection.write(getInitialMessage(entityId));

        //отправляем текущее состояние игры новому клиенту
        State fullState = this.fullState.getFullState();
        fullState.clientsCount = 1;
        Log.i(this,"Send full state");
        client.connection.write(fullState);

        addNewPlayerToGame(entityId);
        initialCM.remove(entityId);
    }

    private InitialGameDataMessage getInitialMessage(int entityId){
        return new InitialGameDataMessage(rows,columns,entityId);
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
