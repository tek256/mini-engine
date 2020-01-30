package io.tek256.game;

import org.joml.Vector3f;

import io.tek256.Keyboard;
import io.tek256.Mouse;
import io.tek256.Transform;
import io.tek256.render.Camera;

public class Player extends Transform{
	public float lookSpeed = 1.0f;
	private float moveSpeed;
	private float walkSpeed = 5f;
	private float runSpeed = 8f;
	
	public Player(){
		this(new Vector3f());
	}
	
	public Player(Vector3f position){
		this(position,new Vector3f());
	}
	
	public Player(Vector3f position, Vector3f rotation){
		this.position = position;
		this.rotation = rotation;
	}
	
	public void input(){
		if(Mouse.isGrabbed()){
			if(Keyboard.getButton("sprint") > 0)
				moveSpeed = runSpeed;
			else
				moveSpeed=  walkSpeed;
			
			float h = Keyboard.getButton("horizontal");
			float v = Keyboard.getButton("vertical");
			if(h != 0 || v != 0)
				move(h * 0.025f * moveSpeed,0,-v * 0.025f * moveSpeed);
		
			float dx = Mouse.getDX();
			float dy = Mouse.getDY();
			if(dx != 0 || dy != 0){
				rotate(dy * 0.1f, dx * 0.1f, 0);
				rotation.x = clamp(rotation.x,-80, 90);
			}
		}
	}
	
	public void update(float delta){
		Camera.getCurrent().setPosition(position);
		Camera.getCurrent().setRotation(rotation);
	}
}
