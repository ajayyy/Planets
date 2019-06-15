package app.ajay.planets.base;

/**
 * Class that stores data used in {@link QueuedServerMessageAction} 
 * when processing messages sent between the server and clients.
 */
public class CommandInfo {
	/** The index of this command */
	public int command;
	
	public String[] argumentStrings;
	
	public CommandInfo(int command, String[] argumentStrings) {
		this.command = command;
		this.argumentStrings = argumentStrings;
	}
}
