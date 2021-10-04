package example.domain.game.ecs.systems.state;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import example.domain.game.ecs.components.Client;
import example.domain.game.ecs.components.events.Initial;
import example.domain.game.ecs.components.events.StateEvent;
import example.server.messages.State;


/**
 * Система рассылки стейта подключенным клиентам
 */

@One({StateEvent.class})
public class SendStateSystem extends IteratingSystem {

    private ComponentMapper<Client> clientCM;
    private ComponentMapper<StateEvent> stateEventCM;

    private final IntBag clients = new IntBag(10);
    @Override
    protected void initialize() {
        // подписываемся на все сущности с компонентом Client которые были проинициализированы
        world.getAspectSubscriptionManager().get(Aspect.all(Client.class).exclude(Initial.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                SendStateSystem.this.clients.addAll(entities);
            }
            @Override
            public void removed(IntBag entities) {
                final int[] data = entities.getData();
                final int size = entities.size();
                for (int i = 0; i < size; i++) {
                    SendStateSystem.this.clients.removeValue(data[i] );
                }
            }
        });
    }


    @Override
    protected void process(int entityId) {
        StateEvent stateEvent = stateEventCM.get(entityId);
        State state = stateEvent.state;

        final int[] data = clients.getData();
        final int size = clients.size();

        state.clientsCount = size;
        int clientId;
        for (int i = 0; i < size; i++) {
            clientId = data[i];
            Client client = clientCM.get(clientId);
            client.connection.write(stateEvent.state);
        }

        stateEventCM.remove(entityId);
    }
}
