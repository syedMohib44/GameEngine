package com.base.engine.core;

import com.base.engine.rendering.Window;

public class CoreEngine {
	
/*	public static final int WIDTH = 1024, HEIGHT = 800;
	public static final String TITLE = "3D Engine";
	public static final double FRAME_CAP = 5000.0;*/
	private Game game;
	private int width, height;
	private double frameTime;
	private RenderingEngine renderingEngine;
	private boolean isRunning = false;
	
	public CoreEngine(int width, int height, double frameRate, Game game) {
		
		this.isRunning = false;
		this.game = game;
		this.width = width;
		this.height = height;
		this.frameTime = 1.0/frameRate;
		game.setEngine(this);
	}
	
	/*private void InitializeRenderingSystem() {
		System.out.println(RenderUtl.getOpenGLVersion());
		RenderUtl.initGraphics();
	}*/
	
	public void createWindow(String title) {
		Window.createWindow(width, height, title);
		this.renderingEngine = new RenderingEngine();
	}
	
	public void start() {
		if(isRunning)
			return;
		
		run();			
	}
	
	public void stop() {
		if(!isRunning)
			return;
		
		isRunning = false;		
	}
	
	private void run() {
		isRunning = true;
		
		int frames = 0;
		long frameCounter = 0;
		game.init();
		
		//This is initialized in constructor...
		//final double frameTime = 1.0 / FRAME_CAP; 
		
		double lastTime = Time.getTime();
		double unProcessedTime = 0;
		
		while(isRunning) {
			boolean render = false;
			double startTime = Time.getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime;
			unProcessedTime += passedTime;
			frameCounter += passedTime;
			
			while(unProcessedTime > frameTime) {
				render = true;
				unProcessedTime -= frameTime;
				if(Window.isClosedRequested())
					stop();
				
				//Time.setDelta(frameTime);
				
				game.input((float)frameTime);
				//renderingEngine.input((float)frameTime);
				//Input.update();
				
				game.update((float)frameTime);
				if(frameCounter >= 1.0) {
					System.out.println(frames);
					frames = 0;
					frameCounter = 0;
					}
				}
			if(render) {
				game.render(renderingEngine);
			    //renderingEngine.render(game.getRootObject());
				Window.render();
				//render();
				frames++;
			}
			else {
				try {
					Thread.sleep(1);
				}
			catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		cleanUp();
	}
	
	private void cleanUp() {
		Window.dispose();
	}
	
	public RenderingEngine getRenderingEngine() {
		return renderingEngine;
	}
		
	
	/*private void render() {
		RenderUtl.clearScreen();
		game.render();
		Window.render();
	}*/
	
	
	/*public static void main(String[] args) {
		Window.createWindow(WIDTH, HEIGHT, TITLE);
		MainComponent game = new MainComponent();
		game.start();
	}*/
}
