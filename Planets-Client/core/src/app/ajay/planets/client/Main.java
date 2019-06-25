package app.ajay.planets.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import app.ajay.planets.base.QueuedServerMessageAction;
import app.ajay.planets.client.networking.ClientMessageReceiver;
import app.ajay.planets.client.networking.WebSocketClientMessenger;

public class Main extends ApplicationAdapter implements ClientMessageReceiver {
	
	/** The messenger to communicate with the server */
	WebSocketClientMessenger messenger;
	
	//variables used for drawing
	SpriteBatch spriteBatch;
	ShapeRenderer shapeRenderer;
	
	/**
	 * Used when calculating frame deltaTime
	 * It is calculated manually to get more control over it
	 */
	long lastTime = -1;
	
	Camera camera;
	
	ClientLevel level;
	
	//starting positions of all players
	float playerStartX = 0;
	float playerStartY = 400;
	
	@Override
	public void create() {
		//connect to server
		messenger = new WebSocketClientMessenger("localhost", 2492, Gdx.app.getType(), this);
		
		level = new ClientLevel();
		level.setClientPlayer(new ClientControlledPlayer(playerStartX, playerStartY));
		
		//add a default planet
		level.planets.add(new ClientPlanet(0, 0, 300));
		
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		//setup camera
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		//setup last frame time
		lastTime = System.nanoTime();
	}

	public void update() { 
		float actualFrameDeltaTime = (System.nanoTime() - lastTime) / 1000000000f;
		
		//frames needed to be done this frame, can only do frames at the rate of level.deltaTime
		int framesNeeded = (int) (actualFrameDeltaTime / level.deltaTime);
		
		//update this many times
		for (int i = 0; i < framesNeeded; i++) {
			level.update(false);
			
			//add back how much time has passed
			lastTime += 1000000000 / level.physicsFrameRate;
		}
	}
	
	public void draw() {
		//clear screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update camera and renderers
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		//central marker to not get lost
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.circle(0, 0, 3);
		shapeRenderer.end();
		
		//render level
		level.render(this, spriteBatch, shapeRenderer);
		
	}
	
	@Override
	public void render () {
		//update and then render
		update();
		
		draw();
	}
	
	@Override
	public void resize(int width, int height) {
		//resize the camera's viewpoint to prevent scaling
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		
		camera.update();
	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void onMessageRecieved(String message) {
		level.queuedServerMessageActions.add(new QueuedServerMessageAction(ClientPlayer.class, message));
	}

	@Override
	public void onConnect(long time) {
		
	}
}
