package example.domain.game.ecs.util;

import com.artemis.World;
import example.domain.game.ecs.components.events.DelayRun;
import example.interfaces.Action;

public class WorldUtil {
    private WorldUtil(){}

    public static void delayRunEvent(World world, int skipFrame, Action action){
        DelayRun delayRun = world.getMapper(DelayRun.class).create(world.create());
        delayRun.skipFrames = skipFrame;
        delayRun.action = action;
    }
}
