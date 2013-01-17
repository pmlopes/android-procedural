package com.jetdrone.and2d.gfx;

import java.util.Random;

import android.graphics.Canvas;

public class ParticleHandler {

	private final Particle[] particles;
	private final Random rand = new Random();

	public ParticleHandler(int num_particles) {
		particles = new Particle[num_particles];
		for (int i = 0; i < num_particles; i++) {
			particles[i] = new Particle();
		}
	}

	public void reset() {
		for (int i = 0; i < particles.length; i++) {
			float vx = rand.nextInt(1000) / 800f;
			float vy = rand.nextInt(1000) / 800f;
			particles[i].init(5 + rand.nextInt(10), 0.3f, vx, vy);
		}
	}

	public void update() {
		for (int i = 0; i < particles.length; i++) {
			particles[i].update();
			particles[i].addVelocity(0f, 0.08f);
		}
	}

	public void draw(Canvas canvas, int particleSize) {
		for (int i = 0; i < particles.length; i++) {
			particles[i].draw(canvas, particleSize);
		}
	}

	public boolean isActive() {
		for (int i = 0; i < particles.length; i++) {
			if (particles[i].isActive())
				return true;
		}
		return false;
	}
}