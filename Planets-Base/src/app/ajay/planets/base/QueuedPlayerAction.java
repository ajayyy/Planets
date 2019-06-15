package app.ajay.planets.base;

public class QueuedPlayerAction {
	
	public long frame;
	public PlayerActionType actionType;
	
	/** The player this action is referring to */
	public Player player;
	
	//for projectile launch
	public float projectileAngle;
	
	public enum PlayerActionType {
		LAUNCH_PROJECTILE
	}
	
	/**
	 * A projectile being launched
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
	 * Execute this queued event.
	 */
	public void execute() {
		//execute differently based on action type
		switch (actionType) {
		case LAUNCH_PROJECTILE:
			launchProjectile();
			break;
		}
	}
		
	public void launchProjectile() {
		//launch the projectile at this frame
		player.projectileLaunched = true;
		player.projectileAngle = projectileAngle;
	}
	
}
