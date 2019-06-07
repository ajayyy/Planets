package app.ajay.planets.client.networking;

import com.badlogic.gdx.Application.ApplicationType;

/**
 * Based on code from https://github.com/TooTallNate/Java-WebSocket
 * This is a library for Java websockets with some changes
 */
public class WebSocketClientMessenger {
	ComClient cc;

	ClientMessageReceiver receiver;

	// For Bidirectional Communication mode
	public WebSocketClientMessenger(String ip, int port, ApplicationType device, ClientMessageReceiver receiver) {
//		if (device == ApplicationType.WebGL) {
//			// Only available on the HTML project - ClientMSG class
//			//currently unsupported and uncommented
//			cc = new GWTClient(ip, port, this);
//		}
		
		if (device == ApplicationType.Desktop) {
			// Only available on the java project (such as Android)
			try {
				//reflection is used in this instance to work later since this is planned to support HTML, the Java classes are only in the Java parts of the project
				cc = (ComClient) Class.forName("app.ajay.planets.client.desktop.networking.WSClient")
						.getConstructor(String.class, Integer.TYPE, WebSocketClientMessenger.class)
						.newInstance(ip, port, this);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Java Websockets BEING CALLED FROM NON JAVA PLATFORM (or WSClient just missing)");
			}
		}
		this.receiver = receiver;
	}

	public void onMessage(String message) {
		receiver.onMessageRecieved(message);
	}

	public void onConnect(long time) {
		receiver.onConnect(time);
	}

	public boolean sendMessage(String message) {
		if (cc != null && cc.isConnected()) {
			// Only send message
			return (cc.sendMsg(message));
		} else
			return false;
	}

	public int getId() {
		return (cc.getId());
	}

	public void close() {
		cc.close();
	}
}