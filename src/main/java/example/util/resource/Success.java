package example.util.resource;

public class Success<T> extends Resource<T> {
    public Success(T data){
        this.data = data;
    }
}
