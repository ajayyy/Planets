package app.ajay.planets.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import app.ajay.planets.base.Level;

public class ClientControlledPlayer extends ClientPlayer {
	
	public ClientControlledPlayer(float x, float y) {
		super(-1, x, y, 0, 0, false, false);
	}
	
	public void update(Level level) {
		super.update(level);
		
		//client controls
		if (Gdx.input.isKeyPressed(Keys.D) && !right) {
			right = true;
			//send command to server
			((ClientLevel) level).main.messenger.sendMessage("R " + level.frame);
		} else if (!Gdx.input.isKeyPressed(Keys.D) && right) {
			right = false;
			//send command to server
			((ClientLevel) level).main.messenger.sendMessage("RD " + level.frame);
		}
		
		if (Gdx.input.isKeyPressed(Keys.A) && !left) {
			left = true;
			//send command to server
			((ClientLevel) level).main.messenger.sendMessage("L " + level.frame);
		} else if (!Gdx.input.isKeyPressed(Keys.A) && left) {
			left = false;
			//send command to server
			((ClientLevel) level).main.messenger.sendMessage("LD " + level.frame);
		}
		
		//launch projectile
		if (Gdx.input.justTouched()) {
			//launch a projectile toward the mouse
			Vector3 screenCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			
			//unproject coordinates to get world coordinates
			Vector3 worldCoords = ((ClientLevel) level).main.camera.unproject(screenCoords);
			
			//get angle that this projectile should be launched at
			float launchAngle = (float) (Math.atan2(y - worldCoords.y, x - worldCoords.x) - Math.PI);
			
			//queue up a projectile launch
			projectileLaunched = true;
			projectileAngle = launchAngle;
			
			//send command to server
			((ClientLevel) level).main.messenger.sendMessage("S " + level.frame + " " + launchAngle);
		}
	}
	
	public void render(Level level, SpriteBatch batch, ShapeRenderer shapeRenderer) {
		super.render(level, batch, shapeRenderer);
	}
}
