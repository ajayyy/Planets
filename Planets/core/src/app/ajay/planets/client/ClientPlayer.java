package app.ajay.planets.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import app.ajay.planets.base.Level;
import app.ajay.planets.base.Player;

public class ClientPlayer extends Player {
	
	public ClientPlayer() {
		super();
	}
	
	public void render(Level level, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.circle(x, y, 30);
		
		shapeRenderer.end();
	}
	
	public void update(Level level) {
		super.update(level);
		
		//client controls
		if (Gdx.input.isKeyPressed(Keys.D)) {
			right = true;
		} else {
			right = false;
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			left = true;
		} else {
			left = false;
		}
		
	}
}
