package app.ajay.planets.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import app.ajay.planets.base.Level;
import app.ajay.planets.base.Player;

public class ClientPlayer extends Player {
	
	public ClientPlayer(int id, float x, float y, float xSpeed, float ySpeed, boolean left, boolean right) {
		super(id, x, y);
		
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.left = left;
		this.right = right;
		
		projectileClass = ClientProjectile.class;
	}
	
	public void render(Level level, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.circle(x, y, radius);
		
		shapeRenderer.end();
	}
	
	public void update(Level level) {
		super.update(level);
		
	}
}
