package example.server.messages;

import com.google.gson.annotations.Expose;

public class ServerCommand {
    @Expose
    public final int remotePlayerId;
    public ServerCommand(int playerId){
        this.remotePlayerId = playerId;
    }
}
