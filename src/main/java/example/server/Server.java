package example.server;

import example.server.messages.ClientMessage;
import example.interfaces.Action;
import example.serialization.GSonSerialization;
import example.serialization.ISerializator;
import example.server.decode.MessageDecoder;
import example.server.endode.CommandEncoder;
import example.server.endode.InitialGameDataMessageEncoder;
import example.server.endode.StateEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    private final int port;
    public Server(int port){
        this.port = port;
    }

    public void start(Action.Arg2<ChannelHandlerContext,ClientMessage> onMessageReceived, Action.Arg1<ChannelHandlerContext> onConnect, Action.Arg1<ChannelHandlerContext> onDisconnect) throws Exception {
        ISerializator serializator = new GSonSerialization();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(

                                            new MessageDecoder(),

                                            new InitialGameDataMessageEncoder(serializator),
                                            new StateEncoder(serializator),
                                            new CommandEncoder(serializator),

                                            new MessageHandler(onMessageReceived, onConnect, onDisconnect)
                                    );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            ChannelFuture f = b.bind(port).sync(); // (7)
            System.out.println("Start server at " + f.channel().localAddress());
            f.channel().closeFuture().sync();
            System.out.println("Server stop!");

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
