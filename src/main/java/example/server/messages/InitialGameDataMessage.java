package example.server.messages;

import com.google.gson.annotations.Expose;

public class InitialGameDataMessage {
    @Expose
    public int rows;
    @Expose
    public int columns;
    @Expose
    public int remotePlayerId;
    public InitialGameDataMessage(int rows, int columns, int remotePlayerId){
        this.rows = rows;
        this.columns = columns;
        this.remotePlayerId = remotePlayerId;
    }
}
