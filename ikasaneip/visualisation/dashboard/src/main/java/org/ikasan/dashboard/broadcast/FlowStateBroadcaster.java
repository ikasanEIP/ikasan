package org.ikasan.dashboard.broadcast;

import com.vaadin.flow.shared.Registration;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FlowStateBroadcaster
{
    static Executor executor = Executors.newSingleThreadExecutor();

    static LinkedList<Consumer<FlowState>> listeners = new LinkedList<>();

    public static synchronized Registration register(Consumer<FlowState> listener)
    {
        listeners.add(listener);

        return () ->
        {
            synchronized (FlowStateBroadcaster.class)
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
