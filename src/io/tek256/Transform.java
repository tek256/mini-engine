package io.tek256;

import org.joml.Vector3f;

public class Transform {
	protected Vector3f position,rotation,scale;
	
	public Transform(){
		this(new Vector3f());
	}
	
	public Transform(Vector3f position){
		this(position, new Vector3f());
	}
	
	public Transform(Vector3f position, Vector3f rotation){
		this(position, rotation, new Vector3f(1f,1f,1f));
	}
	
	public Transform(Vector3f position, Vector3f rotation, Vector3f scale){
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public void setPosition(Vector3f position){
		this.position = position;
	}
	
	public void setPosition(float x, float y, float z){
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public void setX(float x){
		position.x = x;
	}
	
	public void setY(float y){
		position.y = y;
	}
	
	public void setZ(float z){
		position.z = z;
	}
	
	public Vector3f getPosition(){
		return position;
	}
	
	public float getX(){
		return position.x;
	}
	
	public float getY(){
		return position.y;
	}
	
	public float getZ(){
		return position.z;
	}
	
	public void setRotation(Vector3f rotation){
		this.rotation = rotation;
	}
	
	public void setRotation(float x, float y, float z){
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}
	
	public void setRX(float x){
		rotation.x = x;
	}
	
	public void setRY(float y){
		rotation.y = y;
	}
	
	public void setRZ(float z){
		rotation.z = z;
	}
	
	public float getRX(){
		return rotation.x;
	}
	
	public float getRY(){
		return rotation.y;
	}
	
	public float getRZ(){
		return rotation.z;
	}
	
	public Vector3f getRotation(){
		return rotation;
	}
	
	public void setScale(Vector3f scale){
		this.scale = scale;
	}
	
	public void setScale(float x, float y, float z){
		scale.x = x;
		scale.y = y;
		scale.z = z;
	}
	
	public void setScaleX(float x){
		scale.x = x;
	}
	
	public void setScaleY(float y){
		scale.y = y;
	}
	
	public void setScaleZ(float z){
		scale.z = z;
	}
	
	public float getScaleX(){
		return scale.x;
	}
	
	public float getScaleY(){
		return scale.y;
	}
	
	public float getScaleZ(){
		return scale.z;
	}
	
	public Vector3f getScale(){
		return scale;
	}
	
	public void move(Vector3f vec){
		move(vec.x, vec.y, vec.z);
	}
	
	public void move(float x, float y, float z){
		if(z != 0){
			position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1f * z;
			position.z += (float)Math.cos(Math.toRadians(rotation.y)) * z;
		}
		if(x != 0){
			position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1f * x;
			position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * x;
		}
		position.y += y;
	}
	
	public void moveX(float x){
		position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1f * x;
		position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * x;
	}
	
	public void moveZ(float z){
		position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1f * z;
		position.z += (float)Math.cos(Math.toRadians(rotation.y)) * z;
	}
	
	public void rotate(Vector3f vec){
		rotate(vec.x, vec.y, vec.z);
	}
	
	public void rotate(float x, float y, float z){
		rotation.x += x;
		rotation.y += y;
		rotation.z += z;
	}
	
	public float clamp(float value, float min, float max){
		if(value > max)
			value = max;
		else if(value < min)
			value = min;
		return value;
	}
}
