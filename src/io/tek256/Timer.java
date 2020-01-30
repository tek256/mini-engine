package io.tek256;

public class Timer {
	private float lastUpdate;
	private static int FPS = 0,UPS = 0;
	public void init(){
		lastUpdate = getTime();
	}
	
	public float getTime(){
		return System.nanoTime() / 1000000000;
	}
	
	public float getDelta(){
		float cur = getTime();
		float delta = cur - lastUpdate;
		lastUpdate = cur;
		return delta;
	}
	
	public float getLastUpdate(){
		return lastUpdate;
	}
	
	public static int getFPS(){
		return FPS;
	}
	
	public static int getUPS(){
		return UPS;
	}
	
	protected void setFPS(int fps){
		Timer.FPS = fps;
	}
	
	protected void setUPS(int ups){
		Timer.UPS = ups;
	}
}
