package example.domain.game.ecs.util;

import com.artemis.Component;
import com.artemis.World;
import example.interfaces.Action;

public class EntityBuilder {
    public final World world;

    public EntityBuilder(World world) {
        this.world = world;
    }

    private int buildEntityId = -1;

    public <T extends Component> EntityBuilder begin(int entityId,  Class<T> type) {
        if (buildEntityId != -1) throw new IllegalStateException("Билдер неверно закончил работу!");
        this.buildEntityId = entityId;
        return addComponent(type);
    }

    public <T extends Component> EntityBuilder begin(Class<T> type) {
        if (buildEntityId != -1) throw new IllegalStateException("Билдер неверно закончил работу!");
        buildEntityId = world.create();
        return addComponent(type);
    }

    public <T extends Component> EntityBuilder begin(Class<T> type, Action.Arg1<T> config) {
        if (buildEntityId != -1) throw new IllegalStateException("Билдер неверно закончил работу!");
        buildEntityId = world.create();
        return addComponent(type, config);
    }

    public <T extends Component> EntityBuilder addComponent(Class<T> type, Action.Arg1<T> config) {
        if (buildEntityId == -1) throw new IllegalStateException("Билдер нужно начать с метода begin");
        T component = createComponent(buildEntityId, type);
        config.invoke(component);
        return this;
    }

    public <T extends Component> EntityBuilder addComponent(Class<T> type) {
        if (buildEntityId == -1) throw new IllegalStateException("Билдер нужно начать с метода begin");
        createComponent(buildEntityId, type);
        return this;
    }

    public int end() {
        if (buildEntityId == -1) throw new IllegalStateException("Билдер нужно начать с метода begin");
        int id = buildEntityId;
        buildEntityId = -1;
        return id;
    }

    public <T extends Component> T createComponent(int entityId, Class<T> type) {
        return world.getMapper(type).create(entityId);
    }
}
