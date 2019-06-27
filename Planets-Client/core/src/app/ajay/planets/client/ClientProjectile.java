package app.ajay.planets.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import app.ajay.planets.base.Level;
import app.ajay.planets.base.Projectile;

public class ClientProjectile extends Projectile {
	
	public ClientProjectile(float x, float y, float xLaunchProjectile, float yLaunchProjectile) {
		super(x, y, xLaunchProjectile, yLaunchProjectile);
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
