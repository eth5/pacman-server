package example.domain.game.ecs.components.events;

import com.artemis.PooledComponent;

/**
 * Компонент-событие.
 */

public class NewPositionTarget extends PooledComponent {

    public int line;
    public int lineIndex;

    public void set(int line, int lineIndex){
        this.line = line;
        this.lineIndex = lineIndex;
    }

    @Override
    protected void reset() {
        line = -1;
        lineIndex = -1;
    }
}
