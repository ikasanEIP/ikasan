package org.ikasan.dashboard.cache;

import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowState;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CacheStateBroadcaster
{
    static Executor executor = Executors.newSingleThreadExecutor();

    static LinkedList<Consumer<FlowState>> listeners = new LinkedList<>();

    public static synchronized Registration register(Consumer<FlowState> listener)
    {
        listeners.add(listener);

        return () ->
        {
            synchronized (CacheStateBroadcaster.class)
            {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(FlowState message)
    {
        for (Consumer<FlowState> listener : listeners)
        {
            executor.execute(() -> listener.accept(message));
        }
    }
}
