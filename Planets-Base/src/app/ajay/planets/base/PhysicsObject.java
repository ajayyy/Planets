package app.ajay.planets.base;

import java.awt.Panel;
import java.util.ArrayList;
import java.util.List;

public class PhysicsObject extends WorldObject {
	
	public float xSpeed, ySpeed, radius;
	
	/**
	 * Is this object affected by gravity.
	 */
	public boolean gravity = true;
	
	/**
	 * Does this object bounce off planets and other players.
	 */
	public boolean canBounce = false;
	
	/**
	 * Only used if {@code bounces == false}.
	 * True after the first collision with a planet.
	 * Used for projectiles, they destroy themselves after colliding
	 */
	boolean collided = false;
	/**
	 * Will it collide against other phsyics objects and bounce off.
	 */
	public boolean collidesWithPhysicsObjects = true;
	
	public float gravityConstant = 100000000f;
	
	public float bounceVelocityConstant = 450f;
	
	public void update(Level level) {
		super.update(level);

		physicsUpdate(level);
	}
	
	/**
	 * This works out to only run at 30 frames per second (or too the level's set frame rate)
	 * @param level The level that this {@link PhysicsObject} is in
	 */
	public void physicsUpdate(Level level) {
		
		//ideally should be fixed to make the accuracy the same on all clients
		float timeStep = level.deltaTime;
		
		//if the velocity has already been handled
		//Ex. it has been handled in the gravity function after a collision
		boolean velocityHandled = false;
		
		if (gravity) {
			//find nearest planets
			//only the nearest planets need to affect gravity
			List<Planet> closePlanetsList = getClosestPlanets(level.planets, x, y);
			
			//all planet's accelerations are added to this
			float totalGravityAccelX = 0;
			float totalGravityAccelY = 0;
			
			for (Planet planet: closePlanetsList) {
				float xDist = x - planet.x;
				float yDist = y - planet.y;
				
				//squared since this is just a midstep for the below equation to make it easier to understand
				double squaredDistanceFromPlanet = Math.pow(xDist, 2) + Math.pow(yDist, 2);
				
				//gravitate toward planets
				//using the gravity equation found here: https://www.desmos.com/calculator/6l1w4h1qjb
				//inverse square law to get the intensity, cos or sin for direction
				double accelX = -(Math.cos(Math.atan2(yDist, xDist)) * gravityConstant)/ (squaredDistanceFromPlanet);
				double accelY = -(Math.sin(Math.atan2(yDist, xDist)) * gravityConstant)/ (squaredDistanceFromPlanet);
				
				totalGravityAccelX += accelX;
				totalGravityAccelY += accelY;
			}
			
			//create the gravity velocity
			xSpeed += totalGravityAccelX * timeStep;
			ySpeed += totalGravityAccelY * timeStep;
			
			//collision code
			
			//what the position will become this frame, this is to do collision detection before actually moving the player
			float newX = x + xSpeed * timeStep;
			float newY = y + ySpeed * timeStep;
			
			//check if there is a collision with the closest planet
			Planet closestPlanet = getClosestPlanet(closePlanetsList, newX, newY);
			//if there is any planet that can possible collide
			if (closestPlanet != null) {
				float distanceFromPlanet = (float) Math.sqrt(Math.pow(newX - closestPlanet.x, 2) + Math.pow(newY - closestPlanet.y, 2));
				if (distanceFromPlanet < closestPlanet.radius + radius) {
					//collision with this planet
					
					//bounce off this planet
					if (canBounce) {
						//move back to be right on the object
						
						//find angle from the object
						float planetNormalAngle = (float) Math.atan2(newY - closestPlanet.y, newX - closestPlanet.x);
						
						//find where the position should be
						x = (float) (Math.cos(planetNormalAngle) * (closestPlanet.radius + radius)) + closestPlanet.x;
						y = (float) (Math.sin(planetNormalAngle) * (closestPlanet.radius + radius)) + closestPlanet.y;
						
						//the extra wasted timestep has to be ignored, since all time steps should always be the same length
						
						//bounce, this finds the optimal angle to bounce the object at
						//this is used to make the previous speed be taken into account
						//modified algoritm from https://stackoverflow.com/a/573206/1985387
						
						//project the current speed onto the normal of the planet
						double xSpeedOnNormal = 2 * (MathHelper.getDotProduct(xSpeed, ySpeed, Math.cos(planetNormalAngle), Math.sin(planetNormalAngle))) * Math.cos(planetNormalAngle);
						double ySpeedOnNormal = 2 * (MathHelper.getDotProduct(xSpeed, ySpeed, Math.cos(planetNormalAngle), Math.sin(planetNormalAngle))) * Math.sin(planetNormalAngle);
						
						//the direction that it should bounce at in components
						//calculated by removing the current speed projected onto the normal from the current speed
						//whatever speed is extra
						//it doesn't bounce directly up from the normal, but takes the current speed into account
						double bounceDirectionX = xSpeed - xSpeedOnNormal;
						double bounceDirectionY = ySpeed - ySpeedOnNormal;
						
						//the angle from that direction
						double bounceAngle = Math.atan2(bounceDirectionY, bounceDirectionX);
						//multiply that direction by the scale
						xSpeed = (float) (Math.cos(bounceAngle) * bounceVelocityConstant);
						ySpeed = (float) (Math.sin(bounceAngle) * bounceVelocityConstant);
						
						velocityHandled = true;
					} else {
						//remember that a collision has happened if this object cannot bounce
						collided = true;
					}
				}
			}
		}
		
		physicsObjectCollisions(level);
		
		if (!velocityHandled) {
			//change the position based on the velocities
			x += xSpeed * timeStep;
			y += ySpeed * timeStep;
		}
	}
	
	/**
	 * Goes through all other physics objects and tries to determine if this physics object is colliding with another.
	 * It will then bounce off that physics object
	 * 
	 * @param level
	 */
	public void physicsObjectCollisions(Level level) {
		
		//All physics objects to check against
		List<PhysicsObject> physicsObjects = new ArrayList<PhysicsObject>();
		physicsObjects.addAll(level.players);
		physicsObjects.addAll(level.projectiles);
		
		for (PhysicsObject physicsObject: physicsObjects) {
			if (MathHelper.isColliding(x, y, physicsObject.x, physicsObject.y, radius, physicsObject.radius)) {
				//bounce off this object
				
				//swap velocities
				float oldXSpeed = xSpeed;
				float oldYSpeed = ySpeed;
				
				xSpeed = physicsObject.xSpeed;
				ySpeed = physicsObject.ySpeed;
				
				physicsObject.xSpeed = oldXSpeed;
				physicsObject.ySpeed = oldYSpeed;
			}
		}
	}
	
	public List<Planet> getClosestPlanets(List<Planet> planets, float x, float y) {
		//find nearest planets
		List<Planet> closePlanetsList = new ArrayList<>();
		for (Planet planet: planets) {
			//if less than 2000 units away
			if (Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)) < 2000) {
				closePlanetsList.add(planet);
			}
		}
		
		return closePlanetsList;
	}
	
	public Planet getClosestPlanet(List<Planet> planets, float x, float y) {
		//find nearest planet
		Planet closestPlanet = null;
		for (Planet planet: planets) {
			if (closestPlanet == null || Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)) > closestPlanet.radius) {
				closestPlanet = planet;
			}
		}
		
		return closestPlanet;
	}
}
