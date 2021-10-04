package example.domain.game.ecs.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import example.domain.game.ecs.components.events.DelayRun;



@All(DelayRun.class)
public class DelayRunSystem extends IteratingSystem {

    private ComponentMapper<DelayRun> delayRunCM;

    @Override
    protected void process(int entityId) {
        DelayRun delayRun = delayRunCM.get(entityId);
        if ( delayRun.skipFrames-- > 0 ) return;;
        delayRun.action.invoke();
        world.delete(entityId);
    }
}
