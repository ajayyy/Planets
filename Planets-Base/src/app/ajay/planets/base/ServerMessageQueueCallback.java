package app.ajay.planets.base;

public interface ServerMessageQueueCallback {
	
	/**
	 * Used by the server so it can get a callback to send messages to the client
	 * once connections and disconnections have been processed.
	 * 
	 * @param queuedServerMessageAction
	 */
	public abstract void serverMessageActionCompleted(QueuedServerMessageAction queuedServerMessageAction);
}
