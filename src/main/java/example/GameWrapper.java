package example;

import com.artemis.World;
import example.domain.game.Connection;
import example.domain.game.EcsWorldCreator;
import example.domain.game.Game;
import example.log.Log;
import example.server.messages.ClientMessage;
import example.util.resource.Resource;
import example.util.resource.Success;
import io.netty.channel.Channel;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameWrapper {
    private boolean isReady = false;
    private final Set<Channel> queueForDelete = new HashSet<>();
    private final Set<Channel> queueForAdd = new HashSet<>();

    private final Map<Connection, Integer> clientToId = new HashMap<>();
    private final Map<Channel, Connection> channelToConnection = new HashMap<>();


    private final Game game = new Game();
    private final File dir;

    public GameWrapper(File dir) {
        this.dir = dir;
    }

    public void createNewGame(File file) {
        isReady = false;
        Resource<World> gameResource = new EcsWorldCreator().creteFrom(file, this::onEndGame);
        if (gameResource instanceof Success) {

            game.setWorld(gameResource.data);

            //для установки всех мировых начальных настроек
            gameResource.data.setDelta(0.1f);
            gameResource.data.process();
            gameResource.data.process();

            isReady = true;

        } else {
            Log.e(this, gameResource.msg);
        }
    }

    public void runRandomMap(){
        File[] files = dir.listFiles();
        if (files == null) throw new NullPointerException("Нет карт в " + dir.getAbsolutePath());
        int index = (int) (Math.random() * files.length);
        createNewGame(files[index]);
    }

    private void onEndGame() {
        isReady = false;
        Log.e(this, "end game!");
        game.removeAllClientsFromEcsWorld();

        runRandomMap();
        clientToId.clear();

        synchronized (channelToConnection) {
            Set<Channel> channels = channelToConnection.keySet();
            for (Channel channel : channels) {
                Connection connection = channelToConnection.get(channel);
                int clientId = game.addConnection(connection);
                clientToId.put(connection, clientId);
            }
            isReady = true;
        }

    }

    public void messageFrom(Channel channel, ClientMessage message) {
        if (!isReady) {
            message.recycle();
            return;
        }
        if (channelToConnection.containsKey(channel)) {
            Connection connection = channelToConnection.get(channel);
            game.messageFrom(clientToId.get(connection), message);
        } else {
            Log.e(this, "Wtf? Сообщение от незарегестированного клиента ?");
            message.recycle();
            channel.close();
        }
    }

    public synchronized void addChannel(Channel channel) {
        if (isReady) {
            Connection connection = new Connection(channel);
            channelToConnection.put(channel, connection);
            int clientId = game.addConnection(connection);
            clientToId.put(connection, clientId);
        }
    }

    public synchronized void removeChannel(Channel channel) {
        if (!channelToConnection.containsKey(channel)) {
            Log.e(this, "Wtf? Отключается незарегистрированное канал!");
        } else {
            synchronized (channelToConnection) {
                Connection connection = channelToConnection.remove(channel);
                game.removeClient(clientToId.remove(connection));
            }
        }
    }
}
