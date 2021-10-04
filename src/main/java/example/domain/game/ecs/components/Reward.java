package example.domain.game.ecs.components;

import com.artemis.PooledComponent;
import com.google.gson.annotations.Expose;

/**
 * Компонент делающий сущность - вознаграждением. С соответсвующим количеством(score)
 */

public class Reward extends PooledComponent {
    @Expose
    public int score = 0;
    @Override
    protected void reset() {
        score = 0;
    }
}
