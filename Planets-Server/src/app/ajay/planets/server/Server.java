package app.ajay.planets.server;

import java.awt.Canvas;

import app.ajay.planets.base.CommandInfo;
import app.ajay.planets.base.Level;
import app.ajay.planets.base.Planet;
import app.ajay.planets.base.Player;
import app.ajay.planets.base.QueuedServerMessageAction;
import app.ajay.planets.base.ServerMessageQueueCallback;
import app.ajay.planets.server.networking.ServerMessageReceiver;
import app.ajay.planets.server.networking.WebSocketServerMessenger;

public class Server extends Canvas implements Runnable, ServerMessageReceiver, ServerMessageQueueCallback {
	
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
		float actualFrameDeltaTime = (System.nanoTime() - lastTime) / 1000000000f;
		
		//frames needed to be done this frame, can only do frames at the rate of level.deltaTime
		int framesNeeded = (int) (actualFrameDeltaTime / level.deltaTime);

		//update this many times
		for (int i = 0; i < framesNeeded; i++) {
			level.update(false);
			
			//add back how much time has passed
			lastTime += 1000000000 / level.physicsFrameRate;
		}
	}

	@Override
	public void onMessageRecieved(String message, int id) {
	 	QueuedServerMessageAction queuedServerMessageAction = new QueuedServerMessageAction(ServerPlayer.class, id, message);
	 	
	 	CommandInfo commandInfo = queuedServerMessageAction.getCommandCalled();
	 	
	 	int command = commandInfo.command;
	 	String[] argumentStrings = commandInfo.argumentStrings;
	 	
		if (command == -1) {
			System.err.println("Server sent unrecongnised command: " + message);
		} else {
			//add it to the queue, it can be ignored if it is unrecongnised
			level.queuedServerMessageActions.add(queuedServerMessageAction);
			
			//send out this command to all other clients
			for (Player player : level.players) {
				//modify the command so that it sends a proper frame number
				
				ServerPlayer messagePlayer = (ServerPlayer) level.getPlayerById(id);
				long relativeFrameNumber = Integer.parseInt(argumentStrings[1]) + messagePlayer.startFrame - ((ServerPlayer) player).startFrame;
				
				//this message contains the modified frame number
				String newMessageString = argumentStrings[0] + " " + id + " " + relativeFrameNumber;
				for (int i = 2; i < argumentStrings.length; i++) {
					newMessageString += " " + argumentStrings[i];
				}
				
				if (player.id != id) {
					messenger.sendMessageToClient(player.id, newMessageString);
				}
			}
		}
	}

	@Override
	public void onConnected(int id) {
		//add this action to the queue
		level.queuedServerMessageActions.add(new QueuedServerMessageAction(ServerPlayer.class, id, playerStartX, playerStartY, this));
	}

	@Override
	public void onDisconnected(int id) {
		//remove the player under this ID
		level.queuedServerMessageActions.add(new QueuedServerMessageAction(id, this));
	}
	
	@Override
	public void serverMessageActionCompleted(QueuedServerMessageAction queuedServerMessageAction) {
		switch (queuedServerMessageAction.actionType) {
		case MESSAGE_RECEIVED:
			//do nothing
			break;
		case PLAYER_CONNECTED:
			//send this new info to all clients
			for (Player player: level.players) {
				if (player.id != queuedServerMessageAction.id) {
					messenger.sendMessageToClient(player.id, "PC " + queuedServerMessageAction.id + " " + queuedServerMessageAction.x + " " + queuedServerMessageAction.y + " 0 0 false false");
				}
			}
			
			//send all the connected players to this player
			for (Player player: level.players) {
				if (player.id != queuedServerMessageAction.id) {
					messenger.sendMessageToClient(queuedServerMessageAction.id, "PC " + player.id + " " + player.x + " " + player.y
							+ " " + player.xSpeed + " " + player.ySpeed + " " + player.left + " " + player.right);
				}
			}
			break;
		case PLAYER_DISCONNECTED:
			//send this new info to all clients
			messenger.sendMessageToAll("PD " + queuedServerMessageAction.id);
			break;
		}
	}

}
