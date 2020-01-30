package io.tek256.collision;

import org.joml.Vector3f;

public class BoundingBox {
	public Vector3f position,size,min,max;
	public boolean relativeGravity = false;
	private boolean active = true,gravity = true,collidable = true;
	protected boolean canMove = false;
	protected boolean grounded = false;
	protected boolean updatedTransform = false;
	private float activeSwitch,activeLifetime = 1f;
	public float mass = 1.0f;
	
	public BoundingBox(){
		position = new Vector3f();
		size = new Vector3f(1f);
		updateBounds();
	}
	
	public BoundingBox(Vector3f size){
		this.size = size;
		this.position = new Vector3f();
		updateBounds();
	}

	public BoundingBox(Vector3f position, Vector3f size){
		this.size = size;
		this.position = position;
		updateBounds();
	}
	
	private void updateBounds(){
		if(min == null)
			min = new Vector3f();
		if(max == null)
			max = new Vector3f();
		
		if(Collision.currentMode == Collision.CollisionMode.Mode3D){
			min.x = position.x - (size.x / 2f);
			min.y = position.y - (size.y / 2f);
			min.z = position.z - (size.z / 2f);
			
			max.x = position.x + (size.x / 2f);
			max.y = position.y + (size.y / 2f);
			max.z = position.z + (size.z / 2f);
		}else if(Collision.currentMode == Collision.CollisionMode.Mode2D){
			min.x = position.x - (size.x / 2f);
			min.y = position.y - (size.y / 2f);
			
			max.x = position.x + (size.x / 2f);
			max.y = position.y + (size.y / 2f);
		}
		
		if(active)
			canMove = false;
	}
	
	public void update(float delta){
		if(activeSwitch > 0)
			activeSwitch -= delta;
		if(activeSwitch <= 0){
			active = false;
			if(relativeGravity && gravity)
				gravity = false;
		}
	}
	
	protected void move(float x, float y, float z){
		if(Collision.currentMode == Collision.CollisionMode.Mode2D)
			return;
		if(x != 0 || y != 0 || z != 0)
			setActive(true);
		
		position.x += x;
		position.y += y;
		position.y += z;
		
		updateBounds();
	}
	
	protected void move(float x, float y){
		if(Collision.currentMode == Collision.CollisionMode.Mode3D)
			return;
		if(x != 0 || y != 0)
			setActive(true);
		
		position.x += x;
		position.y += y;
		
		updateBounds();
	}
	
	public void setSize(float x, float y, float z){
		this.size.set(x,y,z);
		updateBounds();
	}
	
	public void setPosition(Vector3f position){
		this.position.set(position);
		updateBounds();
	}

	public void setPosition(float x, float y, float z){
		this.size.set(x,y,z);
		updateBounds();
	}
	
	public void setSize(Vector3f size){
		this.size.set(size);
		updateBounds();
	}
	
	public Vector3f getPosition(){
		return position;
	}
	
	public Vector3f getSize(){
		return size;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public boolean canMove(){
		return canMove;
	}
	
	public boolean usesGravity(){
		return gravity;
	}
	
	public void setCollidable(boolean collidable){
		this.collidable = collidable;
	}
	
	public boolean isCollidable(){
		return collidable;
	}
	
	public void setGravity(boolean gravity){
		this.gravity = gravity;
	}
	
	public boolean isGrounded(){
		return grounded;
	}
	
	public Vector3f nextPosition(){
		canMove = true;
		return position;
	}
	
	public void setActive(boolean active){
		if(!this.active && active){
			if(relativeGravity && !gravity)
				gravity = true;
		}
		activeSwitch = activeLifetime;
		this.active = active;
	}
	
	public boolean contains(Vector3f p){
		if(Collision.currentMode == Collision.CollisionMode.Mode3D){
		return (p.x >= min.x && p.x <= max.x &&
				p.y >= min.y && p.y <= max.y &&
				p.z >= min.z && p.z <= max.z);
		}else if(Collision.currentMode == Collision.CollisionMode.Mode2D){
			return (p.x >= min.x && p.x <= max.x &&
					p.y >= min.y && p.y <= max.y);
		}
		return false;
	}
	
	public boolean intersects(BoundingBox o){
		if(Collision.currentMode == Collision.CollisionMode.Mode3D){
			if(Math.abs(position.x - o.position.x) < (size.x / 2f) + (o.size.x / 2f))
				if(Math.abs(position.y - o.position.y) < (size.y / 2f) + (o.size.y /2f))
					if(Math.abs(position.z - o.position.z) < (size.z / 2f) + (o.size.z / 2f))
						return true;
		}else if(Collision.currentMode == Collision.CollisionMode.Mode2D){
			if(Math.abs(position.x - o.position.x) < (size.x / 2f) + (o.size.x / 2f))
				if(Math.abs(position.y - o.position.y) < (size.y / 2f) + (o.size.y /2f))
					return true;
		}
		return false;
	}
	
	public boolean isLeftOf(BoundingBox o){
		return position.x <  o.position.x;
	}
	
	public boolean isRightOf(BoundingBox o){
		return position.x > o.position.x;
	}
	
	public boolean isAbove(BoundingBox o){
		return position.y > o.position.y; 
	}
	
	public boolean isBelow(BoundingBox o){
		return position.y < o.position.y;
	}
	
	public boolean isFrontOf(BoundingBox o){
		return position.z > o.position.z;
	}
	
	public boolean isBackOf(BoundingBox o){
		return position.z < o.position.z;
	}
	
	protected float overlapX(BoundingBox o){
		return ((size.x /2f) + (o.size.x / 2f)) - Math.abs(position.x - o.position.x);
	}
	
	protected float overlapY(BoundingBox o){
		return ((size.y /2f) + (o.size.y / 2f)) - Math.abs(position.y - o.position.y);
	}
	protected float overlapZ(BoundingBox o){
		return ((size.z /2f) + (o.size.z / 2f)) - Math.abs(position.z - o.position.z);
	}
	
	public void collides(BoundingBox o){
		canMove = false;
		if(active)
			if(o.isBelow(this))
				grounded = true;
	}
	
	public void destroy(){
		Collision.boxes.remove(this);
	}
	
}
