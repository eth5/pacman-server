package example.server.messages;

import com.google.gson.annotations.Expose;
import io.netty.util.Recycler;

import java.util.ArrayList;
import java.util.List;

public class State {
    private static final Recycler<State> RECYCLER = new Recycler<State>() {
        @Override
        protected State newObject(Handle<State> handle) {
                return new State(handle);
        }
    };
    public static State getInstance(){
        return RECYCLER.get();
    }
    private final Recycler.Handle<State> handler;
    private State(Recycler.Handle<State> handler){
        this.handler = handler;
    }
    // количество клиентов для отправки
    public int clientsCount;
    @Expose
    private final List<StateEntityData> entities = new ArrayList<>();

    public void add(StateEntityData stateEntityData){
        entities.add(stateEntityData);
    }
    public void addAll(List<StateEntityData> entities){
        this.entities.addAll(entities);
    }

    public void recycle(){
        for (StateEntityData entity : entities) {
            entity.recycle();
        }
        entities.clear();
        clientsCount = 0;
        handler.recycle(this);
    }
}
