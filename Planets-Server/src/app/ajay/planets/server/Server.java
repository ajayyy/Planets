package app.ajay.planets.server;

import java.awt.Canvas;

import app.ajay.planets.base.Level;
import app.ajay.planets.base.Planet;
import app.ajay.planets.base.Player;
import app.ajay.planets.server.networking.ClientMessageReceiver;
import app.ajay.planets.server.networking.WebSocketServerMessenger;

public class Server extends Canvas implements Runnable, ClientMessageReceiver {
	
	private static final long serialVersionUID = -7693060249008916237L;
	Thread serverThread;
	
	/**
	 * The websocket server
	 */
	WebSocketServerMessenger messenger;
	
	Level level;
	
	/**
	 * Used when calculating frame deltaTime
	 */
	long lastTime = -1;
	
	//starting positions of all players
	float playerStartX = 0;
	float playerStartY = 300;
	
	public Server() {
		//init all variables and systems
		init();
		
		//start up the web socket server
		messenger = new WebSocketServerMessenger(2492, this);
		
		//Start server thread
		serverThread = new Thread(this);
		serverThread.start();
	}
	
	public static void main(String[] args) {
		//create new instance of server
		Server server = new Server();
	}
	
	public void init() {
		level = new Level();
		
		//add a default planet
		level.planets.add(new Planet(0, 0, 300));
		
		//setup last frame time
		lastTime = System.nanoTime();
	}

	@Override
	public void run() {
		while (true) {
			//server loop
			update();
		}
	}
	
	public void update() {
		level.deltaTime = (System.nanoTime() - lastTime) / 1000000000f;
		lastTime = System.nanoTime();

		level.update();
	}

	@Override
	public void onMessageRecieved(String message, int id) {
		
	}

	@Override
	public void onConnected(int id) {
		//make a new player under this id
		
		level.players.add(new Player(id, playerStartX, playerStartY));
	}

	@Override
	public void onDisconnected(int id) {
		
	}
	
}
