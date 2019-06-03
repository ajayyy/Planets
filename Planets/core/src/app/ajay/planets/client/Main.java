package app.ajay.planets.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Main extends ApplicationAdapter {
	
	//variables used for drawing
	SpriteBatch spriteBatch;
	ShapeRenderer shapeRenderer;
	
	Camera camera;
	
	ClientLevel level;
	
	@Override
	public void create () {
		level = new ClientLevel();
		level.setClientPlayer(new ClientControlledPlayer(0, 400));
		
		//add a default planet
		level.planets.add(new ClientPlanet(0, 0, 300));
		
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		//setup camera
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
	}

	public void update() { 
		level.deltaTime = Gdx.graphics.getRawDeltaTime();
		level.update();
		
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
}
