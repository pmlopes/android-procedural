package com.jetdrone.and2d;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;

import com.jetdrone.and2d.gfx.BitmapFont;
import com.jetdrone.and2d.gfx.ParticleHandler;
import com.jetdrone.and2d.gfx.Robot;
import com.jetdrone.and2d.gfx.Spaceship;
import com.jetdrone.and2d.sfx.SFXR;
import com.jetdrone.and2d.util.MiniMT;

public class Game extends AbstractGame implements SensorEventListener {

	// Util objects
	private final Activity activity;
	private final BitmapFont font;
	private static final MiniMT mt = new MiniMT();
	
    // Game objects
	private Spaceship ship;
	private Robot[][] robot;
	private ParticleHandler particleHandler;
    private SFXR sfxGenerator;
	private AudioClip[] soundBank;
	
	// SoundBank index
	private final static int SOUND_EXPLOSION = 0;

    // game states
    private final static int GAME_INIT = 0;
    private final static int GAME_LEVEL = 1;
    private final static int GAME_OVER = 2;
    private final static int GAME_COMPLETE = 3;
    // current state of the game
    private static int state = GAME_INIT;

    // screen dimensions
    private int mCanvasWidth = 0;
    private int mCanvasHeight = 0;

    // are we running in portrait mode.
    private boolean mPortrait;

    // sensor manager used to control the accelerometer sensor.
    private SensorManager mSensorManager;
    
    // accelerometer sensor values.
    private float mAccelX = 0;
    private float mAccelY = 0;

    private float mSensorBuffer = 0;
    
    public Game(Context context, Activity activity) {
    	super(context, 30);
		this.activity = activity;
		this.font = new BitmapFont(context);
		
		// setup accelerometer sensor manager.
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
    	if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        // grab the values required to respond to user movement.
	        mAccelX = event.values[1];
//	        mAccelY = event.values[1];
    	}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // currently not used
    }
    
    /**
     * Register the accelerometer sensor so we can use it in-game.
     */
    public void registerListener() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Unregister the accelerometer sensor otherwise it will continue to operate
     * and report values.
     */
    public void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }

    /**
     * Init Objects that live the whole lifecycle of the app
     */
    @Override
	public void init() {
		ship = new Spaceship();
		particleHandler = new ParticleHandler(50);
		soundBank = new AudioClip[8];
		sfxGenerator = new SFXR();
	}
	
    private short[] generateAudio(int sampleRateinHz, int length) {
    	short[] audioData = new short[(int) (1000.f / sampleRateinHz * length)];
		for (int i = 0; i < audioData.length; i++) {
			audioData[i] = (short) (sfxGenerator.synthSample() * Short.MAX_VALUE);
		}
		return audioData;
    }
	
	/**
	 * Update input
	 */
    @Override
	public void updateState() {
    	
        switch (state) {
        case GAME_INIT:
        	// create explosion sound
    		sfxGenerator.init(100);
    		sfxGenerator.random(2);
    		sfxGenerator.resetSample(false);
    		soundBank[SOUND_EXPLOSION] = new AudioClip(22050, generateAudio(22050, 500));
    		// create ship
    		ship.generate(0x3CE0EF76);
    		// create the robots
    		robot = new Robot[10][5];
    		for(int i=0; i<robot.length; i++) {
    			for(int j=0; j<robot[j].length; j++) {
    				robot[i][j] = new Robot();
    				robot[i][j].generate(mt.generate());
    			}
    		}

    		// set the starting state of the game.
    		state = GAME_LEVEL;
    		
        	break;
        case GAME_LEVEL:
            if (mAccelX > mSensorBuffer || mAccelX < -mSensorBuffer) {
                ship.updateX(-mAccelX, mCanvasWidth);
                ship.updateY(mCanvasHeight - 20, mCanvasHeight);
            }
//          if (mAccelY > mSensorBuffer || mAccelY < -mSensorBuffer)
          
//          if(!particles.isActive())
//          	particles.init(mCanvasWidth, mCanvasHeight);
//          else
//          	particles.update();
            break;
        }
	}
	
	/**
	 * Called when the screen dimensions change
	 * 
	 * @param width
	 * @param height
	 */
    @Override
	public void updateDimensions(int width, int height) {
	    mCanvasWidth = width;
	    mCanvasHeight = height;
	}
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	if(event.getAction() == MotionEvent.ACTION_DOWN) {
    		soundBank[SOUND_EXPLOSION].play();
    	}
    	return true;
    }

    /**
     * Called from the game loop to render every frame to the canvas
     */
    @Override
	public void updateCanvas(Canvas canvas) {
        // clear the screen.
    	canvas.drawColor(Color.BLACK);

        // simple state machine, draw screen depending on the current state.
        switch (state) {
        case GAME_INIT:
        case GAME_LEVEL:
    		for(int i=0; i<robot.length; i++) {
    			for(int j=0; j<robot[j].length; j++) {
    				if(robot[i][j] != null) {
	    				robot[i][j].draw(canvas, i * 24, j * 24);
    				}
    			}
    		}
    		font.print(canvas, 10, 150, "Test 123");
//        	particles.draw(canvas);
        	canvas.save();
        	ship.draw(canvas);
        	canvas.restore();
            // draw hud
            //drawHUD();
            break;

        case GAME_OVER:
            //drawGameOver();
            break;

        case GAME_COMPLETE:
            //drawGameComplete();
            break;
        }
	}
	
	/**
	 * Called when the game is over
	 */
    @Override
	public void finish() {
    	soundBank[SOUND_EXPLOSION].release();
        unregisterListener();
        activity.finish();
	}
}
