package app.ajay.planets.base;

import java.util.ArrayList;
import java.util.List;

public class PhysicsObject extends WorldObject {
	
	public float xSpeed, ySpeed;
	
	public boolean gravity = true;
	
	public float gravityConstant = 10000000f;
	
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
		
		if (gravity) {
			//find nearest planets
			//only the nearest planets need to affect gravity
			List<Planet> closePlanetsList = new ArrayList<>();
			for (Planet planet: level.planets) {
				//if less than 1000 units away
				if (Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)) < 1000) {
					closePlanetsList.add(planet);
				}
			}
			
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
			
			//add to velocity
			xSpeed += totalGravityAccelX * timeStep;
			ySpeed += totalGravityAccelY * timeStep;
		}
		
		//change the position based on the velocities
		x += xSpeed * timeStep;
		y += ySpeed * timeStep;
	}
}
