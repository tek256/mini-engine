package io.tek256.runtime;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.tek256.game.Game;
import io.tek256.render.Texture;

public class LevelTexture {
	public Vector3f color;
	public Vector3f position;
	public Vector2f size,texRepeat;
	public Texture texture;
	
	public LevelTexture(){
		position = new Vector3f();
		size = new Vector2f(Game.TILE_SIZE, Game.TILE_SIZE);
		texRepeat = new Vector2f(1f,1f);
		color = new Vector3f(1f,1f,1f);
	}
	
	public LevelTexture(Vector3f position, Vector2f size, Texture texture){
		this.position = position;
		this.size = size;
		this.texture = texture;
		this.color = new Vector3f(1f,1f,1f);
		texRepeat = new Vector2f(1f,1f);
	}
	
}
