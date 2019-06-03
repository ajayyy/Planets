package app.ajay.planets.base;

import java.awt.Panel;
import java.util.ArrayList;
import java.util.List;

public class PhysicsObject extends WorldObject {
	
	public float xSpeed, ySpeed, radius;
	
	public boolean gravity = true;
	
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
			
			//collision code
			
			//what the position will become this frame, this is to do collision detection before actually moving the player
			float newX = x + xSpeed * timeStep;
			float newY = y + ySpeed * timeStep;
			
			//check if there is a collision with this planet
			for (Planet planet: closePlanetsList) {
				float distanceFromPlanet = (float) Math.sqrt(Math.pow(newX - planet.x, 2) + Math.pow(newY - planet.y, 2));
				if (distanceFromPlanet < planet.radius + radius) {
					//collision with this planet
					
					//move back to be right on the object
					
					//find angle from the object
					float angle = (float) Math.atan2(newY - planet.y, newX - planet.x);
					
					//find where the position should be
					x = (float) (Math.cos(angle) * (planet.radius + radius)) + planet.x;
					y = (float) (Math.sin(angle) * (planet.radius + radius)) + planet.y;
					
					//the extra wasted timestep has to be ignored, since all time steps should always be the same length
					
					//bounce
					xSpeed = (float) (Math.cos(angle) * bounceVelocityConstant);
					ySpeed = (float) (Math.sin(angle) * bounceVelocityConstant);
					velocityHandled = true;
				}
			}
		}
		
		if (!velocityHandled) {
			//change the position based on the velocities
			x += xSpeed * timeStep;
			y += ySpeed * timeStep;
		}
	}
}
