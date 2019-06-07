package app.ajay.planets.server.networking;

/**
 * Interface used to grab messages from a websocket server
 */
public interface ClientMessageReceiver {
	public void onMessageRecieved(String message, int id);

	public void onConnected(int id);

	public void onDisconnected(int id);
}