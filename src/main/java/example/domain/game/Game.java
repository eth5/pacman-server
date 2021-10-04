package example.domain.game;

import com.artemis.World;
import example.domain.game.ecs.components.Client;
import example.domain.game.ecs.components.Position;
import example.domain.game.ecs.components.events.ClientPressKeysEvent;
import example.domain.game.ecs.components.events.DeleteEvent;
import example.domain.game.ecs.components.events.Initial;
import example.domain.game.ecs.components.events.StateChangedEvent;
import example.domain.game.ecs.util.EntityBuilder;
import example.domain.game.ecs.util.FullState;
import example.log.Log;
import example.server.messages.ClientMessage;
import example.server.messages.InitialGameDataMessage;
import example.server.messages.State;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Game{
    final World world;
    final EntityBuilder entityBuilder;
    final FullState fullState;
    final int worldFieldWidth, worldFieldHeight;
    private Thread thread;


    public Game(World world, int worldFieldWidth, int worldFieldHeight){
        this.worldFieldWidth = worldFieldWidth;
        this.worldFieldHeight = worldFieldHeight;
        this.world = world;
        this.entityBuilder = new EntityBuilder(world);
        this.fullState = new FullState(world);
    }

    private Map<Connection, Integer> connectionToEntityMap = new HashMap<>();

    public void addConnection(Connection connection){
        Log.i(this, "addConnection " + connection.name);

        final int newPlayerId = world.create();
        connectionToEntityMap.put(connection, newPlayerId);

        Log.i("Новый клиент id="+newPlayerId);
        connection.write(new InitialGameDataMessage(worldFieldWidth, worldFieldHeight,newPlayerId));

        State fullState = this.fullState.getFullState();
        fullState.clientsCount = 1;
        connection.write(fullState);

        world.getMapper(Initial.class).create(newPlayerId);
        world.getMapper(Client.class).create(newPlayerId).connection = connection;
    }

    public void removeConnection(Connection connection){
        Log.i(this, "removeConnection");
        Integer playerId = connectionToEntityMap.remove(connection);
        if (playerId == null) Log.e("playerId = null!!! Illegal State!");
        else {
            world.getMapper(DeleteEvent.class).create(playerId);
            world.getMapper(StateChangedEvent.class).create(playerId);
        }
    }

    public Collection<Connection> getConnections(){
        return connectionToEntityMap.keySet();
    }

    public void messageFrom(Connection connection, ClientMessage clientMessage){

        int clientEntityId = connectionToEntityMap.get(connection);
        if (world.getMapper(Initial.class).has(clientEntityId)) return;

        int event = world.create();
        ClientPressKeysEvent clientPressKeysEvent = world.getMapper(ClientPressKeysEvent.class).create(event);
        clientPressKeysEvent.entityId = clientEntityId;
        clientPressKeysEvent.size = clientMessage.size;

        try {
            System.arraycopy(clientMessage.keys,0,clientPressKeysEvent.keys,0,clientMessage.size);
        }catch (Throwable t){
            t.printStackTrace();
            connection.close();
        }finally {
            clientMessage.recycle();
        }

    }
    public void start(){
        thread = new Thread(()->{
            try {
                while (true){
                    Thread.sleep(Position.BLOCK_TIME);
                    world.setDelta(0.1f);
                    world.process();
                }
            }catch (InterruptedException exception){
                exception.printStackTrace();
            }finally {
                world.dispose();
            }
        });
        thread.start();
    }


    public void dispose() {
        if (thread != null) thread.interrupt();
    }
}
