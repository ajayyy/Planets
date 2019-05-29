package app.ajay.planets.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import app.ajay.planets.base.Level;

public class Main extends ApplicationAdapter {
	
	//variables used for drawing
	SpriteBatch spriteBatch;
	ShapeRenderer shapeRenderer;
	
	Camera camera;
	
	Level level;
	
	@Override
	public void create () {
		level = new Level();
		level.players.add(new ClientPlayer());
		
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		//setup camera
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
	}

	public void update() { 
		level.deltaTime = Gdx.graphics.getDeltaTime();
		level.update();
		
		//move camera to be centered on the client player
		//linearly interpolate
		float lerp = 2.5f;
		float xMovement = ((level.players.get(0).x - camera.position.x) * lerp * level.deltaTime);
		float yMovement = ((level.players.get(0).y - camera.position.y) * lerp * level.deltaTime);
		
		camera.position.x += xMovement;
		camera.position.y += yMovement;
		
		camera.update();
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
		
		((ClientPlayer) level.players.get(0)).render(level, spriteBatch, shapeRenderer);
	}
	
	@Override
	public void render () {
		
		//update and then render
		update();
		
		draw();
		
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
		shapeRenderer.dispose();
	}
}
