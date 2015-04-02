package org.ikasan.dashboard.ui;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broadcaster implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6315712258026245193L;

	static ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	public interface BroadcastListener 
	{
		void receiveBroadcast(String message);
	}

	private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

	public static synchronized void register(BroadcastListener listener)
	{
		listeners.add(listener);
	}

	public static synchronized void unregister(BroadcastListener listener) 
	{
		listeners.remove(listener);
	}

	public static synchronized void broadcast(final String message)
	{
		for (final BroadcastListener listener : listeners)
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					listener.receiveBroadcast(message);
				}
			});
	}
}