package example.serialization;

import com.artemis.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GSonSerializer implements ISerializer {
    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Component.class, new ClassPathSerializer<Component>("example.domain.game.ecs.components."))
            .create();

    @Override
    public <T> T createFromJson(String jasonString, Class<T> clazz) {
        return gson.fromJson(jasonString, clazz);
    }

    @Override
    public <T> String toJson(T object) {
        return gson.toJson(object);
    }
}
