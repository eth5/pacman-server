package example;

import example.server.Server;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File file = new File("maps");

        GameWrapper gameWrapper = new GameWrapper(file);
        gameWrapper.runRandomMap();

        try {
            new Server(35000).start(
                    (ctx, message) -> gameWrapper.messageFrom(ctx.channel(), message),
                    ctx -> gameWrapper.addChannel(ctx.channel()),
                    ctx -> gameWrapper.removeChannel(ctx.channel())
                    );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
