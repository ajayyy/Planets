package app.ajay.planets.server;

import java.awt.Canvas;

public class Server extends Canvas implements Runnable{
	
	private static final long serialVersionUID = -7693060249008916237L;
	Thread serverThread;
	
	public Server() {
		//Start server thread
		serverThread = new Thread(this);
		serverThread.start();
	}
	
	public static void main(String[] args) {
		
		//create new instance of server
		Server server = new Server();
	}

	@Override
	public void run() {
		
		while (true) {
			//server loop
			
		}
	}
	
}
