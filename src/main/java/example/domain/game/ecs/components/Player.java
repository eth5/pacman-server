package example.domain.game.ecs.components;

import com.artemis.Component;
import com.google.gson.annotations.Expose;

/**
 * Компонент содержит игровые данные игрока :)
 */

public class Player extends Component {
    @Expose
    public int score = 0;
}
