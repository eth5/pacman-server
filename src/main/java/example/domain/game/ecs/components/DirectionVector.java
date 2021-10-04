package example.domain.game.ecs.components;

import com.artemis.PooledComponent;
import com.google.gson.annotations.Expose;

/**
 * Компонент содержит верктор направления клиента
 */

public class DirectionVector extends PooledComponent {
    @Expose
    public int x;
    @Expose
    public int y;


    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void setZero(){
        x = 0;
        y = 0;
    }

    @Override
    protected void reset() {
        x = 0;
        y = 0;
    }

}
