package example.server;

import example.server.messages.ClientMessage;
import example.interfaces.Action;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<ClientMessage> {
    private final Action.Arg2<ChannelHandlerContext,ClientMessage> onMessageReceived;
    private final Action.Arg1<ChannelHandlerContext> onConnect;
    private final Action.Arg1<ChannelHandlerContext> onDisconnect;
    public MessageHandler(Action.Arg2<ChannelHandlerContext,ClientMessage> onMessageReceived, Action.Arg1<ChannelHandlerContext> onConnect, Action.Arg1<ChannelHandlerContext> onDisconnect){
        this.onMessageReceived = onMessageReceived;
        this.onConnect = onConnect;
        this.onDisconnect = onDisconnect;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        onConnect.invoke(ctx);
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        onDisconnect.invoke(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientMessage msg) throws Exception {
        onMessageReceived.invoke(ctx,msg );

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
