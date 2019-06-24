package app.ajay.planets.client.networking;

/**
 * Based on code from https://github.com/TooTallNate/Java-WebSocket
 * This is a library for Java websockets with some changes
 */
public interface ClientMessageReceiver {
	public void onMessageRecieved(String message);

	/**
	 * When connected to a server
	 * 
	 * @param time The epoch time for this connection
	 */
	public void onConnect(long time);
}
