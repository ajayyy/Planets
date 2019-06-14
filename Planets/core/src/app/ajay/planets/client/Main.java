package app.ajay.planets.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import app.ajay.planets.base.Level;
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
	
	/**
	 * The list of commands that could be sent from the server
	 * 
	 * Player connected, player disconnected, player shot, left, right, left disabled, right disabled
	 */
	String[] commands = {"PC", "PD", "S", "L", "R", "LD", "RD"};
	
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
			level.update();
			
			//add back how much time has passed
			lastTime += 1000000000 / level.physicsFrameRate;

			//one frame has just occurred
			level.frame++;
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
		
		System.out.println(width + " " + height);
		
		camera.update();
	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void onMessageRecieved(String message) {
		//first item is the command itself
		String[] argumentStrings = new String[0];
		
		//what command has been sent
		int command = -1;
		for (int i = 0; i < commands.length; i++) {
			if (message.startsWith(commands[i])) {
				command = i;
				
				argumentStrings = message.split(" ");
				break;
			}
		}
		
		if (command == -1) {
			System.err.println("Server sent unrecongnised command: " + message);
		}
		
		//handle the command
		switch (command) {
		case 0:
			//player connected
			level.players.add(new ClientPlayer(Integer.parseInt(argumentStrings[1]), Float.parseFloat(argumentStrings[2]), 
					Float.parseFloat(argumentStrings[3]), Float.parseFloat(argumentStrings[4]), Float.parseFloat(argumentStrings[5]), 
					Boolean.parseBoolean(argumentStrings[6]), Boolean.parseBoolean(argumentStrings[7])));
			break;
		case 1:
			//player disconnected
			//remove the player under this ID
			int id = Integer.parseInt(argumentStrings[1]);
			for (int i = 0; i < level.players.size(); i++) {
				if (level.players.get(i).id == id) {
					level.players.remove(i);
					break;
				}
			}
			break;
		case 2:
			//left pressed
			level.getPlayerById(Integer.parseInt(argumentStrings[1])).left = true;
			break;
		case 3:
			//right pressed
			level.getPlayerById(Integer.parseInt(argumentStrings[1])).right = true;
			break;
		case 4:
			//left unpressed
			level.getPlayerById(Integer.parseInt(argumentStrings[1])).left = false;
			break;
		case 5:
			//right unpressed
			level.getPlayerById(Integer.parseInt(argumentStrings[1])).right = false;
			break;
		}
	}

	@Override
	public void onConnect(long time) {
		
	}
}
