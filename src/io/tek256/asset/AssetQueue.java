package io.tek256.asset;

import java.io.File;
import java.util.ArrayList;

import io.tek256.asset.Asset.AssetType;

public class AssetQueue {
	public static int MAX_THREADS = 3;
	private static AssetLoader[] threads;
	
	public AssetQueue(){
		threads = new AssetLoader[MAX_THREADS];
		for(int i=0;i<MAX_THREADS;i++){
			threads[i] = new AssetLoader();
		}
	}
	
	public AssetQueue(Asset...assets){
		for(int i=0;i<MAX_THREADS;i++)
			threads[i] = new AssetLoader();
		
		for(int i=0;i<assets.length;i++)
			add(assets[i]);
	}
	
	public void start(){
		for(int i=0;i<threads.length;i++)
			threads[i].start();
	}
	
	public static AssetLoader getAssetLoader(int index){
		return threads[index];
	}

	public static boolean ready(){
		for(int i=0;i<threads.length;i++)
			if(!threads[i].isIdle())
				return false;
		return true;	
	}
	
	public static void addAll(Asset...assets){
		for(int i=0;i<assets.length;i++)
			add(assets[i]);
	}
	
	public static void addAll(ArrayList<Asset> assets){
		for(int i=0;i<assets.size();i++)
			add(assets.get(i));
	}
	
	public static void addAll(String type, String directory){
		addAll(AssetType.valueOf(type.toUpperCase()),directory);
	}
	
	public static void addAll(AssetType types, String directory){
		File dir = new File(directory);
		if(!dir.isDirectory())
			return;
		for(File f : dir.listFiles()){
			add(new Asset(types,f.getPath()));
		}
	}
	
	public static void addAll(AssetType types, String[] files){
		for(String f : files)
			add(new Asset(types,f));
	}
	
	public static void addAll(String type, String[] files){
		addAll(AssetType.valueOf(type.toUpperCase()),files);
	}
	
	//balance load across threads
	public static void add(Asset asset){
		int lowest = 0;
		for(int i=0;i<threads.length;i++){
			if(threads[i].getLoadSize() < threads[lowest].getLoadSize())
				lowest = i;
		}
		threads[lowest].add(asset);
	}
	
	public static ArrayList<Asset> getAllAssetsLoaded(){
		ArrayList<Asset> loaded = new ArrayList<Asset>();
		for(int i=0;i<threads.length;i++)
			loaded.addAll(threads[i].getLoaded());
		return loaded;
	}
	
	public static ArrayList<Asset> getAllAssetsInQueue(){
		ArrayList<Asset> toLoad = new ArrayList<Asset>();
		for(int i=0;i<threads.length;i++)
			toLoad.addAll(threads[i].getToLoad());
		return toLoad;
	}
	
	public static ArrayList<Asset> getAllForGL(){
		ArrayList<Asset> forGL = new ArrayList<Asset>();
		for(int i=0;i<threads.length;i++){
			forGL.addAll(threads[i].getGLLoad());
			threads[i].getGLLoad().clear();
		}
		return forGL;
	}
	
	public static void out(){
		StringBuilder ln = new StringBuilder();
		for(int i=0;i<threads.length;i++)
			ln.append(i+": "+threads[i].getAssetsLoaded()+" ");
		System.out.println(ln);
	}
	
	public static void clearAll(){
		for(int i=0;i<threads.length;i++)
			threads[i].clear();
	}
	
	public static void exit(){
		for(int i=0;i<threads.length;i++)
			threads[i].join();
	}
}
