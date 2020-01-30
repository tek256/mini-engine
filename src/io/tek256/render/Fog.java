package io.tek256.render;

import org.joml.Vector3f;

public class Fog {
	public static Fog NO = new Fog();
	private boolean active = false;
	private Vector3f color;
	private float density;
	
	public Fog(){
		active = false;
		color = new Vector3f();
		density = 0f;
	}
	
	public Fog(Vector3f color, float density){
		this(true, color, density);
	}
	
	public Fog(boolean active, Vector3f color, float density){
		this.active = active;
		this.color = color;
		this.density = density;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public void setDensity(float density){
		this.density = density;
	}
	
	public float getDensity(){
		return density;
	}
	
	public float getR(){
		return color.x;
	}
	
	public float getG(){
		return color.y;
	}
	
	public float getB(){
		return color.z;
	}
	
	public Vector3f getColor(){
		return color;
	}
}
