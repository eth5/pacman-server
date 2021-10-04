package example.server.decode;

import example.server.messages.ClientMessage;
import example.log.Log;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    private boolean readLen = true;
    private int len = 0;
    private final byte[] array = new byte[5 * 4];

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (readLen){
            if (in.readableBytes() < 4) return;
            len = in.readInt();
            if (len > 5){
                Log.e("Превышена максимальная длина сообщения!");
                ctx.close();
            }else{
                readLen = false;
            }
            return;
        }
        if (in.readableBytes() < len * 4) return;
        ClientMessage clientMessage = ClientMessage.getInstance();

        for (int i = 0; i < len; i++) {
            clientMessage.keys[i] = in.readInt();
        }
        clientMessage.size = len;
        out.add(clientMessage);
        readLen = true;
    }
}
