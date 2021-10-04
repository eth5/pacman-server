package example.domain.game;

import com.artemis.World;
import example.domain.game.ecs.components.Position;
import example.domain.game.ecs.components.Reward;
import example.domain.game.ecs.components.Road;
import example.domain.game.ecs.components.Wall;
import example.domain.game.ecs.components.events.NewPositionTarget;
import example.domain.game.ecs.util.EntityBuilder;

import java.util.List;


/**
 * Класс создает игровое поле (стены и дороги) из конфигурации
 */

public class FieldEntityCreator {
    public static final String ROAD = "0";
    public static final String WALL = "1";

    private final World world;
    private final EntityBuilder entityBuilder;

    public FieldEntityCreator(World world){
        this.world = world;
        entityBuilder = new EntityBuilder(world);
    }

    public void build(List<String[]> cfg){
        createFieldItems(cfg);
    }


    private void createFieldItems(List<String[]> cfg){
        for (int columnIndex = 0; columnIndex < cfg.size(); columnIndex++) {
            String[] cgfLine = cfg.get(columnIndex);
            for (int rowIndex = 0; rowIndex < cgfLine.length; rowIndex++){
                int fieldItemId = createFrom(cgfLine[rowIndex], columnIndex, rowIndex);
            }
        }
    }

    private int createFrom(String itemTypeValue, int line, int position){
        int fieldItemId = createSimpleItem(itemTypeValue, line, position);
        return fieldItemId;
    }

    private int createSimpleItem(String type, int line, int position){
        boolean createRewardInPosition = false;
        switch (type){
            case ROAD -> {
                entityBuilder.begin(Road.class);
                createRewardInPosition = true;
            }
            case WALL -> entityBuilder.begin(Wall.class);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        int entityId = entityBuilder
                .addComponent(Position.class)
                .addComponent(NewPositionTarget.class, np->np.set(line, position))
                .end();

        if (createRewardInPosition) createReward(line, position);

        return entityId;
    }

    private void createReward(int line, int position){
        entityBuilder
                .begin(Reward.class, r->r.score = 1)
                .addComponent(Position.class)
                .addComponent(NewPositionTarget.class, np->np.set(line, position))
                .end();
    }
}
