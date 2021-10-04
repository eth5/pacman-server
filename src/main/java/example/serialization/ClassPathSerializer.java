package example.serialization;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ClassPathSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private static final String CLASSNAME = "CN";
    private static final String DATA = "DATA";
    private final String pathToPackage;
    public ClassPathSerializer(String pathToPackage){
        this.pathToPackage = pathToPackage;
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        if (src!=null){
            String name0 = src.getClass().getName();
            String name2 = name0.replaceFirst(pathToPackage, "");
            jsonObject.addProperty(CLASSNAME, name2);

            JsonElement serialize = context.serialize(src);
            jsonObject.add(DATA, serialize);
        }
        return jsonObject;
    }
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        T value = null;
        if (jsonObject != null){
            String className = pathToPackage + jsonObject.getAsJsonPrimitive(CLASSNAME).getAsString();
            try {
                Class<?> aClass = Class.forName(className);
                value = context.deserialize(jsonObject.get(DATA), aClass);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return value;
    }
}
