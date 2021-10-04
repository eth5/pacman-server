package example.domain.game;

import example.log.Log;
import io.netty.channel.ChannelHandlerContext;

public class Connection {
    public final String name;
    private final ChannelHandlerContext ctx;
    public Connection(ChannelHandlerContext ctx){
        this.ctx = ctx;
        name = ctx.channel().remoteAddress().toString();
    }

    public void write(Object object){
        if (!ctx.channel().isWritable()) {
            Log.e(this, "ctx not writable!");
            close();
            return;
        }

        try {

            ctx.writeAndFlush(object).sync();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            close();
        }
    }

    public void close(){
        ctx.close();
    }

}
