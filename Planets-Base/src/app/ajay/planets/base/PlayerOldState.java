package app.ajay.planets.base;

/**
 * A container class containing all the necessary information
 * to go back in time to a certain frame for one player
 */
public class PlayerOldState {

	float x, y, xSpeed, ySpeed;
	
	boolean left, right, alive;
	
	/** projectileLaunched is if  projectile has been launched this frame */
	boolean projectileLaunched;
	float projectileAngle;
	
	/** what frame is this state from */
	long frame;
	
	public PlayerOldState(long frame, float x, float y, float xSpeed, float ySpeed, boolean left, boolean right, 
			boolean alive, boolean projectileLaunched, float projectileAngle) {
		this.frame = frame;
		this.x = x;
		this.y = y;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.left = left;
		this.right = right;
		
		this.alive = alive;
		
		if (projectileLaunched) {
			this.projectileLaunched = projectileLaunched;
			this.projectileAngle = projectileAngle;
		}
	}
	
	public PlayerOldState(long frame, float x, float y, float xSpeed, float ySpeed, boolean left, boolean right,
			boolean projectileLaunched, float projectileAngle) {
		this(frame, x, y, xSpeed, ySpeed, left, right, true, projectileLaunched, projectileAngle);
	}
	
	public PlayerOldState(long frame, float x, float y, float xSpeed, float ySpeed, boolean left, boolean right, 
			float projectileAngle) {
		this(frame, x, y, xSpeed, ySpeed, left, right, true, projectileAngle);
	}
	
	public PlayerOldState(long frame, float x, float y, float xSpeed, float ySpeed, boolean left, boolean right) {
		this(frame, x, y, xSpeed, ySpeed, left, right, false, -1);
	}
	
	/**
	 * Sets player to be in this current state
	 * 
	 * @param player
	 */
	public void makePlayerThisState(Player player) {
		player.x = x;
		player.y = y;
		player.xSpeed = xSpeed;
		player.ySpeed = ySpeed;
		player.left = left;
		player.right = right;
		
		player.alive = alive;
		
		if (projectileLaunched) {
			player.projectileLaunched = projectileLaunched;
			player.projectileAngle = projectileAngle;
		}
	}
	
	/**
	 * Sets player to be in this current state only for control variables.
	 * 
	 * Ex. left button, right button, shooting.
	 * 
	 * @param player
	 */
	public void makePlayerControlChangesToThisState(Player player, PlayerOldState lastFramePlayerOldState) {
		if (lastFramePlayerOldState.left != left) player.left = left;
		if (lastFramePlayerOldState.right != right) player.right = right;
		
		if (lastFramePlayerOldState.alive != alive) player.alive = alive;
		
		makePlayerProjectileToThisState(player);
	}
	
	/**
	 * Sets player to be in this current state only for projectile variables.

	 * @param player
	 */
	public void makePlayerProjectileToThisState(Player player) {
		if (projectileLaunched) {
			player.projectileLaunched = projectileLaunched;
			player.projectileAngle = projectileAngle;
		}
	}
}
