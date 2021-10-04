package example.server.endode;

import example.serialization.ISerializer;
import example.server.messages.InitialGameDataMessage;
import example.server.messages.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class InitialGameDataMessageEncoder extends MessageToByteEncoder<InitialGameDataMessage> {
    private final ISerializer serializator;
    public InitialGameDataMessageEncoder(ISerializer serializator){
        this.serializator = serializator;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, InitialGameDataMessage msg, ByteBuf out) throws Exception {
        if (msg == null) throw new NullPointerException("сообщение не может быть null!!!");

        String jsonString = serializator.toJson(msg);
        out.writeInt(jsonString.length()).writeInt(MessageType.INITIAL_GAME_DATA);
        out.writeCharSequence(jsonString, Charset.defaultCharset());
    }
}
