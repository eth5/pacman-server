package example.server;

import example.interfaces.Action;
import example.serialization.ISerializer;
import example.server.decode.MessageDecoder;
import example.server.endode.CommandEncoder;
import example.server.endode.InitialGameDataMessageEncoder;
import example.server.endode.StateEncoder;
import example.server.messages.ClientMessage;
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

    public void start(
            final ISerializer serializer,
            final Action.Arg2<ChannelHandlerContext, ClientMessage> onMessageReceived,
            final Action.Arg1<ChannelHandlerContext> onConnect,
            final Action.Arg1<ChannelHandlerContext> onDisconnect) throws Exception {


        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(

                                            new MessageDecoder(),

                                            new InitialGameDataMessageEncoder(serializer),
                                            new StateEncoder(serializer),
                                            new CommandEncoder(serializer),

                                            new MessageHandler(onMessageReceived, onConnect, onDisconnect)
                                    );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            System.out.println("Start server at " + f.channel().localAddress());
            f.channel().closeFuture().sync();
            System.out.println("Server stop!");

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
