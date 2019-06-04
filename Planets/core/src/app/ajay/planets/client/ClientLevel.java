package app.ajay.planets.client;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import app.ajay.planets.base.Level;
import app.ajay.planets.base.Planet;
import app.ajay.planets.base.Player;
import app.ajay.planets.base.Projectile;

public class ClientLevel extends Level {
	
	ClientControlledPlayer clientControlledPlayer = null;
	
	//smoother frame delta time used only for rendering things (nothing important like physics)
	float graphicsDeltaTime;
	
	public ClientLevel() {
		super();
	}
	
	Main main;
	
	public void update(Main main) {
		super.update();
		
		this.main = main;
	}
	
	public void cameraUpdates(Main main) {
		//move camera to be centered on the client player
		//linearly interpolate
		float lerp = 2.5f;
		float xMovement = ((players.get(0).x - main.camera.position.x) * lerp * graphicsDeltaTime);
		float yMovement = ((players.get(0).y - main.camera.position.y) * lerp * graphicsDeltaTime);
		
		main.camera.position.x += xMovement;
		main.camera.position.y += yMovement;
		
		main.camera.update();
	}
	
	public void render(Main main, SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
		this.main = main;
		
		//setup graphics delta time
		graphicsDeltaTime = Gdx.graphics.getDeltaTime();
		if (graphicsDeltaTime > 1/20f) {
			graphicsDeltaTime = 1/20f;
		}
		
		//update camera
		cameraUpdates(main);
		
		//render all planets
		for (Planet planet : planets) {
			((ClientPlanet) planet).render(this, spriteBatch, shapeRenderer);
		}
		
		//render all projectiles
		for (Projectile projectile : new ArrayList<Projectile>(projectiles)) {
			((ClientProjectile) projectile).render(this, spriteBatch, shapeRenderer);
		}
		
		//render all players
		for (Player player : players) {
			((ClientPlayer) player).render(this, spriteBatch, shapeRenderer);
		}
	}
	
	public void setClientPlayer(ClientControlledPlayer clientControlledPlayer) {
		if (clientControlledPlayer != null) {
			//remove that one, it's done now
			players.remove(this.clientControlledPlayer);
		}
		
		this.clientControlledPlayer = clientControlledPlayer;
		
		players.add(0, clientControlledPlayer);
	}
}
