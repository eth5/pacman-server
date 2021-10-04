package example.server.messages;

import com.google.gson.annotations.Expose;

public class InitialGameDataMessage {
    @Expose
    public int width;
    @Expose
    public int height;
    @Expose
    public int remotePlayerId;
    public InitialGameDataMessage(int width, int height, int remotePlayerId){
        this.width = width;
        this.height = height;
        this.remotePlayerId = remotePlayerId;
    }
}
