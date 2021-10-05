package example.domain.game;

import com.artemis.Aspect;
import com.artemis.World;
import com.artemis.utils.IntBag;
import example.domain.game.ecs.components.Client;
import example.domain.game.ecs.components.Position;
import example.domain.game.ecs.components.events.ClientPressKeysEvent;
import example.domain.game.ecs.components.events.DeleteEvent;
import example.domain.game.ecs.components.events.Initial;
import example.domain.game.ecs.components.events.StateChangedEvent;
import example.domain.game.ecs.util.EntityBuilder;
import example.log.Log;
import example.server.messages.ClientMessage;

public class Game{
    private boolean isSetted = false;
    private World world;
    private EntityBuilder entityBuilder;
    private Thread thread;

    public Game(){}

    public void setWorld(final World world){
        if (thread!=null && !thread.isInterrupted()){
            thread.interrupt();
            while (!thread.isInterrupted()) { Log.i(this, "Wait stop old world");}
        }

        this.world = world;
        this.entityBuilder = new EntityBuilder(world);

        thread = new Thread(()->{
            try {
                while (true){
                    Thread.sleep(Position.BLOCK_TIME);
                    world.setDelta(0.1f);
                    world.process();
                }
            }catch (InterruptedException exception){
                exception.printStackTrace();
                Log.i(this,"Ecs World stopped?");
            }finally {
                world.dispose();
            }
        });
        thread.start();

    }

    public int addConnection(Connection connection){
        Log.i(this, "addConnection " + connection.name);

        final int newClientId = world.create();
        Log.i("Новый клиент id="+newClientId);
        world.getMapper(Client.class).create(newClientId).connection = connection;
        world.getMapper(Initial.class).create(newClientId);

        return newClientId;
    }

    public void removeClient(int client){
        Log.i(this, "removePlayer");
        if (world.getEntityManager().isActive(client)){
            world.getMapper(Client.class).remove(client);
            world.getMapper(DeleteEvent.class).create(client);
            world.getMapper(StateChangedEvent.class).create(client);
        }else{
            Log.i(this,"Клиент вне Ecs World");
        }
    }

    public void messageFrom(int clientId, ClientMessage clientMessage){
        //если клиент не инициализирован игнорим
        if (world.getMapper(Initial.class).has(clientId)) {
            clientMessage.recycle();
            return;
        }

        int event = world.create();
        ClientPressKeysEvent clientPressKeysEvent = world.getMapper(ClientPressKeysEvent.class).create(event);
        clientPressKeysEvent.entityId = clientId;
        clientPressKeysEvent.size = clientMessage.size;
        try {
            System.arraycopy(clientMessage.keys,0,clientPressKeysEvent.keys,0,clientMessage.size);
        }catch (Throwable t){

            t.printStackTrace();
        }finally {
            clientMessage.recycle();
        }

    }

    public void dispose() {
        if (thread != null) thread.interrupt();
    }

    public void removeAllClientsFromEcsWorld(){
        IntBag entities = world
                .getAspectSubscriptionManager()
                .get(Aspect.one(Client.class).exclude(Initial.class))
                .getEntities();
        int size = entities.size();
        int[] data = entities.getData();
        for (int i = 0; i < size; i++) {
            world.delete(data[i]);
        }
    }
}
