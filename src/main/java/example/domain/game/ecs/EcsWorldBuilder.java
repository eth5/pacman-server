package example.domain.game.ecs;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import example.domain.game.ecs.systems.*;
import example.domain.game.ecs.systems.collision.CollisionDetectSystem;
import example.domain.game.ecs.systems.collision.CollisionHandlerSystem;
import example.domain.game.ecs.systems.move.MoveToWayHandleSystem;
import example.domain.game.ecs.systems.move.NewPositionSystem;
import example.domain.game.ecs.systems.state.SendStateSystem;
import example.domain.game.ecs.systems.state.UpdateStateSystem;
import example.interfaces.Action;

/**
 * Класс для создания конфигурации ecs мира с дефолтными системами и возможностью внедрения зависимостей
 */

public class EcsWorldBuilder {
    private final WorldConfiguration worldConfiguration;
    private boolean isBuild = false;

    public EcsWorldBuilder() {
        worldConfiguration = getDefaultEcsSystems().build();
    }

    public World build() {
        return new World(worldConfiguration);
    }

    public EcsWorldBuilder dependencyInjection(Action.Arg1<WorldConfiguration> injector) {
        injector.invoke(worldConfiguration);
        return this;
    }

    private WorldConfigurationBuilder getDefaultEcsSystems() {
        WorldConfigurationBuilder builder = new WorldConfigurationBuilder()
                .with(
                        new InitialNewPlayerSystem(),
                        new PlayerSpawnSystem(),
                        new DelayRunSystem(),

                        new ClientInputHandleSystem(),

                        new MoveToWayHandleSystem(),
                        new NewPositionSystem(),

                        new CollisionDetectSystem(),
                        new CollisionHandlerSystem(),

                        new UpdateStateSystem(),
                        new SendStateSystem(),

                        new DeleteSystem()
                );
        return builder;
    }
}
