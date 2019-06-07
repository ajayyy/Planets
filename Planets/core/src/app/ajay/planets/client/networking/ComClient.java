package app.ajay.planets.client.networking;

/**
 * Based on code from https://github.com/TooTallNate/Java-WebSocket
 * This is a library for Java websockets with some changes
 */
public interface ComClient {
	public void connectClient(String ip);

	public boolean sendMsg(String msg);

	public boolean isConnected();

	public int getId();

	public void close();
}