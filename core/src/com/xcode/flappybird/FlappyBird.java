package com.xcode.flappybird;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Random;

public class FlappyBird implements Screen {

	//Implements Screen
	private Stage stage;
	private Game game;
    private Music backgroundMusic;
	private SpriteBatch batch;
	private Texture background;
	private Texture gameOver;
	private ShapeRenderer shapeRenderer;
	private ShapeRenderer shapeRenderer2;
	private Texture[] birds;
	private Texture explosion;
	private String [] musicList = {"Callista.mp3", "slipthru.mp3", "VelvetSakiKaskas.mp3"};
    private String [] backgroundX = {"background-night.png", "BackGround1.png", "flappyBackground.png", "nightPic.png"};
	private Texture topTube;
	private Texture base;
	private Texture bottomTube;
	private float gap = 400;
	private Circle birdCircle;
	private Rectangle[] topTubeRectanbles;
	private Rectangle[] bottomTubeRectangles;
	private int flapState = 0;
	private int score = 0;
	private int scoringTube = 0;

	private BitmapFont font;
	private float birdY = 0;
	private float velocity = 0;
    private Random random = new Random();
	private int gameState = 0;
	private float gravity = 2;
	private float maxTubeOffSet = 0;
	private Random randomGenerator;
	private float tubeVelocity = 4;
	private int numberOfTubes = 4;
	private float[] tubeX = new float[numberOfTubes];
	private float[] tubeOffSet = new float[numberOfTubes];
	private float distanceBetweenTubes;


	public FlappyBird(Game aGame) {

		game = aGame;
		stage = new Stage(new ScreenViewport());
		batch = new SpriteBatch();
		// randomly selects an index from the arr
		int backgroundSelect = random.nextInt(backgroundX.length);

		background = new Texture(backgroundX[backgroundSelect]);
		gameOver = new Texture("GameOver3.png");
		base = new Texture("base.png");
		birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		shapeRenderer = new ShapeRenderer();
		shapeRenderer2 = new ShapeRenderer();

		birds = new Texture[4];
		birds[0] = new Texture("Helicopter1.png");
		birds[1] = new Texture("Helicopter2.png");
		birds[2] = new Texture("Helicopter3.png");
		birds[3] = new Texture("Helicopter4.png");
		explosion = new Texture("Explosion.png");
		topTube = new Texture("UFOA.gif");
		bottomTube = new Texture("BuildingC.png");

		maxTubeOffSet = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
		topTubeRectanbles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		playMusic();
		startRestartLogic();
	}


	@Override
	public void show() {}

	@Override
	public void render(float delta) {

		//System.out.println("RENDER METHOD IS LAUNCHING");
		batch.begin();
		drawBackgroud();
		switch (gameState) {
			case 1:
				gameStateOne();
				justTouched();
				tubeLogic();
				keepBirdAlive();
				break;
			case 0:
				restartOnTouch();
				break;
			case 2:
				restartGame();
				break;
		}
		animateBird();
		font.draw(batch, String.valueOf(score), 100, 200);
		// birdCircle.set(Gdx.graphics.getWidth()/ 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

		collisionDetection();

		batch.end();
		shapeRenderer.end();
	}


	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}

	//Render the background stages
	public void drawBackgroud() {
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	}

	public void gameStateOne() {
		if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
			score++;
			Gdx.app.log("Score", String.valueOf(score));
			if (scoringTube < numberOfTubes - 1) {
				scoringTube++;
			} else {
				scoringTube = 0;
			}
		}
	}

	public void tubeLogic() {
		for (int i = 0; i < numberOfTubes; i++) {
			if(tubeX[i] < - topTube.getWidth()) {
				tubeX[i] += numberOfTubes * distanceBetweenTubes;
				tubeOffSet[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			} else {
				tubeX[i] = tubeX[i] - tubeVelocity;
			}
			batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffSet[i]);
			batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffSet[i]);

			topTubeRectanbles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffSet[i], topTube.getWidth(), topTube.getHeight());
			bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffSet[i], bottomTube.getWidth(), bottomTube.getHeight());
		}
	}

	public void animateBird() {
		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
	}

	public void startRestartLogic() {
		birdY = Gdx.graphics.getWidth()/ 2 - birds[0].getHeight() * 3/4;

		for (int i = 0; i < numberOfTubes; i++) {

			tubeOffSet[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectanbles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

		}
	}
	/*
	* This method controls the gravity of the helicopter
 	*/
	public void justTouched() {
		if (Gdx.input.justTouched())
		{
			velocity = -20;
		}
	}

	public void keepBirdAlive() {
		if (birdY > 0) { //keep bird moving hax velocity < 0
			velocity = velocity + gravity;
			birdY -= velocity;
		} else {
			gameState = 2;
		}
	}

	public void collisionDetection() {

		birdCircle.set(Gdx.graphics.getWidth()/ 3, birdY + birds[flapState].getHeight() / 3, birds[flapState].getWidth() / 3);
		//ShapeRenderer logic for collision detection for the bird
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.CORAL);
//		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);



		for (int i = 0; i < numberOfTubes; i++) {

			/*
			/ShapRenderer logic for the top and bottom tubes
			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffSet[i], topTube.getWidth(), topTube.getHeight());
			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffSet[i], bottomTube.getWidth(), bottomTube.getHeight());
			*/
			if(Intersector.overlaps(birdCircle, topTubeRectanbles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
					Gdx.app.log("collison be happening", "yes");
					//System.out.println("COLLISION NOW");
					batch.draw(explosion, 0, 0, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				gameState = 2;
			}
		}
	}

	public void restartGame() {
		batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getWidth() / 2);

		if (Gdx.input.justTouched()) {
			gameState = 1;
			//call start restart logic
			startRestartLogic();
			score = 0;
			scoringTube = 0;
			velocity = 0;
		}
	}

	public void restartOnTouch() {
		if (Gdx.input.justTouched()) {
			gameState = 1;
		}
	}

	//Play music while the game is running
	public void playMusic() {
		int musicSelect = random.nextInt(musicList.length);
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(musicList[musicSelect]));
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
	}

}
