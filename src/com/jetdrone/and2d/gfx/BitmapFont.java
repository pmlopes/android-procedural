package com.jetdrone.and2d.gfx;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.jetdrone.and2d.R;

public final class BitmapFont {
	
	private final Bitmap regular;
	private final Bitmap bold;
	
	private final Rect src;
	private final Rect dest;
	
	public BitmapFont(Context ctx) {
		Resources res = ctx.getResources();
		regular = BitmapFactory.decodeResource(res, R.drawable.regular);
		bold = BitmapFactory.decodeResource(res, R.drawable.bold);
		
		src = new Rect();
		dest = new Rect();
	}
	
	public void print(Canvas canvas, int x, int y, String str) {
		for(int i = 0; i < str.length(); i++) {
			int ch = str.charAt(i) - 32;
			src.set(ch * 8, 0, (ch + 1) * 8, 8);
			dest.set(x + i*8, y, x + (i + 1) * 8, y + 8);
			canvas.drawBitmap(regular, src, dest, null);
		}
	}

	public void printBold(Canvas canvas, int x, int y, String str) {
		for(int i = 0; i < str.length(); i++) {
			int ch = str.charAt(i) - 32;
			src.set(ch * 8, 0, (ch + 1) * 8, 8);
			dest.set(x + i*8, y, x + (i + 1) * 8, y + 8);
			canvas.drawBitmap(bold, src, dest, null);
		}
	}
}
