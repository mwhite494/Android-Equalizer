package com.visualizerdemo;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class VisualizerThread extends Thread {
	private SurfaceHolder holder;
	private Visualizer visualizer;
	private boolean running = false;
	
	public VisualizerThread(SurfaceHolder holder, Visualizer visualizer) {
		this.holder = holder;
		this.visualizer = visualizer;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		while(running) {
			if (!holder.getSurface().isValid()) {
				continue;
			}
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas(null);
				synchronized (holder) {
					visualizer.draw(canvas);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
}
