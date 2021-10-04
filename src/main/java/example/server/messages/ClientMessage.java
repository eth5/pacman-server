package example.server.messages;

import io.netty.util.Recycler;

import java.util.Arrays;

public class ClientMessage {
    private static final Recycler<ClientMessage> RECYCLER = new Recycler<ClientMessage>() {
        @Override
        protected ClientMessage newObject(Handle<ClientMessage> handle) {
            return new ClientMessage(handle);
        }
    };
    public static ClientMessage getInstance(){
        return RECYCLER.get();
    }

    public final int[] keys = new int[5];
    public int size;

    private final Recycler.Handle<ClientMessage> handle;

    private ClientMessage(Recycler.Handle<ClientMessage> handle){
        this.handle = handle;
    }

    public void recycle() {
        handle.recycle(this);
    }

    @Override
    public String toString() {
        return Arrays.toString(keys);
    }
}
