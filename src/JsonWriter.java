import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.tek256.Util;
import io.tek256.runtime.GameObject;
import io.tek256.runtime.Transform;

public class JsonWriter {
	public static void main(String[] args){
		//WE WRITING JSON FILE FOR SPRITESHEET
		System.out.println("what");
		Scanner in = new Scanner(System.in);
		String line;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonObject root = new JsonObject();
		root.addProperty("name", "objects");
		root.addProperty("type", "spritesheet");
		root.addProperty("path", "res/textures/o.png");
		root.addProperty("width", "8");
		root.addProperty("height", "8");
		JsonArray jMappings = new JsonArray();
		while((line = in.nextLine()) != null){
			String[] mappings = line.split(" ");
			int i = 0;
			for(String mapping : mappings){
				JsonObject m = new JsonObject();
				m.addProperty("id", i);
				m.addProperty("name", mapping);
				i++;
			}
		}
		root.add("mappings", jMappings);
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter("o.sheet"));
			out.write(gson.toJson(root));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * try{
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
	 */
}
