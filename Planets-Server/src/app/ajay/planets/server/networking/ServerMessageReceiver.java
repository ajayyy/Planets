package app.ajay.planets.server.networking;

/**
 * Based on code from https://github.com/TooTallNate/Java-WebSocket
 * This is a library for Java websockets with some changes
 */
public interface ServerMessageReceiver {
	public void onMessageRecieved(String message, int id);

	public void onConnected(int id);

	public void onDisconnected(int id);
}