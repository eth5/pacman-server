package example.domain.game;

import example.log.Log;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class Connection {
    public final String name;
    private final ChannelHandlerContext ctx;
    public Connection(ChannelHandlerContext ctx){
        this.ctx = ctx;
        name = ctx.channel().remoteAddress().toString();
    }

    public void write(Object object){
        // Log.d(this, "write obj "+object+" for " + ctx.channel().remoteAddress());
        if (!ctx.channel().isWritable()) {
            Log.e(this, "ctx not writable!");
            close();
            return;
        }

        try {

            ChannelFuture channelFuture = ctx.writeAndFlush(object);
            ChannelFuture sync = channelFuture.sync();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            close();
        }
    }

    public void close(){
        ctx.close();
    }

}
