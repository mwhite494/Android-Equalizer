package com.visualizerdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Visualizer extends SurfaceView implements SurfaceHolder.Callback{

	private VisualizerThread thread;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private float[] spectrum = new float[6];
	
	public Visualizer(Context context) {
		super(context);
		initialize();
	}

	public Visualizer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	public Visualizer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	
	private void initialize() {
		for (int i = 0; i < spectrum.length; i++) {
			spectrum[i] = 0f;
		}
		getHolder().addCallback(this);
		thread = new VisualizerThread(getHolder(), this);
		setFocusable(true);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initialize();
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRunning(false);
		while(true) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		spectrum = MainActivity.getSpectrum();
		int barWidth = (canvas.getWidth()-220)/12;
		paint.setColor(Color.MAGENTA);
		canvas.drawRect(5, canvas.getHeight()-spectrum[5], barWidth+5, canvas.getHeight(), paint);
		canvas.drawRect(barWidth*11+225, canvas.getHeight()-spectrum[5], barWidth*12+225, canvas.getHeight(), paint);
		paint.setColor(Color.BLUE);
		canvas.drawRect(barWidth+25, canvas.getHeight()-spectrum[4], barWidth*2+25, canvas.getHeight(), paint);
		canvas.drawRect(barWidth*10+205, canvas.getHeight()-spectrum[4], barWidth*11+205, canvas.getHeight(), paint);
		paint.setColor(Color.GREEN);
		canvas.drawRect(barWidth*2+45, canvas.getHeight()-spectrum[3], barWidth*3+45, canvas.getHeight(), paint);
		canvas.drawRect(barWidth*9+185, canvas.getHeight()-spectrum[3], barWidth*10+185, canvas.getHeight(), paint);
		paint.setColor(Color.YELLOW);
		canvas.drawRect(barWidth*3+65, canvas.getHeight()-spectrum[2], barWidth*4+65, canvas.getHeight(), paint);
		canvas.drawRect(barWidth*8+165, canvas.getHeight()-spectrum[2], barWidth*9+165, canvas.getHeight(), paint);
		paint.setColor(0xFFFF9933);
		canvas.drawRect(barWidth*4+85, canvas.getHeight()-spectrum[1], barWidth*5+85, canvas.getHeight(), paint);
		canvas.drawRect(barWidth*7+145, canvas.getHeight()-spectrum[1], barWidth*8+145, canvas.getHeight(), paint);
		paint.setColor(Color.RED);
		canvas.drawRect(barWidth*5+105, canvas.getHeight()-spectrum[0], barWidth*6+105, canvas.getHeight(), paint);
		canvas.drawRect(barWidth*6+125, canvas.getHeight()-spectrum[0], barWidth*7+125, canvas.getHeight(), paint);
	}

}
