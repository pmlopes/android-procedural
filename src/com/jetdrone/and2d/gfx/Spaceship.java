package com.jetdrone.and2d.gfx;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class Spaceship {
	
//	private static final MiniMT rng = new MiniMT();

	private static final int cols = 12;
	private static final int rows = 12;
	private static final int EMPTY = 0;
	private static final int AVOID = 1;
	private static final int SOLID = 2;
	private static final int COKPT = 3; // ::added to aid coloring
	
	int x, y;
	int hwidth, hheight;
	
	int seed;
	int[][] grid;
	int xscale, yscale;
	int xmargin, ymargin;
	
	Paint pSolid;
	Paint pCokpt;

	public Spaceship() {
		grid = new int[rows][cols];
		xscale = yscale = 1;
		xmargin = ymargin = 0;
		
		hwidth = rows*xscale/2;
		hheight = cols*yscale/2;
		
		pSolid = new Paint();
		pSolid.setColor(Color.WHITE);
		pSolid.setStyle(Style.FILL);

		pCokpt = new Paint();
		pCokpt.setColor(Color.LTGRAY);
		pCokpt.setStyle(Style.FILL);
	}

	int getHeight() {
		return rows * yscale + ymargin * 2;
	}

	int getWidth() {
		return cols * xscale + xmargin * 2;
	}

	void setMargins(int xm, int ym) {
		xmargin = xm;
		ymargin = ym;
	}

	void setScales(int xs, int ys) {
		xscale = xs;
		yscale = ys;
	}

	void setSeed(int s) {
		seed = s;
	}

	void wipe() {
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				grid[r][c] = EMPTY;
	} // wipe()

	public void generate(int seed) {
		this.seed = seed;

		wipe();
		// FILL IN THE REQUIRED SOLID CELLS
		int[] solidcs = { 5, 5, 5, 5, 5 };
		int[] solidrs = { 2, 3, 4, 5, 9 };
		for (int i = 0; i < 5; i++) {
			int c = solidcs[i];
			int r = solidrs[i];
			grid[r][c] = SOLID;
		}
		// FILL IN THE SEED-SPECIFIED BODY CELLS, AVOID OR EMPTY
		int[] avoidcs = { 4, 5, 4, 3, 4, 3, 4, 2, 3, 4, 1, 2, 3, 1, 2, 3, 1, 2,
				3, 1, 2, 3, 4, 3, 4, 5 };
		int[] avoidrs = { 1, 1, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8,
				8, 9, 9, 9, 9, 10, 10, 10 };
		int bitmask = 1;
		for (int i = 0; i < 26; i++) {
			int c = avoidcs[i];
			int r = avoidrs[i];
			grid[r][c] = ((seed & bitmask) != 0) ? AVOID : EMPTY;
			bitmask <<= 1;
		}
		// FLIP THE SEED-SPECIFIED COCKPIT CELLS, SOLID OR EMPTY
		int[] emptycs = { 4, 5, 4, 5, 4, 5 };
		int[] emptyrs = { 6, 6, 7, 7, 8, 8 };
		bitmask = 1 << 26;
		for (int i = 0; i < 6; i++) {
			int c = emptycs[i];
			int r = emptyrs[i];
			grid[r][c] = ((seed & bitmask) != 0) ? SOLID : COKPT; // ::added to
			// aid
			// coloring
			bitmask <<= 1;
		}
		// SKINNING -- wrap the AVOIDs with SOLIDs where EMPTY
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
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
		// mirror left side into right side
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols / 2; c++)
				grid[r][cols - 1 - c] = grid[r][c];
		}
	}

    /**
     * Attempt to update the marble with a new x value, boundary checking
     * enabled to make sure the new co-ordinate is valid.
     * 
     * @param newX
     *            Incremental value to add onto current x co-ordinate.
     */
    public void updateX(float newX, int maxX) {
        x -= newX;

        // boundary checking, don't want the marble rolling off-screen.
        if (x + hwidth >= maxX)
            x = maxX - hwidth;
        else if (x - hwidth < 0)
            x = hwidth;
    }

    /**
     * Attempt to update the marble with a new y value, boundary checking
     * enabled to make sure the new co-ordinate is valid.
     * 
     * @param newY
     *            Incremental value to add onto current y co-ordinate.
     */
    public void updateY(float newY, int maxY) {
        y += newY;
        
        // boundary checking, don't want the marble rolling off-screen.
        if (y + hheight >= maxY)
            y = maxY - hheight;
        else if (y - hheight < 0)
            y = hheight;
    }

    public void draw(Canvas canvas) {
    	
    	canvas.translate(x-hwidth, y-hheight);
    	
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				int x1 = xmargin + c * xscale;
				int y1 = ymargin + r * yscale;
				int m = grid[r][c];
				// ::added to aid coloring
				// for monochrome just draw SOLID's as black and you're done
				// otherwise...
				if (m == SOLID) {
					canvas.drawRect(x1, y1, x1 + xscale, y1 + yscale, pSolid);
				} else if (m == AVOID) {
					// float mysat = sats[r];
					// float mybri = bris[c]; //+90;
					// int h = 0;
					// if (r < 6) h = (colorseed & 0xff00) >> 8;
					// else if (r < 9) h = (colorseed & 0xff0000) >> 16;
					// else h = (colorseed & 0xff000000) >> 24;
					// colorMode(HSB);
					// fill(h, mysat, mybri);
//					canvas.drawRect(x1, y1, x1 + xscale, y1 + yscale, pAvoid);
				} else if (m == COKPT) {
					// float mysat = sats[c];
					// float mybri = bris[r]+40;
					// colorMode(HSB);
					// int h = (colorseed & 0xff);
					// fill(h, mysat, mybri);
					// rect(x1,y1,xscale,yscale);
					canvas.drawRect(x1, y1, x1 + xscale, y1 + yscale, pCokpt);
				}
			}
		}
	}
}
