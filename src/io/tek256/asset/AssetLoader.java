package io.tek256.asset;

import java.util.ArrayList;

import io.tek256.Util;
import io.tek256.asset.Asset.AssetType;

public class AssetLoader implements Runnable {
	private static int threadNumber = 0;
	private Thread thread;
	private int id;
	private boolean allowIdle = true;
	private LoadState state = LoadState.NOT_STARTED;
	private ArrayList<Asset> toLoad;
	private ArrayList<Asset> loaded;
	private ArrayList<Asset> forGL;
	private int assetsLoaded = 0;
	
	public AssetLoader(){
		toLoad = new ArrayList<Asset>();
		loaded = new ArrayList<Asset>();
		forGL = new ArrayList<Asset>();
		id = threadNumber;
		threadNumber ++;
	}
	
	public void start(){
		thread = new Thread(this,"GAME_ASSET_LOADER_"+id);
		thread.start();
	}
	
	@Override
	public void run(){
		state = LoadState.STARTING;
		//starting
		while(!isDone()){
			if(toLoad.size() > 0 && !isPaused()){
				state = LoadState.ACTIVE;
			}
			while(isActive()){
				while(toLoad.size() > 0 && !isPaused()){
					Asset a = toLoad.get(0);
					if(a.isModel()){
						a.setResource(Util.getMeshBuffer(a.getResourcePath()));
						a.setLoaded(false);
						toLoad.remove(a);
						loaded.add(a);
						forGL.add(a);
					}else if(a.isText()){
						a.setResource(Util.getText(a.getResourcePath()));
						loaded.add(a);
						toLoad.remove(a);
					}else if(a.isTexture()){
						String path = a.getResourcePath();
						if(path.toLowerCase().endsWith(".png")){
							a.setResource(Util.getPNGBuffer(a.getResourcePath()));
							a.setLoaded(false);
							toLoad.remove(a);
							forGL.add(a);
						}else if(path.toLowerCase().endsWith(".sheet") || path.toLowerCase().endsWith(".json")){
							a.setResource(a.getResourcePath());
							a.setLoaded(false);
							a.setType(AssetType.TEXTURESHEET);
							toLoad.remove(a);
							forGL.add(a);
						}
					}
					assetsLoaded ++;
					state = LoadState.IDLE;
				}
				
				while(isPaused()){
					sleep(1);
				}
			}
			
			if(isIdleAllowed() && state != LoadState.ENDING){
				state = LoadState.IDLE;
				sleep(1);
			}else{
				state = LoadState.ENDING;
			}
			
			if(isEnding()){
				//toEnd
				state = LoadState.DONE;
				join();
			}
		}
	}
	
	
	public void sleep(long ms){
		try{
			Thread.sleep(ms);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public synchronized void join(long ms){
		if(state != LoadState.ENDING){
			state = LoadState.ENDING;
			return;
		}
		threadNumber --;
		try{
			thread.join(ms);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public synchronized void clear(){
		toLoad.clear();
		loaded.clear();
		forGL.clear();
	}
	
	public void join(){
		join(100);
	}
	
	public int getAssetsLoaded(){
		return assetsLoaded;
	}
	
	public synchronized void add(Asset asset){
		toLoad.add(asset);
	}
	
	public synchronized void add(String type, String resource){
		toLoad.add(new Asset(AssetType.valueOf(type),resource));
	}
	
	public void allowIdle(boolean idle){
		allowIdle = idle;
	}
	
	public synchronized void pause(){
		state = LoadState.PAUSE;
	}
	
	public synchronized void resume(){
		if(toLoad.size() > 0)
			state = LoadState.ACTIVE;
		else
			state = LoadState.IDLE;
	}
	
	public boolean isIdleAllowed(){
		return allowIdle;
	}
	
	public boolean isStarting(){
		return state == LoadState.STARTING;
	}
	
	public boolean isEnding(){
		return state == LoadState.ENDING;
	}
	
	public boolean isActive(){
		return state == LoadState.ACTIVE || state == LoadState.PAUSE;
	}
	
	public boolean isIdle(){
		return state == LoadState.IDLE;
	}
	
	public boolean isPaused(){
		return state == LoadState.PAUSE;
	}
	
	public boolean isDone(){
		return state == LoadState.DONE;
	}
	
	public LoadState getState(){
		return state;
	}
	
	public synchronized ArrayList<Asset> getToLoad(){
		return toLoad;
	}
	
	public synchronized ArrayList<Asset> getLoaded(){
		return loaded;
	}
	
	public synchronized ArrayList<Asset> getGLLoad(){
		return forGL;
	}
	
	public synchronized int getLoadedSize(){
		return loaded.size();
	}
	
	public synchronized int getLoadSize(){
		return toLoad.size();
	}
	
	public synchronized int getGLLoadSize(){
		return forGL.size();
	}
	
	public static enum LoadState{
		NOT_STARTED,
		STARTING,
		IDLE,
		PAUSE,
		ACTIVE,
		ENDING,
		DONE;
	}
}
