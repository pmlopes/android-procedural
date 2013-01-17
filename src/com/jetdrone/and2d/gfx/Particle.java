package com.jetdrone.and2d.gfx;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Particle {
	
	private boolean active;
	private int lifetime, current_lifetime;
	private float fade_speed;
	private int px, py;
	private float vx, vy;

	private int red_factor, green_factor, blue_factor;
	private float rfact, gfact, bfact;
	
	private final Random rand;
	private final Paint paint;
	
	public Particle() {
		paint = new Paint();
		paint.setStyle(Style.FILL);
		rand = new Random();
	}

	public void init(int lifetime, float fade_speed, float vx, float vy) {
		this.active = true;
		this.lifetime = lifetime;
		this.current_lifetime = lifetime;
		this.fade_speed = fade_speed;
		this.px = 0;
		this.py = 0;
		this.vx = vx;
		this.vy = vy;

		red_factor = Math.abs(rand.nextInt(255));
		green_factor = Math.abs(rand.nextInt(255));
		blue_factor = Math.abs(rand.nextInt(255));
		paint.setARGB(255, red_factor, green_factor, blue_factor);
	}

	public void update() {
		if (!active)
			return;

		rfact = red_factor * ((float) current_lifetime / (float) lifetime);
		gfact = green_factor * ((float) current_lifetime / (float) lifetime);
		bfact = blue_factor * ((float) current_lifetime / (float) lifetime);

		current_lifetime -= fade_speed;
		if (current_lifetime < 0)
			active = false;

		paint.setARGB(255, (int) rfact, (int) gfact, (int) bfact);

		px += vx;
		py += vy;
	}

	public void draw(Canvas canvas, int particleSize) {
		if (active) {
			canvas.drawRect(px, py, px + particleSize, py + particleSize, paint);
		}
	}

	public boolean isActive() {
		return active;
	}

	public void addVelocity(float fx, float fy) {
		vx += fx;
		vy += fy;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
}
