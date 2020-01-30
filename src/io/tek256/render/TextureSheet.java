package io.tek256.render;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.joml.Vector2f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.tek256.Util;

public class TextureSheet {
	private static HashMap<String,TextureSheet> sheets = new HashMap<String,TextureSheet>();
	private Texture src;
	private String name;
	private int sub_width,sub_height;
	private int per_width,per_height;
	private Texture[] subTextures;
	private HashMap<String,Integer> customMap;
	
	public TextureSheet(Texture t, int sub_width, int sub_height){
		this.src = t;
		this.sub_width = sub_width;
		this.sub_height = sub_height;
		per_width = t.getWidth() / sub_width;
		per_height = t.getHeight() / sub_height;
		customMap = new HashMap<String,Integer>();
		initSubTextures();
	}
	
	public TextureSheet(String path){
		loadFromInfo(path);
	}
	
	private void loadFromInfo(String path){
		customMap = new HashMap<String,Integer>();
		JsonParser p = new JsonParser();
		JsonElement e = p.parse(Util.getText(path));
		JsonObject root = e.getAsJsonObject();
		
		src = Texture.getTexture(root.get("path").getAsString());
		name = root.get("name").getAsString();
		sub_width = root.get("width").getAsInt();
		sub_height = root.get("height").getAsInt();
		per_width = src.getWidth() / sub_width;
		per_height = src.getHeight() / sub_height;
		
		initSubTextures();
		
		JsonArray mappings = root.get("mappings").getAsJsonArray();
		for(int i=0;i<mappings.size();i++){
			JsonObject mapping = mappings.get(i).getAsJsonObject();
			int id = mapping.get("id").getAsInt();
			String name = mapping.get("name").getAsString();
			subTextures[id].name(name);
			customMap.put(name, id);
		}
		
		sheets.put(name, this);
	}
	
	public void nameSheet(String sheetName){
		this.name = sheetName;
		sheets.put(name, this);
	}
	
	public String getSheetName(){
		return name;
	}
	
	public static TextureSheet getSheet(String name){
		if(sheets.containsKey(name))
			return sheets.get(name);
		return null;
	}
	
	public void outMappings(){
		for(String k : customMap.keySet()){
			System.out.println(k + ":"+customMap.get(k));
		}
	}
	
	public void saveMappings(String path){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject root = new JsonObject();
			root.addProperty("name", name);
			root.addProperty("type", "spritesheet");
			root.addProperty("path", src.getPath());
			root.addProperty("width", sub_width);
			root.addProperty("height", sub_height);
			JsonArray mapps = new JsonArray();
			STMapping[] mappps = new STMapping[customMap.keySet().size()];
			int i=0;
			for(String k : customMap.keySet()){
				mappps[i] = new STMapping(k,customMap.get(k));
				i++;
			}
			Arrays.sort(mappps);
			for(STMapping m : mappps){
				JsonObject mapping = new JsonObject();
				mapping.addProperty("id", m.id);
				mapping.addProperty("name", m.name);
				mapps.add(mapping);
			}
			root.add("mappings", mapps);
			out.write(gson.toJson(root));
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public HashMap<String,Integer> getNamed(){
		return customMap;
	}
	
	public int getNameIndex(String name){
		return customMap.get(name);
	}
	
	public int getNamedLength(){
		return customMap.keySet().size();
	}
	
	public String[] getNamedKeyset(){
		String[] array = new String[customMap.keySet().size()];
		int pos = 0;
		for(String s : customMap.keySet()){
			array[pos] = s;
			pos++;
		}
		return array;
	}
	
	public Integer[] getNamedValues(){
		Integer[] array = new Integer[customMap.values().size()];
		int pos = 0;
		for(Integer i : customMap.values()){
			array[pos] = i;
			pos++;
		}
		return array;
	}
	
	public void name(String name, int subTexture){
		customMap.put(name, subTexture);
	}
	
	public String getName(int subTexture){
		String[] keys = new String[customMap.keySet().size()];
		int i = 0;
		for(String key : customMap.keySet()){
			keys[i] = key;
			i++;
		}
		i=0;
		for(Integer val : customMap.values()){
			if(val == subTexture)
				return keys[i];
			i++;
		}
		return "Not found";
	}
	
	public Texture get(String name){
		if(customMap.containsKey(name))
			return subTextures[customMap.get(name)];
		return null;
	}
	
	private void initSubTextures(){
		subTextures = new Texture[per_width * per_height];
		for(int i=0;i<subTextures.length; i++)
			subTextures[i] = createSubTexture(i);
	}
	
	private Texture createSubTexture(int index){
		return new Texture(this,index);
	}
	
	public Texture getSubTexture(int index){
		return subTextures[index];
	}
	
	public Texture getSubTexture(int x, int y){
		return getSubTexture(x + (y * per_width));
	}
	
	public int getSubWidth(){
		return sub_width;
	}
	
	public int getSubHeight(){
		return sub_height;
	}
	
	public int getPerWidth(){
		return per_width;
	}
	
	public Vector2f getSubSize(){
		return new Vector2f(sub_width,sub_height);
	}
	
	public int getPerHeight(){
		return per_height;
	}
	
	public int getWidth(){
		return src.getWidth();
	}
	
	public int getHeight(){
		return src.getHeight();
	}
	
	public int getLength(){
		return per_width * per_height;
	}
	
	public Texture getTexture(){
		return src;
	}
	
	public int getIndexAt(Vector2f point){
		return getIndexAt((int)point.x,(int)point.y);
	}
	
	public int getIndexAt(int point_x, int point_y){
		return 0;
	}
	
	public static HashMap<String,TextureSheet> getSheets(){
		return sheets;
	}
	
	public static class STMapping implements Comparable<STMapping>{
		String name;
		int id;
		
		public STMapping(String name, int id){
			this.name = name;
			this.id = id;
		}
		
		public int compare(int id1, int id2){
			return Integer.compare(id1, id2);
		}

		@Override
		public int compareTo(STMapping o) {
			return Integer.compare(id,o.id);
		}
	}
}
