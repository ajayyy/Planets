package app.ajay.planets.base;

import java.lang.reflect.Field;
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
	 * If the data is coming from a client to the server, than the frame count will be relative to the client.
	 * The player's start time must be added to this relative frame count of be accurate.
	 */
	public boolean removePlayerStartTime = false;
	
	/**
	 * Used if this is being used on the client and a new player is going to be created.
	 * This allows a client player to be created.
	 */
	Class<? extends Player> playerClass = Player.class;
	
	/** 
	 * The callback used for messages that the server deals with. 
	 * Only used for player connects and disconnects, not messages
	 */
	ServerMessageQueueCallback serverMessageQueueCallback;
	/** The coordinates used to spawn players in the PLAYER_CONNECTED action type */
	public float x, y;
	
	/**
	 * The list of commands that could be sent
	 * 
	 * Player connected, player disconnected, player shot, left, right, left disabled, right disabled
	 */
	String[] commands = {"PC", "PD", "S", "L", "R", "LD", "RD"};
	
	public enum ServerMessageActionType {
		MESSAGE_RECEIVED,
		PLAYER_CONNECTED
	}
	
	public QueuedServerMessageAction(Class<? extends Player> playerClass) {
		this.playerClass = playerClass;
	}
	
	/**
	 * Constructor used for a MESSAGE_RECEIVED action
	 * The player class is needed for this command in case a player connected.
	 * 
	 * @param frame
	 * @param playerClass The player class to instantiate if necessary, such as ClientPlayer or ServerPlayer.
	 * @param message
	 */
	public QueuedServerMessageAction(Class<? extends Player> playerClass, String message) {
		this(playerClass);
		
		this.message = message;
		
		actionType = ServerMessageActionType.MESSAGE_RECEIVED;
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
	public QueuedServerMessageAction(Class<? extends Player> playerClass, int id, String message) {
		this(playerClass, message);
		
		this.id = id;
		//because the id is known, this is a server message
		//so, we must convert the relative time to a server absolute time
		//with this variable
		removePlayerStartTime = true;
	}
	
	/**
	 * This is the constructor used by the server when a player connects.
	 * 
	 * It takes a callback so that the server can send messages to the other clients when the player
	 * has been added to the simulation on the server.
	 * 
	 * @param playerClass The type of player to instantiate. (Probably ServerPlayer)
	 * @param id The id of the player being added.
	 * @param x
	 * @param y
	 * @param serverMessageQueueCallback
	 */
	public QueuedServerMessageAction(Class<? extends Player> playerClass, int id, float x, float y, ServerMessageQueueCallback serverMessageQueueCallback) {
		this(playerClass);
		
		this.id = id;
		this.x = x;
		this.y = y;
		
		this.serverMessageQueueCallback = serverMessageQueueCallback;
		
		actionType = ServerMessageActionType.PLAYER_CONNECTED;
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
		case PLAYER_CONNECTED:
			playerConnected(level, id);
			break;
		}
	}
	
	/**
	 * Gets the command being called in this message and splits out the arguments
	 * 
	 * @param message
	 * @return
	 */
	public CommandInfo getCommandCalled() {
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
		
		return new CommandInfo(command, argumentStrings);
	}
	
	/**
	 * Code for when a message is received
	 */
	public void messageReceived(Level level) {
		CommandInfo commandInfo = getCommandCalled();
		
		//get returned data out of the class
		int command = commandInfo.command;
		String[] argumentStrings = commandInfo.argumentStrings;
		
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
			playerConnectedFromMessage(level, id, argumentStrings);
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
	
	public void playerConnectedFromMessage(Level level, int id, String[] argumentStrings) {
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
	
	public void playerConnected(Level level, int id) {
		//create the new player and add it to the list using reflection (since this class has no direct access to client classes)
		try {
			Player player = (Player) playerClass.getDeclaredConstructor(int.class, float.class, float.class, long.class)
					.newInstance(id, x, y, level.frame);
							
			level.players.add(player);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		//call the callback, this has completed
		serverMessageQueueCallback.serverMessageActionCompleted(this);
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
		
		long frame = getRelativeFrameNumber(Integer.parseInt(argumentStrings[1]), player);
		
		level.launchProjectileAtFrame(level, frame, player, Float.parseFloat(argumentStrings[2]));
	}
	
	/**
	 * Gets the absolute frame to this program.
	 * 
	 * This is necessary on the server since the frames received are relative to the client
	 * and not the absolute server frame. So, they must be converted.
	 * 
	 * @param frame The frame being received from others.
	 * @param player The player that sent this message.
	 * @return The frame to use in calculations.
	 */
	public long getRelativeFrameNumber(int frame, Player player) {
		if (removePlayerStartTime) {
			//get the start frame using reflection and add it to the frame number
			try {
				Field startFrameField = playerClass.getDeclaredField("startFrame");
				startFrameField.setAccessible(true);
				
				return frame + startFrameField.getLong(player);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return frame;
	}
}
