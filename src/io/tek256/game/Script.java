package io.tek256.game;

public abstract class Script {
	public String name;
	
	public abstract void queue();
	public abstract void onStart();
	public abstract void update(float  delta);
}
