package example;

import example.server.messages.ClientMessage;
import example.domain.game.Connection;
import example.domain.game.Game;
import example.domain.game.GameCreator;
import example.log.Log;
import example.util.resource.Resource;
import example.util.resource.Success;

import java.io.File;

public class GameWrapper {
    private boolean isGameSet = false;
    private Game game;
    private final File file;

    public GameWrapper(File file){
        this.file = file;
    }
    public void createNewGame(File file){
        this.game = null;
        isGameSet = false;
        Resource<Game> gameResource = new GameCreator().creteFrom(file, this::onEndGame);
        if (gameResource instanceof Success) {

            setGame(gameResource.data);

        }else{
            Log.e(this, gameResource.msg);
        }
    }


    private void setGame(Game game){
        this.game = game;
        isGameSet = true;
        game.start();
    }

    private void onEndGame(){
        Log.e(this,"end game!");
    }

    public void messageFrom(Connection connection, ClientMessage message) {
        if (isGameSet)game.messageFrom(connection, message);
    }

    public synchronized void addConnection(Connection connection) {
        if (isGameSet)game.addConnection(connection);
    }

    public synchronized void removeConnection(Connection connection) {
        if (isGameSet)game.removeConnection(connection);
    }
}
