package io.tek256.runtime;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.tek256.collision.BoundingBox;

public class Transform {
	protected Vector3f position,rotation,scale,size,velocity;
	protected BoundingBox boundingBox;
	
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
		this(position, rotation, scale, new Vector3f(1f,1f,1f));
	}
	
	public Transform(Vector3f position, Vector3f rotation, Vector3f scale, Vector3f size){
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.size = size;
		this.velocity = new Vector3f();
		this.boundingBox = new BoundingBox(position,size);
	}
	
	public void setPosition(Vector3f position){
		this.position.set(position);
	}
	
	public void setPosition(Vector2f position){
		this.position.set(position.x, position.y, this.position.z);
	}
	
	public void setPosition(float x, float y, float z){
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public void setPosition(float x, float y){
		this.position.x = x;
		this.position.y = y;
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
		this.rotation.set(rotation);
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
	
	public void setVelocity(Vector3f velocity){
		this.velocity.set(velocity);
	}
	
	public void setVelocity(float x, float y, float z){
		this.velocity.set(x,y,z);
	}
	
	public void setVX(float x){
		this.velocity.x = x;
	}
	
	public void setVY(float y){
		this.velocity.y = y;
	}
	
	public void setVZ(float z){
		this.velocity.z = z;
	}
	
	public Vector3f getVelocity(){
		return velocity;
	}
	
	public float getVX(){
		return velocity.x;
	}
	
	public float getVY(){
		return velocity.y;
	}
	
	public float getVZ(){
		return velocity.z;
	}
	
	public void setScale(Vector3f scale){
		this.scale.set(scale);
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
	
	public void setSize(Vector3f size){
		setSize(size.x, size.y, size.z);
	}
	
	public void setSize(float x, float y, float z){
		size.x = x;
		size.y = y;
		size.z = z;
	}
	
	public Vector3f getSize(){
		return size;
	}
	
	public Vector3f getGameSize(){
		return new Vector3f(size.x * scale.x, size.y * scale.y, size.z * scale.z);
	}
	
	public void setGameSize(Vector3f gameSize){
		setGameSize(gameSize.x,gameSize.y,gameSize.z);
	}
	
	public void setGameSize(float x, float y, float z){
		scale.x = x / size.x;
		scale.y = y / size.y;
		scale.z = z / size.z;
	}
	
	public void setSizeX(float x){
		size.x = x;
	}
	
	public void setSizeY(float y){
		size.y = y;
	}
	
	public void setSizeZ(float z){
		size.z = z;
	}
	
	public void move2D(float x, float y){
		position.x += x;
		position.y += y;
	}
	
	public void move2D(Vector2f vec){
		move2D(vec.x, vec.y);
	}
	
	public void move2D(Vector3f vec){
		move2D(vec.x, vec.y);
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
		
		boundingBox.setPosition(position);
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
	
	public float getDistance(Transform transform){
		return getDistance(transform.position);
	}
	
	public float getDistance(Vector3f point){
		return (float)(Math.sqrt(Math.pow((position.x - point.x),2)
				+ Math.pow((position.y - point.y),2)
				+ Math.pow((position.z - point.z),2)));
	}
}
