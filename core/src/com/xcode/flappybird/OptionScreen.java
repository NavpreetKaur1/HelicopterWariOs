package com.xcode.flappybird;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;




public class OptionScreen implements Screen {

    private Stage stage;
    private Game game;
    private int highScore = 10;
    private String message = "";
    private float highestY = 0.0f;
    int row_height = Gdx.graphics.getWidth() / 50;
    int Help_Guides = 2;

    Label title = new Label(message, MyGdxGame.gameSkin,"small-black");

    public OptionScreen(Game aGame) {
        game = aGame;
        stage = new Stage(new ScreenViewport());


        // sensors button
        final TextButton sensorButton = new TextButton("Get Sensors",MyGdxGame.gameSkin);
        sensorButton.setWidth(Gdx.graphics.getWidth());
        sensorButton.setPosition(Gdx.graphics.getWidth()/2-sensorButton.getWidth()/2,Gdx.graphics.getHeight()/2-sensorButton.getHeight()/2);
        stage.addActor(sensorButton);
        sensorButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                message = "";
                title.addAction(Actions.removeActor());
                int w = Gdx.graphics.getWidth();
                int h = Gdx.graphics.getHeight();

                int deviceAngle = Gdx.input.getRotation();
                Input.Orientation orientation = Gdx.input.getNativeOrientation();
                float accelY = Gdx.input.getAccelerometerY();

                if(accelY > highestY)
                    highestY = accelY;

                message = "Device rotated to:" + Integer.toString(deviceAngle) + " degrees\n";
                message += "Device orientation is ";

                switch(orientation){
                    case Landscape:
                        message += " landscape.\n";
                        break;

                    case Portrait:
                        message += " portrait. \n";
                        break;

                    default:
                        message += " error!\n";
                        break;
                }

                message += "Device Resolution: " + Integer.toString(w) + "," + Integer.toString(h) + "\n";
                message += "Y axis accel: " + Float.toString(accelY) + " \n";
                message += "Highest Y value: " + Float.toString(highestY) + " \n";

                if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Vibrator)){
                    if(accelY > 7){
                        Gdx.input.vibrate(100);
                    }
                }

                if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Compass)){
                    message += "Z-Rotation:" + Float.toString(Gdx.input.getAzimuth()) + "\n";
                    message += "X-Rotation:" + Float.toString(Gdx.input.getPitch()) + "\n";
                    message += "Y-Rotation:" + Float.toString(Gdx.input.getRoll()) + "\n";
                }
                else{
                    message += "No compass available\n";
                }

                title = new Label(message, MyGdxGame.gameSkin,"small-black");
                title.setAlignment(Align.center);
                title.setY(Gdx.graphics.getHeight()*2/3);
                title.setWidth(Gdx.graphics.getWidth());
                title.setSize(Gdx.graphics.getWidth(),row_height);
                title.setWrap(true);
                stage.addActor(title);
//                title.addAction(Actions.removeActor());
            }
        }
        );





        // button to go back to main menu
//        System.out.println("SCORE IS !@@###### " + score);
        TextButton backButton = new TextButton("Go Back",MyGdxGame.gameSkin);
        backButton.setWidth(Gdx.graphics.getWidth()/2);
        backButton.setPosition(Gdx.graphics.getWidth()/2-backButton.getWidth()/2,Gdx.graphics.getHeight()/6-backButton.getHeight()/2);
        backButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new com.xcode.flappybird.TitleScreen(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(backButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

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
    public void dispose() {
        stage.dispose();
    }


}
