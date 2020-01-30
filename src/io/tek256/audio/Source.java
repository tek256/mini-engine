package io.tek256.audio;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

public class Source {
	private static HashMap<String,Source> sources = new HashMap<String,Source>();
	protected int id = -1;
	protected Vector3f position,rotation,velocity;
	protected float gain = 1.0f,freq = 1.0f;
	protected Sound sound;
	private String name;
	
	public Source(){
		position = new Vector3f();
		rotation = new Vector3f();
		velocity = new Vector3f();
		updateBuffers();
	}
	
	public Source(String name){
		this.name = name;
		position = new Vector3f();
		rotation = new Vector3f();
		velocity = new Vector3f();
		updateBuffers();
	}
	
	public Source(Sound sound){
		this.sound = sound;
		position = new Vector3f();
		rotation = new Vector3f();
		velocity = new Vector3f();
		updateBuffers();
	}
	
	public Source(String name, Sound sound){
		this.name = name;
		this.sound = sound;
		position = new Vector3f();
		rotation = new Vector3f();
		velocity = new Vector3f();
		updateBuffers();
	}
	
	private boolean updateBuffers(){
		if(id == -1)
			id = AL10.alGenSources();
		AL10.alSourcei(id, AL10.AL_BUFFER, (sound != null) ? sound.id : 0);
		AL10.alSourcef(id, AL10.AL_PITCH, freq);
		AL10.alSourcef(id, AL10.AL_GAIN, gain);
		AL10.alSource3f(id, AL10.AL_POSITION, position.x, position.y, position.z);
		AL10.alSource3f(id, AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
		
		if(AL10.alGetError() != AL10.AL_NO_ERROR)
			return false;
		
		if(!sources.containsValue(this))
			sources.put(name, this);
		
		return true;
	}
	
	public float getGain(){
		return gain;
	}
	
	public float getFreq(){
		return freq;
	}
	
	public Vector3f getRotation(){
		return rotation;
	}
	
	public void setRotation(Vector3f rotation){
		this.rotation = rotation;
		updateBuffers();
	}
	
	public Vector3f getPosition(){
		return position;
	}
	
	public void setPosition(Vector3f position){
		this.position = position;
		updateBuffers();
	}
	
	public void setSound(Sound sound){
		this.sound = sound;
		updateBuffers();
	}
	
	public Sound getSound(){
		return sound;
	}
	
	public boolean hasSound(){
		return sound != null;
	}
	
	public void setName(String name){
		if(hasName())
			sources.remove(this.name);
		this.name = name;
		sources.put(name, this);
	}
	
	public String getName(){
		return name;
	}
	
	public boolean hasName(){
		if(name == null)
			return false;
		if(name.equals(""))
			return false;
		return true;
	}
	
	public static HashMap<String,Source> getSources(){
		return sources;
	}
	
	public static Source getSource(String name){
		return sources.get(name);
	}
	
	public static Source getSourceByPosition(Vector3f position){
		for(Source s: sources.values())
			if(s.position == position)
				return s;
		return null;
	}
	
	public static ArrayList<Source> getSourcesBySound(Sound sound){
		ArrayList<Source> f = new ArrayList<Source>();
		for(Source s: sources.values())
			if(s.sound == sound)
				f.add(s);
		return f;
	}
}
