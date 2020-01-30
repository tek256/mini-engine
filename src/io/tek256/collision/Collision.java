package io.tek256.collision;

import java.util.ArrayList;

public class Collision {
	public static enum CollisionMode{
		Mode3D,
		Mode2D
	};
	public static CollisionMode currentMode = CollisionMode.Mode2D;
	public static float gravity = 9.8f;
	public static ArrayList<BoundingBox> boxes = new ArrayList<BoundingBox>();
	
	public static boolean intersects(BoundingBox a, BoundingBox b){
		int active = a.isActive() ? 1 : 0;
		if(active == 0)
			active = b.isActive() ? 2 : 0;
		
		if(active == 0)
			return false;
		
		return ((active == 1) ? a : b).intersects((active == 1) ? b : a);
	}
	
	public static void update(float delta){
		for(BoundingBox b : boxes)
			b.update(delta);
		applyGravity(delta);
		solve();
	}
	
	public static void applyGravity(float delta){
		for(BoundingBox b : boxes){
			if(b.usesGravity())
				b.move(0, -gravity*b.mass*delta, 0);
		}
	}
	
	public static void solve(){
		for(BoundingBox a: boxes){
			if(a.isActive()){
				for(BoundingBox b : boxes){
					if(!b.equals(a) && b.isCollidable()){
						if(intersects(a,b))
							handle(a,b);
					}
				}
			}
			a.canMove = true;
		}
	}
	
	public static void handle(BoundingBox a, BoundingBox b){
		float x = 0, y = 0, z = 0;
		x = a.overlapX(b);
		y = a.overlapY(b);
		if(currentMode == CollisionMode.Mode3D)
			z = a.overlapZ(b);
		
		a.collides(b);
		b.collides(a);
		
		while(a.intersects(b)){
			float min = Float.POSITIVE_INFINITY;
			if(Collision.currentMode == Collision.CollisionMode.Mode3D){
				min = Math.min(x, y);
				min = Math.min(min, z);
			}else if(Collision.currentMode == Collision.CollisionMode.Mode2D){
				min = Math.min(x, y);
			}
			
			if(min == x){
				if(a.isRightOf(b)){
					a.move(x, 0, 0);
					x = 0f;
				}else if(a.isLeftOf(b)){
					a.move(-x, 0, 0);
					x =0f;
				}
			}else if(min == y){
				if(a.isAbove(b)){
					a.move(0, y, 0);
					y = 0f;
				}else if(a.isBelow(b)){
					a.move(0, -y, 0);
					y = 0f;
				}
			}else if(min == z){
				if(a.isFrontOf(b)){
					a.move(0, 0, z);
					z = 0f;
				}else if(a.isBackOf(b)){
					a.move(0, 0, -z);
					z = 0f;
				}
			}
		}
	}
}
