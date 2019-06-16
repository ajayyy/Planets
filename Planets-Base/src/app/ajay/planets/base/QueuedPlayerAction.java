package app.ajay.planets.base;

/**
 * Class used for player actions that are queued for frames in the future.
 * Sometimes other clients are running faster than this client, so their 
 * actions will happen in the future.
 */
public class QueuedPlayerAction {
	
	public long frame;
	public PlayerActionType actionType;
	
	/** The player this action is referring to */
	public Player player;
	
	//for projectile launch
	public float projectileAngle;
	
	//for player connection
	Class<? extends Player> playerClass;
	int id;
	float x, y, xSpeed, ySpeed;
	boolean left, right;
	
	public enum PlayerActionType {
		LAUNCH_PROJECTILE,
		CONNECTED_PLAYER
	}
	
	/**
	 * A projectile being launched.
	 * 
	 * @param frame The frame this action should happen.
	 * @param projectileAngle
	 */
	public QueuedPlayerAction(long frame, Player player, float projectileAngle) {
		this.frame = frame;
		this.player = player;
		this.projectileAngle = projectileAngle;
		
		actionType = PlayerActionType.LAUNCH_PROJECTILE;
	}
	
	/**
	 * A player has connected.
	 * 
	 * @param frame The frame this action should happen.
	 * @param playerClass
	 * @param id
	 * @param x
	 * @param y
	 * @param xSpeed
	 * @param ySpeed
	 * @param left
	 * @param right
	 */
	public QueuedPlayerAction(long frame, Class<? extends Player> playerClass, int id, float x, float y, float xSpeed, float ySpeed, boolean left, boolean right) {
		this.frame = frame;
		this.playerClass = playerClass;
		this.id = id;
		this.x = x;
		this.y = y;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.left = left;
		this.right = right;
		
		actionType = PlayerActionType.CONNECTED_PLAYER;
	}
	
	/**
	 * Execute this queued event.
	 */
	public void execute(Level level) {
		//execute differently based on action type
		switch (actionType) {
		case LAUNCH_PROJECTILE:
			level.launchProjectileAtFrame(level.frame, player, projectileAngle);
			break;
		case CONNECTED_PLAYER:
			level.connectPlayerAtFrame(level.frame, playerClass, id, x, y, xSpeed, ySpeed, left, right);
		}
	}
		
}
