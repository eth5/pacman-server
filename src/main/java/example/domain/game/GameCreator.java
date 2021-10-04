package example.domain.game;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.utils.IntBag;
import example.domain.game.configuration.FieldConfig;
import example.domain.game.configuration.GetFieldConfig;
import example.domain.game.ecs.EcsWorldBuilder;
import example.domain.game.ecs.components.PlayerSpawn;
import example.domain.game.ecs.components.Road;
import example.domain.game.ecs.components.events.DelayRun;
import example.interfaces.Action;
import example.log.Log;
import example.util.resource.Error;
import example.util.resource.Resource;
import example.util.resource.Success;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GameCreator {
    public Resource<Game> creteFrom(File file, Action onEndGame){
        try {

            //парсим конфиг файл в более удобный формат для дальнейшей работы
            final FieldConfig fieldConfig = new GetFieldConfig().fromFile(file);


            //будет содержать в себе коллекцию id кажой сущности, для удобного поиска сущностей по координатам
            final IntBag[][] entitiesIdsByPosition = createFieldArray(fieldConfig.columns, fieldConfig.rows);

            final World world = createWorld(entitiesIdsByPosition, onEndGame);
            Game game = new Game(world, fieldConfig.columns,fieldConfig.rows);

            new FieldEntityCreator(world).build(fieldConfig.itemsData);
            createSpawnComponents(world,entitiesIdsByPosition,fieldConfig.spawnPositions);

            return new Success<>(game);

        } catch (IOException e) {
            e.printStackTrace();
            return new Error<Game>(e.getMessage());
        }
    }


    private void createSpawnComponents(World world, final IntBag[][] entitiesIdsByPosition, List<int[]> spawnPositions){
        DelayRun delayRun = world.getMapper(DelayRun.class).create(world.create());
        // запускаем в следующем фрейме т.к. сущносты еще не разложены по своим позициям
        // за это отвечает NewPositionSystem которая на данном этапе еще не отработала т.к. world не получал update
        // команду
        delayRun.skipFrames = 1;
        delayRun.action = ()->{
            ComponentMapper<Road> mapper = world.getMapper(Road.class);
            ComponentMapper<PlayerSpawn> spawnCM = world.getMapper(PlayerSpawn.class);

            for (int[] spawnPosition : spawnPositions) {

                IntBag intBag = entitiesIdsByPosition[spawnPosition[0]][spawnPosition[1]];

                int[] data = intBag.getData();
                int size = intBag.size();
                boolean notFound = true;
                for (int i = 0; i < size; i++) {
                    if (mapper.has(data[i])){
                        spawnCM.create(data[i]);
                        notFound = false;
                        break;
                    }
                }
                if (notFound){
                    Log.e(this, "Не найдена сущность для точки спауна, позиция = " + Arrays.toString(spawnPosition));
                }
            }

        };
    }

    // двумерный массив коллекций идентификаторов сущностей, обновляется в NewPositionSystem
    private IntBag[][] createFieldArray(int widht, int height){
        IntBag[][] field = new IntBag[height][widht];

        for (int line = 0; line < field.length; line++) {
            IntBag[] intBags = field[line];
            for (int position = 0; position < intBags.length; position++) {
                intBags[position] = new IntBag(5);
            }
        }

        return field;
    }
    // создаем ecs world и парсим зависимости в системы
    private World createWorld(final IntBag[][] entitiesIdsByPosition, Action onEndGame){
        return new EcsWorldBuilder()
                .dependencyInjection(
                        worldCfgBuilder->{
                            worldCfgBuilder
                                    .register("entitiesIdsByPosition", entitiesIdsByPosition)
                                    .register("onEndGame", onEndGame);
                        })
                .build();
    }
}
