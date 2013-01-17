package com.jetdrone.and2d.gfx;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Robot {
	
	// these are black magic voodoo values
	private static final int cols = 7;
	private static final int rows = 11;
	// possible values for the grid array
	private static final int EMPTY = 0; // aka "WHITE SPACE"
	private static final int AVOID = 1; // aka "GUTS" or "INSIDES"
	private static final int SOLID = 2; // aka "SKIN" or "OUTLINE"
	// the grid; only 4 columns of storage are really needed due to
	// symmetry, but the full 7 columns are allocated and processed,
	// which makes it easier to play with assymetrical designs etc.
	private int[][] grid;
	// colors for filling EMPTY/AVOID and SOLID areas, respectively
	private Paint bgPaint, fgPaint;
	// scaling values (in pixel units)
	private int xscale, yscale;
	// margins (in pixel units)
	private int xmargin, ymargin;

	public Robot() {
		grid = new int[rows][cols];
		
		bgPaint = new Paint();
		bgPaint.setStyle(Style.FILL);
		bgPaint.setARGB(255, 255, 255, 255);
		
		fgPaint = new Paint();
		fgPaint.setStyle(Style.FILL);
		fgPaint.setARGB(255, 0, 0, 0);
		
		xscale = yscale = 2;
		xmargin = ymargin = 1;
	}

	//
	int getHeight() {
		return rows * yscale + ymargin;
	}

	//
	int getWidth() {
		return cols * xscale + xmargin;
	}

	//
	void setMargins(int xm, int ym) {
		xmargin = xm;
		ymargin = ym;
	}

	//
	void setScales(int xs, int ys) {
		xscale = xs;
		yscale = ys;
	}

	// reset the entire grid to empty
	private void wipe() {
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				grid[r][c] = EMPTY;
	}

	// generate the robot pattern for the given seed
	public void generate(int seed) {
		wipe();
		// HEAD
		int hseed = (seed) & 0xff;
		grid[1][1] = ((hseed & 1) > 0) ? AVOID : EMPTY;
		grid[1][2] = ((hseed & 2) > 0) ? AVOID : EMPTY;
		grid[1][3] = ((hseed & 4) > 0) ? AVOID : EMPTY;
		grid[2][1] = ((hseed & 8) > 0) ? AVOID : EMPTY;
		grid[2][2] = ((hseed & 16) > 0) ? AVOID : EMPTY;
		grid[2][3] = ((hseed & 32) > 0) ? AVOID : EMPTY;
		grid[3][2] = ((hseed & 64) > 0) ? AVOID : EMPTY;
		grid[3][3] = ((hseed & 128) > 0) ? AVOID : EMPTY;
		// BODY
		int bseed = (seed >> 8) & 0xff;
		grid[4][3] = ((bseed & 1) > 0) ? AVOID : EMPTY;
		grid[5][1] = ((bseed & 2) > 0) ? AVOID : EMPTY;
		grid[5][2] = ((bseed & 4) > 0) ? AVOID : EMPTY;
		grid[5][3] = ((bseed & 8) > 0) ? AVOID : EMPTY;
		grid[6][1] = ((bseed & 16) > 0) ? AVOID : EMPTY;
		grid[6][2] = ((bseed & 32) > 0) ? AVOID : EMPTY;
		grid[6][3] = ((bseed & 64) > 0) ? AVOID : EMPTY;
		grid[7][3] = ((bseed & 128) > 0) ? AVOID : EMPTY;
		// FEET
		int fseed = (seed >> 16) & 0xff;
		grid[8][3] = ((fseed & 1) > 0) ? AVOID : EMPTY;
		grid[9][1] = ((fseed & 2) > 0) ? AVOID : EMPTY;
		grid[9][2] = ((fseed & 4) > 0) ? AVOID : EMPTY;
		grid[9][3] = ((fseed & 8) > 0) ? AVOID : EMPTY;
		grid[10][0] = ((fseed & 16) > 0) ? AVOID : EMPTY;
		grid[10][1] = ((fseed & 32) > 0) ? AVOID : EMPTY;
		grid[10][2] = ((fseed & 64) > 0) ? AVOID : EMPTY;
		grid[10][3] = ((fseed & 128) > 0) ? AVOID : EMPTY;
		// edge detector
		// wrap the AVOIDs with SOLIDs where EMPTY
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c <= cols / 2; c++) {
				int here = grid[r][c];
				if (here != EMPTY)
					continue;
				boolean needsolid = false;
				if ((c > 0) && (grid[r][c - 1] == AVOID))
					needsolid = true;
				if ((c < cols - 1) && (grid[r][c + 1] == AVOID))
					needsolid = true;
				if ((r > 0) && (grid[r - 1][c] == AVOID))
					needsolid = true;
				if ((r < rows - 1) && (grid[r + 1][c] == AVOID))
					needsolid = true;
				if (needsolid)
					grid[r][c] = SOLID;
			}
		}
		// mirror left side into right side, force symmetry
		for (int r = 0; r < rows; r++) {
			grid[r][4] = grid[r][2];
			grid[r][5] = grid[r][1];
			grid[r][6] = grid[r][0];
		}
	}

	// draw the robot at given coordinates
	public void draw(Canvas canvas, int basex, int basey) {
		for (int r = 0; r < rows; r++) {
			int y1 = basey + ymargin / 2 + r * yscale;
			for (int c = 0; c < cols; c++) {
				int x1 = basex + xmargin / 2 + c * xscale;
				int m = grid[r][c];
				switch (m) {
				case EMPTY:
				case AVOID:
					canvas.drawRect(x1, y1, x1 + xscale, y1 + yscale, bgPaint);
					break;
				case SOLID:
					canvas.drawRect(x1, y1, x1 + xscale, y1 + yscale, fgPaint);
					break;
				}
			}
		}
	}
}
