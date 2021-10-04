package example.domain.game.ecs.components;

import com.artemis.PooledComponent;
import com.google.gson.annotations.Expose;

/**
 * Позиция сущности в игровых координатах
 * так же содержит в себе время в ms до наступления которого должны игнорироваться команды перемещания игрока
 * чтобы не спидхачил, время не передается клиенту
 */

public class Position extends PooledComponent {
    public static final long BLOCK_TIME = 100L;
    @Expose
    public int lineIndex;
    @Expose
    public int line;

    public long blockTime;

    @Override
    protected void reset() {
        lineIndex = 0;
        line = 0;
        blockTime = 0;
    }

    public void set(int line, int lineIndex){
        this.lineIndex = lineIndex;
        this.line = line;
        this.blockTime = System.currentTimeMillis() + BLOCK_TIME;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position){
            Position position = (Position) obj;
            return position.lineIndex == lineIndex && position.line == line;
        }
        return super.equals(obj);
    }
}
