package example.domain.game;

import io.netty.channel.Channel;

public class Connection {
    public final String name;
    private final Channel channel;
    public Connection(Channel channel){
        this.channel = channel;
        name = channel.remoteAddress().toString();
    }

    public void write(Object object){
        channel.writeAndFlush(object);
    }

    public void close(){
        channel.close();
    }

}
