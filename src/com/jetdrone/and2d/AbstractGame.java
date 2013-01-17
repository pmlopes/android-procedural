package com.jetdrone.and2d;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class AbstractGame extends SurfaceView implements Runnable, SurfaceHolder.Callback {
	
    /**
     * Reset game variables in preparation for a new game.
     */
	abstract void init();
	
	/**
	 * Update Game State
	 */
	abstract void updateState();
	
	/**
	 * Called when the screen dimensions change
	 * 
	 * @param width
	 * @param height
	 */
	abstract void updateDimensions(int width, int height);

    /**
     * Called from the game loop to render every frame to the canvas
     */
	abstract void updateCanvas(Canvas canvas);
	
	/**
	 * Called when the game is over
	 */
	abstract void finish();

	
	// App main thread
	private final Thread gameLoopThread;
	
	private final SurfaceHolder surfaceHolder;
	
	private final int MILLIS_PER_FRAME;
	private final int MAX_FRAME_SKIP;
	
	private boolean run = false;
	
	public AbstractGame(final Context context, final int fps) {
		super(context);
		MILLIS_PER_FRAME = 1000 / fps;
		MAX_FRAME_SKIP = MILLIS_PER_FRAME / 5;
		surfaceHolder = getHolder();
		gameLoopThread = new Thread(this);
		surfaceHolder.addCallback(this);
	}
	
	@Override
	public final void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		updateDimensions(width, height);
	}

	@Override
	public final void surfaceCreated(SurfaceHolder holder) {
		run = true;
		gameLoopThread.start();
	}

	@Override
	public final void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		run = false;
		
		while (retry) {
			try {
				gameLoopThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
		finish();
	}
	
	@Override
	public final void run() {
		Canvas c = null;
		long t0, tDiff, sleep;
		int framesSkipped;
		init();

		while (run) {
			
			try {
				c = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					t0 = SystemClock.uptimeMillis();
					framesSkipped = 0;
					// State
					updateState();
					// GFX
					updateCanvas(c);
					// Sleep
					tDiff = SystemClock.uptimeMillis() - t0;
					sleep = MILLIS_PER_FRAME - tDiff;
					if(sleep > 0) {
						try {
							// save some batt.
							Thread.sleep(sleep);
						} catch(InterruptedException e) { /* ignore */ }
					}
					while (sleep < 0 && framesSkipped < MAX_FRAME_SKIP) {
						// update without render
						updateState();
						// add frame period to check if in next frame
						sleep += MILLIS_PER_FRAME;
						framesSkipped++;
					}
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
	
	@Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // get new screen dimensions.
        updateDimensions(w, h);
    }


    @Override
    public final boolean onKeyDown(int keyCode, KeyEvent event) {
        // quit application if user presses the back key.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	finish();
        }
        return true;
    }
}
