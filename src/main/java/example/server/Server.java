package example.server;

import example.interfaces.Action;
import example.log.Log;
import example.server.messages.ClientMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    private final int port;

    public Server(int port){
        this.port = port;
    }

    public void start(
            final Action.Arg2<ChannelHandlerContext, ClientMessage> onMessageReceived,
            final Action.Arg1<ChannelHandlerContext> onConnect,
            final Action.Arg1<ChannelHandlerContext> onDisconnect) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer(onConnect, onDisconnect, onMessageReceived))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            Log.i(this, "Server at " + f.channel().localAddress());
            f.channel().closeFuture().sync();
            Log.i(this,"Stop!");

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
