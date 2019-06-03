package app.ajay.planets.base;

import java.util.ArrayList;
import java.util.List;

public class Player extends PhysicsObject {
	
	public boolean right, left;
	
	public boolean alive;
	
	public float movementSpeed = 400;
	
	public Player(float x, float y) {
		super();
		
		this.x = x;
		this.y = y;
		
		radius = 25;
	}
	
	public void update(Level level) {
		super.update(level);
		
		if (right || left) {
			//moving
			
			//find nearest planet
			//this planet will be used to move against
			Planet closestPlanet = null;
			for (Planet planet: level.planets) {
				//if less than 1000 units away
				if (closestPlanet == null || Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)) > closestPlanet.radius) {
					closestPlanet = planet;
				}
			}
			
			//find angle from the object
			float planetAngle = (float) Math.atan2(y - closestPlanet.y, x - closestPlanet.x);
			
			//change speeds based on movement
			if (right) {
				float movementAngle = (float) (planetAngle - Math.PI / 2);
				
				xSpeed += Math.cos(movementAngle) * movementSpeed * level.deltaTime;
				ySpeed += Math.sin(movementAngle) * movementSpeed * level.deltaTime;
			} else if (left) {
				float movementAngle = (float) (planetAngle + Math.PI / 2);
				
				xSpeed += Math.cos(movementAngle) * movementSpeed * level.deltaTime;
				ySpeed += Math.sin(movementAngle) * movementSpeed * level.deltaTime;
			}
		}
		
		
	}
	
}
