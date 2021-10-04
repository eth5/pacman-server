package example.server.endode;

import example.serialization.ISerializator;
import example.server.messages.MessageType;
import example.server.messages.ServerCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class CommandEncoder extends MessageToByteEncoder<ServerCommand> {
     private final ISerializator serializator;
    public CommandEncoder(ISerializator serializator){
        this.serializator = serializator;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerCommand msg, ByteBuf out) throws Exception {
        if (msg == null) throw new NullPointerException("сообщение не может быть null!!!");
        String jsonString = serializator.toJson(msg);
        out.writeInt(jsonString.length()).writeInt(MessageType.SERVER_COMMAND);
        out.writeCharSequence(jsonString, Charset.defaultCharset());
    }
}
