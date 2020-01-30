package io.tek256.render;

import org.joml.Vector3f;

public class Material {
	public static final Vector3f DEFAULT_COLOR = new Vector3f(1f,1f,1f);
	private Vector3f color;
	private float reflect;
	private Texture texture,normalMap;
	private boolean destroyed = false;
	
	public Material(){
		color = DEFAULT_COLOR;
		reflect = 0;
	}
	
	public Material(float r, float g, float b){
		this.color = new Vector3f(r,g,b);
		reflect = 0;
	}
	
	public Material(Vector3f color){
		this.color = color;
		reflect = 0;
	}
	
	public Material(float r, float g, float b, float reflect){
		this.color = new Vector3f(r,g,b);
		this.reflect = reflect;
	}
	
	public Material(Vector3f color, float reflect){
		this.color = color;
		this.reflect = reflect;
	}
	
	public Material(Texture texture){
		this.color = DEFAULT_COLOR;
		reflect = 0;
		this.texture = texture;
	}
	
	public Material(Texture texture, Texture normalMap){
		this.color = DEFAULT_COLOR;
		reflect = 0f;
		this.texture = texture;
		this.normalMap = normalMap;
	}
	
	public Material(Vector3f color, Texture texture){
		this.color = color;
		reflect = 0;
		this.texture = texture;
	}
	
	public Material(Vector3f color, float reflect, Texture texture){
		this.color = color;
		this.reflect = reflect;
		this.texture = texture;
	}
	
	public Material(Vector3f color, float reflect, Texture texture, Texture normalMap){
		this.color = color;
		this.reflect = reflect;
		this.texture = texture;
		this.normalMap = normalMap;
	}
	
	public void setColor(Vector3f color){
		if(destroyed) return;
		this.color = color;
	}
	
	public Vector3f getColor(){
		if(destroyed) return null;
		return color;
	}
	
	public void setReflect(float reflect){
		if(destroyed) return;
		this.reflect = reflect;
	}
	
	public float getReflect(){
		if(destroyed) return -1f;
		return reflect;
	}
	
	public void setTexture(Texture texture){
		if(destroyed) return;
		this.texture = texture;
	}
	
	public Texture getTexture(){
		if(destroyed) return null;
		return texture;
	}
	
	public boolean hasTexture(){
		if(destroyed) return false;
		return texture != null;
	}
	
	public void setNormalMap(Texture normalMap){
		if(destroyed) return;
		this.normalMap = normalMap;
	}
	
	public Texture getNormalMap(){
		if(destroyed) return null;
		return normalMap;
	}
	
	public boolean hasNormalMap(){
		if(destroyed) return false;
		return normalMap != null;
	}
	
	public void destroy(){
		destroyed = true;
		if(texture != null)
			texture.destroy();
		if(normalMap != null)
			normalMap.destroy();
	}
	
	public boolean isDestroyed(){
		return destroyed;
	}
}
