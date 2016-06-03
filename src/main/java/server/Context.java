package server;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {
    public static final String CONTEXT_KEY = "context";

    @NotNull
    private final Map<Class, Object> contextMap = new ConcurrentHashMap<>();

    public void put(@NotNull Class aClass, @NotNull Object object){
        contextMap.put(aClass, object);
    }

    //@NotNull
    public <T> T get(@NotNull Class<T> aClass){
        //noinspection unchecked
        return (T) contextMap.get(aClass);
    }

}