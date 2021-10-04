package example;

import example.domain.game.Connection;
import example.log.Log;
import example.serialization.GSonSerializer;
import example.server.Server;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        File file = new File("cfg");

        GameWrapper gameWrapper = new GameWrapper(file);
        gameWrapper.createNewGame(file);
        Map<ChannelHandlerContext, Connection> clients = new HashMap<>();

        try {
            new Server(8080).start(
                    new GSonSerializer(),
                    (ctx, message) -> {
                        Connection connection = clients.get(ctx);
                        gameWrapper.messageFrom(connection, message);
                    },
                    ctx -> {
                        Connection connection = new Connection(ctx);
                        gameWrapper.addConnection(connection);
                        clients.put(ctx, connection);
                    },
                    ctx -> {
                        Connection connection = clients.remove(ctx);
                        if (connection == null) Log.e("Отключился не подключившийся клиент? О_о");
                        else gameWrapper.removeConnection(connection);
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
