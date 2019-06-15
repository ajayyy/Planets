package app.ajay.planets.base;

import java.awt.SystemTray;
import java.lang.reflect.InvocationTargetException;

/**
 * Similar to {@link QueuedPlayerAction}, but for server messages.
 * This is to make all networking get processed only on the main thread.
 */
public class QueuedServerMessageAction {
	
	public ServerMessageActionType actionType;
	
	//for message received
	public String message;
	
	/** 
	 * Will be blank for client messages, as the data is instead stored inside the message String.
	 * For server messages, it is not stored there.
	 */
	public int id = -1;
	
	/**
	 * Used if this is being used on the client and a new player is going to be created.
	 * This allows a client player to be created.
	 */
	Class<? extends Player> playerClass;
	
	/**
	 * The list of commands that could be sent
	 * 
	 * Player connected, player disconnected, player shot, left, right, left disabled, right disabled
	 */
	String[] commands = {"PC", "PD", "S", "L", "R", "LD", "RD"};
	
	public enum ServerMessageActionType {
		MESSAGE_RECEIVED
	}
	
	/**
	 * The constructor used for a MESSAGE_RECEIVED action
	 * 
	 * @param message
	 */
	public QueuedServerMessageAction(String message) {
		this.message = message;
		
		actionType = ServerMessageActionType.MESSAGE_RECEIVED;
	}
	
	/**
	 * Constructor used by the client side to be able to support the player connected command.
	 * The player class is needed for this command.
	 * 
	 * @param frame
	 * @param playerClass The player class to instantiate if necessary, such as ClientPlayer.
	 * @param message
	 */
	public QueuedServerMessageAction(Class<? extends Player> playerClass, String message) {
		this(message);
		this.playerClass = playerClass;
	}
	
	/**
	 * Constructor used by server class.
	 * Since the server already knows what id this message is coming from, it doesn't need to process that out
	 * and can take it as an input.
	 * 
	 * @param frame
	 * @param id The player id that this message is from.
	 * @param message
	 */
	public QueuedServerMessageAction(int id, String message) {
		this(message);
		
		this.id = id;
	}
	
	/**
	 * Execute this queued event.
	 */
	public void execute(Level level) {
		//execute differently based on action type
		switch (actionType) {
		case MESSAGE_RECEIVED:
			messageReceived(level);
			break;
		}
	}
	
	/**
	 * Code for when a message is received
	 */
	public void messageReceived(Level level) {
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
		}
		
		if (id == -1) {
			id = Integer.parseInt(argumentStrings[1]);
			//remove id from the arguments
			String[] cleanedArgumentsString = new String[argumentStrings.length - 1]; 
			for (int i = 0; i < argumentStrings.length; i++) {
				//ignore the i == 1 index, which is the player id
				if (i < 1) {
					cleanedArgumentsString[i] = argumentStrings[i];
				} else if (i > 1) {
					cleanedArgumentsString[i - 1] = argumentStrings[i];
				}
			}
			
			//set argumentStrings to this new cleaned version without the player id
			argumentStrings = cleanedArgumentsString;
		}
		
		//handle the command
		switch (command) {
		case 0:
			//player connected
			playerConnected(level, id, argumentStrings);
			break;
		case 1:
			//player disconnected
			playerDisconnected(level, id, argumentStrings);
			break;
		case 2:
			//player shot
			playerLaunchedProjectile(level, id, argumentStrings);
			break;
		case 3:
			//left pressed
			level.getPlayerById(id).left = true;
			break;
		case 4:
			//right pressed
			level.getPlayerById(id).right = true;
			break;
		case 5:
			//left unpressed
			level.getPlayerById(id).left = false;
			break;
		case 6:
			//right unpressed
			level.getPlayerById(id).right = false;
			break;
		}
	}
	
	public void playerConnected(Level level, int id, String[] argumentStrings) {
		//create the new player and add it to the list using reflection (since this class has no direct access to client classes)
		try {
			Player player = (Player) playerClass.getDeclaredConstructor(int.class, float.class, float.class, float.class, float.class, boolean.class, boolean.class)
					.newInstance(id, Float.parseFloat(argumentStrings[1]), 
							Float.parseFloat(argumentStrings[2]), Float.parseFloat(argumentStrings[3]), Float.parseFloat(argumentStrings[4]), 
							Boolean.parseBoolean(argumentStrings[5]), Boolean.parseBoolean(argumentStrings[6]));
							
			level.players.add(player);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void playerDisconnected(Level level, int id, String[] argumentStrings) {
		//remove the player under this ID
		for (int i = 0; i < level.players.size(); i++) {
			if (level.players.get(i).id == id) {
				level.players.remove(i);
				break;
			}
		}
	}
	
	public void playerLaunchedProjectile(Level level, int id, String[] argumentStrings) {
		//launch projectile at the frame it happened
		Player player = level.getPlayerById(id);
		level.launchProjectileAtFrame(level, Integer.parseInt(argumentStrings[1]), player, Float.parseFloat(argumentStrings[2]));
	}
}
