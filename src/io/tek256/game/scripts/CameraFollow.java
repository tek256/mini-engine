package io.tek256.game.scripts;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.tek256.game.Player;
import io.tek256.game.Script;
import io.tek256.render.Camera;

public class CameraFollow extends Script{
	private Camera camera;
	private Player player;
	
	public float followSpeed = 48.0f;
	public Vector2f offset = new Vector2f(0f, 360f);
	public Vector2f start;
	public Vector2f percentageBounds = new Vector2f(0.8f, 0.3f);
	public Vector2f realBounds;
	
	@Override
	public void queue(){
		name = "CameraFollow";
	}
	
	@Override
	public void onStart() {
		camera = Camera.getCurrent();
		player = Player.getInstance();
	}

	@Override
	public void update(float delta) {
		if(camera == null){
			camera = Camera.getCurrent();
			start = Camera.getCurrent().getPosition().as2f();
			Vector3f size = camera.getSize();
			realBounds = new Vector2f(size.x * percentageBounds.x, size.y * percentageBounds.y);
		}
		if(player == null)
			player = Player.getInstance();
		if(camera != null && player != null){
			if(outsideOfBounds()){
				camera.move2D(getDir().mul(followSpeed * delta).x, 0f);
			}
		}
	}
	
	public boolean outsideOfBounds(){
		Vector2f extents = new Vector2f(realBounds.x / 2f, realBounds.y / 2f);
		float centerx = camera.getX() + (camera.getSize().x / 2f);
		float centery = camera.getY() + (camera.getSize().y/ 2f);
		return player.getX() > centerx + extents.x || player.getX() < centerx - extents.x;
	}
	
	public Vector2f getDir(){
		Vector2f dir = new Vector2f(player.getX() - (camera.getX() + (camera.getSize().x / 2f)), player.getY() - (camera.getY() + (camera.getSize().y / 2f)));
		return dir.normalize();
	}
		
}
