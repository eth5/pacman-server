package example.serialization;

public interface ISerializer {
    public <T> T createFromJson(String jasonString, Class<T> clazz);
    public <T> String toJson(T object);
}
