package io.tek256.render;

import org.joml.Vector3f;

public class PointLight {
	private Vector3f color,position;
	private float intensity;
	private Attenuation attenuation;
	
	public PointLight(){
		color = new Vector3f(1f,1f,1f);
		position = new Vector3f();
		intensity = 0.5f;
		attenuation = new Attenuation(1,0,0);
	}
	
	public PointLight(Vector3f color, float intensity){
		this.position = new Vector3f();
		this.color = color;
		this.intensity = intensity;
		attenuation = new Attenuation(1,0,0);
	}
	
	public PointLight(Vector3f color, Vector3f position, float intensity){
		this.color = color;
		this.position = position;
		this.intensity = intensity;
	}
	
	public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation){
		this.color = color;
		this.position = position;
		this.intensity = intensity;
		this.attenuation = attenuation;
	}
	
	public Vector3f getPosition(){
		return position;
	}
	
	public Vector3f getColor(){
		return color;
	}
	
	public float getIntensity(){
		return intensity;
	}
	
	public void setPosition(Vector3f position){
		this.position = position;
	}
	
	public void setColor(Vector3f color){
		this.color = color;
	}
	
	public void setIntensity(float intensity){
		this.intensity = intensity;
	}
	
	public void setAttenuation(Attenuation atten){
		attenuation = atten;
	}
	
	public Attenuation getAttenuation(){
		return attenuation;
	}
	
	public static class Attenuation{
		private float constant,linear,exponent;
		
		public Attenuation(float constant, float linear, float exponent){
			this.constant = constant;
			this.linear = linear;
			this.exponent = exponent;
		}
		
		public float getConstant(){
			return constant;
		}
		
		public float getLinear(){
			return linear;
		}
		
		public float getExponent(){
			return exponent;
		}
		
		public void setConstant(float constant){
			this.constant = constant;
		}
		
		public void setLinear(float linear){
			this.linear = linear;
		}
		
		public void setExponent(float exponent){
			this.exponent = exponent;
		}
	}
}
