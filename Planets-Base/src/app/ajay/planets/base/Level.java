package app.ajay.planets.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Level {
	public ArrayList<Planet> planets = new ArrayList<>();
	
	public ArrayList<Player> players = new ArrayList<>();
	
	public ArrayList<Projectile> projectiles = new ArrayList<>();
	
	/**
	 * Set to 30 since it has to be the same on all clients
	 * This means 30 frames per second
	 */
	public int physicsFrameRate = 60;
	
	/**
	 * The amount of time the last frame took. Used to make all movement smooth towards the frame rate.
	 */
	public float deltaTime = 1f/physicsFrameRate;
	
	/** the update frame this program is currently on */
	public long frame = 0;
	
	/** 
	 * The list of all the queued actions.
	 * Used if the server sends an event that happens in the future.
	 */
	List<QueuedPlayerAction> queuedActions = new ArrayList<QueuedPlayerAction>();
	
	/**
	 * List that contains all the queued message actions.
	 * These are messages that have been received from the server or clients that have been queued to be processed.
	 * This is used to make sure that all processing happens on the same frame to be able to properly resimulate and deal with frames.
	 */
	public List<QueuedServerMessageAction> queuedServerMessageActions = new ArrayList<QueuedServerMessageAction>();
	
	/**
	 * Update this world.
	 * 
	 * @param simulation If this is a simulated update. If it's simulated, then the queued server messages can be ignored temporarily.
	 */
	public void update(boolean simulation) {
		//check for any queued server messages that need to be processed
		if (!simulation && queuedServerMessageActions.size() > 0) {
			for (QueuedServerMessageAction queuedServerMessageAction: new ArrayList<>(queuedServerMessageActions)) {
				queuedServerMessageAction.execute(this);
			}
			
			//empty the queue
			queuedServerMessageActions.removeAll(queuedServerMessageActions);
		}
		
		//check for any queued actions and trigger them if necessary
		for (QueuedPlayerAction queuedAction: new ArrayList<>(queuedActions)) {
			if (queuedAction.frame == frame) {
				//trigger this action
				queuedAction.execute(this);
			}
		}
		
		//deal with physics object collisions
		processPhysicsObjectCollisions();
		
		//update all players
		for (Player player : players) {
			player.update(this);
			player.postUpdate(this);
		}
		
		//update all planets
		for (Planet planet : planets) {
			planet.update(this);
		}
		
		//update all projectiles
		for (Projectile projectile : new ArrayList<>(projectiles)) {
			projectile.update(this);
		}
		
		//one frame has just occurred
		frame++;
	}
	
	/**
	 * Goes through all other physics objects and tries to determine if any physics object is colliding with another.
	 * It will then bounce the physics objects off of eachother.
	 * 
	 * It will not do the same collision twice.
	 */
	public void processPhysicsObjectCollisions() {
		//All physics objects to check against
		List<PhysicsObject> physicsObjects = new ArrayList<PhysicsObject>();
		physicsObjects.addAll(players);
		physicsObjects.addAll(projectiles);
		
		for (int i = 0; i < physicsObjects.size(); i++) {
			for (int s = i + 1; s < physicsObjects.size(); s++) {
				PhysicsObject physicsObject1 = physicsObjects.get(i);
				PhysicsObject physicsObject2 = physicsObjects.get(s);
				
				if (MathHelper.isColliding(physicsObject1.x, physicsObject1.y, physicsObject2.x, physicsObject2.y, physicsObject1.radius, physicsObject2.radius)) {
					//bounce these objects off of eachother
					
					//swap velocities
					float oldXSpeed = physicsObject1.xSpeed;
					float oldYSpeed = physicsObject1.ySpeed;
					
					physicsObject1.xSpeed = physicsObject2.xSpeed;
					physicsObject1.ySpeed = physicsObject2.ySpeed;
					
					physicsObject2.xSpeed = oldXSpeed;
					physicsObject2.ySpeed = oldYSpeed;
				}
			}
		}
	}
	
	/**
	 * Launches projectile as if the game were still at an old frame.
	 * Rolls back to that frame, launches the projectile, then resimulates the frames up to present
	 * 
	 * @param level
	 * @param oldFrame The frame that this event should have happened
	 * @param player
	 * @param projectileAngle
	 */
	public void launchProjectileAtFrame(long oldFrame, Player player, float projectileAngle) {
		long framesToSimulate = frame - oldFrame;
		
		if (framesToSimulate < 0) {
			//add this as a queued event instead
			queuedActions.add(new QueuedPlayerAction(oldFrame, player, projectileAngle));
			return;
		}
		
		//only simulate if necessary
		if (framesToSimulate != 0) {
			rollBackToFrame(oldFrame);
		}
		
		//launch the projectile at this frame
		player.projectileLaunched = true;
		player.projectileAngle = projectileAngle;
		
		//only simulate if necessary
		if (framesToSimulate != 0) {
			simulateFrames(framesToSimulate);
		}
	}
	
	/**
	 * Launches projectile as if the game were still at an old frame.
	 * Rolls back to that frame, launches the projectile, then resimulates the frames up to present
	 * 
	 * @param level
	 * @param oldFrame The frame that this event should have happened
	 * @param player
	 * @param projectileAngle
	 */
	public void connectPlayerAtFrame(long oldFrame, Class<? extends Player> playerClass, int id, float x, float y, float xSpeed, float ySpeed, boolean left, boolean right) {
		long framesToSimulate = frame - oldFrame;
		
		if (framesToSimulate < 0) {
			//add this as a queued event instead
			queuedActions.add(new QueuedPlayerAction(oldFrame, playerClass, id, x, y, xSpeed, ySpeed, left, right));
			return;
		}
		
		//only simulate if necessary
		if (framesToSimulate != 0) {
			rollBackToFrame(oldFrame);
		}
		
		//create the new player and add it to the list using reflection (since this class has no direct access to client or server specific classes)
		try {
			Player player = (Player) playerClass.getDeclaredConstructor(int.class, float.class, float.class, float.class, float.class, boolean.class, boolean.class)
					.newInstance(id, x, y, xSpeed, ySpeed, left, right);
							
			players.add(player);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		
		//only simulate if necessary
		if (framesToSimulate != 0) {
			simulateFrames(framesToSimulate);
		}
	}
	
	/**
	 * Rolls back the whole game to a certain frame.
	 * 
	 * @param level
	 * @param oldFrame The frame to roll back to.
	 */
	public void rollBackToFrame(long oldFrame) {
		//reset everything to this frame
		for (Player player: players) {
			//this frame's old state
			PlayerOldState fromFrameOldState = player.getOldStateAtFrame(oldFrame);
			
			fromFrameOldState.makePlayerThisState(player);
			
			player.previousPlayerOldStates = player.playerOldStates;
			player.removePlayerOldStates(oldFrame);
		}
		
		//set the level frame to the correct frame
		frame = oldFrame;
	}
	
	/**
	 * @param level
	 * @param framesToSimulate Simulates this many frames happening.
	 */
	public void simulateFrames(long framesToSimulate) {
		for (int i = 0; i < framesToSimulate; i++) {
			//check to see if old state button pressed have to be set
			for (Player player: players) {
				PlayerOldState previousFrameOldState = player.getPreviousOldStateAtFrame(frame - 1);
				PlayerOldState thisFrameOldState = player.getPreviousOldStateAtFrame(frame);
				
				//only care about the changes, since there is new input now
				if (previousFrameOldState != null && thisFrameOldState != null) {
					thisFrameOldState.makePlayerControlChangesToThisState(player, previousFrameOldState);
				} else if (thisFrameOldState != null) {
					thisFrameOldState.makePlayerProjectileToThisState(player);
				}
			}
			
			//simulate the frames
			update(true);
		}
	}
	
	public Player getPlayerById(int id) {
		for (Player player : new ArrayList<>(players)) {
			if (player.id == id) {
				return player;
			}
		}
		
		//no player was found
		return null;
	}
}
