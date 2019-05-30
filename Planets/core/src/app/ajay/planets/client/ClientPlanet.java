package app.ajay.planets.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import app.ajay.planets.base.Level;
import app.ajay.planets.base.Planet;

public class ClientPlanet extends Planet {
	
	public ClientPlanet(float x, float y, float radius) {
		super(x, y, radius);
	}
	
	public void update(Level level) {
		super.update(level);
	}
	
	public void render(Level level, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Filled);
		System.out.println(radius);
		
		shapeRenderer.circle(x, y, radius);
		
		shapeRenderer.end();
	}

}
