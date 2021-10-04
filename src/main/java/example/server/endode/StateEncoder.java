package example.server.endode;

import example.serialization.ISerializer;
import example.server.messages.MessageType;
import example.server.messages.State;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class StateEncoder extends MessageToByteEncoder<State> {
    private final ISerializer serializer;
    public StateEncoder(ISerializer serializer){
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, State msg, ByteBuf out) throws Exception {
        if (msg == null) throw new NullPointerException("сообщение не может быть null!!!");
        String jsonString = serializer.toJson(msg);

        // если это был последний клиент - утилизируем
        if (--msg.clientsCount == 0) msg.recycle();

        ByteBuf byteBuf = out
                .writeInt(jsonString.length())
                .writeInt(MessageType.STATE);

        byteBuf.writeCharSequence(jsonString, Charset.defaultCharset());
    }
}
