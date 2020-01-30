package io.tek256.runtime;

public class Timer {
	private float lastUpdate;
	private static int FPS = 0,UPS = 0;
	
	public void init(){
		lastUpdate = getTime();
	}
	
	public static float getTime(){
		return System.nanoTime() / 1000000000f;
	}
	
	public float getDelta(){
		float cur = getTime();
		float delta = cur - lastUpdate;
		lastUpdate = cur;
		return delta;
	}
	
	public void update(){
		lastUpdate = getTime();
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
	
	public void setFPS(int fps){
		Timer.FPS = fps;
	}
	
	public void setUPS(int ups){
		Timer.UPS = ups;
	}
	
	public static float milliToNano(float milli){
		return milli * 1000000;
	}
	
	public static float nanoToMilli(float nano){
		return nano / 1000000;
	}
}
