package example.server;

import example.interfaces.Action;
import example.serialization.GSonSerializer;
import example.serialization.ISerializer;
import example.server.decode.MessageDecoder;
import example.server.endode.CommandEncoder;
import example.server.endode.InitialGameDataMessageEncoder;
import example.server.endode.StateEncoder;
import example.server.messages.ClientMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    final Action.Arg1<ChannelHandlerContext> onConnect;
    final Action.Arg1<ChannelHandlerContext> onDisconnect;
    final Action.Arg2<ChannelHandlerContext, ClientMessage> onMessageReceived;
    private final ISerializer serializer = new GSonSerializer();

    public ServerInitializer(Action.Arg1<ChannelHandlerContext> onConnect, Action.Arg1<ChannelHandlerContext> onDisconnect, Action.Arg2<ChannelHandlerContext, ClientMessage> onMessageReceived) {
        this.onConnect = onConnect;
        this.onDisconnect = onDisconnect;
        this.onMessageReceived = onMessageReceived;
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(

                        new MessageDecoder(),

                        new InitialGameDataMessageEncoder(serializer),
                        new StateEncoder(serializer),
                        new CommandEncoder(serializer),

                        new MessageHandler(onMessageReceived, onConnect, onDisconnect)
                );
    }
}
