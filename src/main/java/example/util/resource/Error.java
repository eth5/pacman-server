package example.util.resource;

public class Error<T> extends Resource<T> {
    public Error(String msg){
        this.msg = msg;
    }
}
