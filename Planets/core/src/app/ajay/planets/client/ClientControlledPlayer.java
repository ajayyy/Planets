package app.ajay.planets.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import app.ajay.planets.base.Level;

public class ClientControlledPlayer extends ClientPlayer {
	
	public ClientControlledPlayer() {
		super();
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
	
	public void render(Level level, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		super.render(level, batch, shapeRenderer);
	}
}
