package app.ajay.planets.base;

public class QueuedAction {
	public long frame;
	public ActionType actionType;
	
	/** The player this action is referring to */
	public Player player;
	
	//for projectile launch
	public float projectileAngle;
	
	public enum ActionType {
		LAUNCH_PROJECTILE
	}
	
	/**
	 * A projectile being launched
	 * 
	 * @param frame The frame this action should happen.
	 * @param projectileAngle
	 */
	public QueuedAction(long frame, Player player, float projectileAngle) {
		this.frame = frame;
		this.player = player;
		this.projectileAngle = projectileAngle;
		
		actionType = ActionType.LAUNCH_PROJECTILE;
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
