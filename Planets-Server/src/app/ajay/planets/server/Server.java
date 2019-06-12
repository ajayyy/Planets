package app.ajay.planets.server;

import java.awt.Canvas;

import app.ajay.planets.base.Level;
import app.ajay.planets.base.Planet;
import app.ajay.planets.base.Player;
import app.ajay.planets.base.Projectile;
import app.ajay.planets.server.networking.ServerMessageReceiver;
import app.ajay.planets.server.networking.WebSocketServerMessenger;

public class Server extends Canvas implements Runnable, ServerMessageReceiver {
	
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
	float playerStartY = 400;
	
	/**
	 * The list of commands that could be sent from the clients
	 * 
	 * Player shot, Left pressed, Right pressed, left disabled, right disabled
	 */
	String[] commands = {"S", "L", "R", "LD", "RD"};
	
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
		//first item is the command itself
		String[] argumentStrings = new String[0];
		
		//what command has been sent
		int command = -1;
		for (int i = 0; i < commands.length; i++) {
			if (message.startsWith(commands[i])) {
				command = i;
				
				argumentStrings = message.split(" ");
				break;
			}
		}
		
		if (command == -1) {
			System.err.println("Server sent unrecongnised command: " + message);
		} else {
			//send out this command to all other clients
			for (Player player : level.players) {
				if (player.id != id) {
					messenger.sendMessageToClient(player.id, message + " " + id);
				}
			}
		}
		
		//handle the command
		switch (command) {
		case 0:
			//player shot
			level.getPlayerById(id).launchProjectile(Projectile.class, level, Float.parseFloat(argumentStrings[1]));
			break;
		case 1:
			//left pressed
			level.getPlayerById(id).left = true;
			break;
		case 2:
			//right pressed
			level.getPlayerById(id).right = true;
			break;
		case 3:
			//left unpressed
			level.getPlayerById(id).left = false;
			break;
		case 4:
			//right unpressed
			level.getPlayerById(id).right = false;
			break;
		}
	}

	@Override
	public void onConnected(int id) {
		//make a new player under this id

		//send this new info to all clients
		for (Player player: level.players) {
			messenger.sendMessageToClient(player.id, "PC " + id + " " + playerStartX + " " + playerStartY + " 0 0 false false");
		}
		
		level.players.add(new Player(id, playerStartX, playerStartY));
		
		//send all the connected players to this player
		for (Player player: level.players) {
			if (player.id != id) {
				messenger.sendMessageToClient(id, "PC " + player.id + " " + player.x + " " + player.y
						+ " " + player.xSpeed + " " + player.ySpeed + " " + player.left + " " + player.right);
			}
		}
	}

	@Override
	public void onDisconnected(int id) {
		//remove the player under this ID
		for (int i = 0; i < level.players.size(); i++) {
			if (level.players.get(i).id == id) {
				level.players.remove(i);
				break;
			}
		}
		
		//send this new info to all clients
		messenger.sendMessageToAll("PD " + id);

	}
	
}
