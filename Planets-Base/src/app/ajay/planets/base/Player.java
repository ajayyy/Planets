package app.ajay.planets.base;

import java.util.ArrayList;
import java.util.List;

public class Player extends PhysicsObject {
	
	public boolean right, left;
	
	public boolean alive;
	
	public float movementSpeed = 300;
	
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
			Planet closestPlanet = getClosestPlanet(level.planets, x, y);
			
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
