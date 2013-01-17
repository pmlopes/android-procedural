package com.jetdrone.and2d;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class RFighter extends Activity {
	
	private Game game;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
  
        game = new Game(getApplicationContext(), this);
        game.setFocusable(true);
        setContentView(game);
	}

	@Override
	protected void onResume() {
		super.onResume();
		game.registerListener();
	}

	@Override
	protected void onStop() {
		super.onStop();
		game.unregisterListener();
	}
}