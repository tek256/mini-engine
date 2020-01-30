package io.tek256.audio;

import java.util.HashMap;

import org.lwjgl.openal.AL10;

public class Sound {
	private static HashMap<String,Sound> soundMap = new HashMap<String,Sound>();
	
	private String name,path;
	public int id;

	public Sound(String path){
		this.path = path;
		init();
		soundMap.put(path, this);
	}
	
	private void init (){
		if(path == null)
			return;
		id = AL10.alGenBuffers();
		
	}
	
	public static HashMap<String,Sound> getSoundMap(){
		return soundMap;
	}
	
	public static Sound getSoundByPath(String path){
		for(Sound s: soundMap.values())
			if(s.getPath() == path)
				return s;
		return null;
	}
	
	public static Sound getSoundByName(String name){
		for(Sound s: soundMap.values())
			if(s.getName() == name)
				return s;
		return null;
	}
	
	public static Sound getSound(String key){
		return soundMap.get(key);
	}
	
	public boolean hasName(){
		if(name == null)
			return false;
		if(name.equals(""))
			return false;
		return true;
	}
	
	public void name(String name){
		if(hasName())
			soundMap.remove(this.name); //no double entries
		this.name = name;
		soundMap.put(name, this);
	}
	
	public String getName(){
		return name;
	}
	
	public String getPath(){
		return path;
	}
}
