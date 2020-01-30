package io.tek256.runtime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.tek256.Util;
import io.tek256.Window;
import io.tek256.collision.Collision;
import io.tek256.game.Player;
import io.tek256.game.Script;
import io.tek256.render.Camera;
import io.tek256.render.Material;
import io.tek256.render.Mesh;
import io.tek256.render.ShaderProgram.Shader;
import io.tek256.render.ShaderProgram;
import io.tek256.render.Texture;
import io.tek256.render.TextureSheet.STMapping;

public class Scene {
	private static Scene current;
	
	public static Scene getCurrent(){
		return current;
	}
	
	private SceneType type;
	private static boolean shadersSetup = false;
	
	public ArrayList<GameObject> gameObjects;
	
	public ArrayList<LevelTexture> levelTextures;
	
	private ArrayList<Script> scripts;
	
	private static Camera currentCamera;
	
	private Vector3f ambientLight,skyboxLight;
	private static ShaderProgram simpleShader;
	private static ShaderProgram guiShader;
	
	private  boolean isGenerated = false;
	protected long seed = 0L;
	
	public Scene(){
		type = SceneType.DEFAULT;
		init();
	}
	
	public Scene(SceneType type){
		this.type = type;
		init();
	}
	
	public Scene(String path){
		type = SceneType.DEFAULT;
	}
	
	public void write(String path){
		//iterate through mesh lists
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject root = new JsonObject();
			root.addProperty("name", "untitled");
			root.addProperty("type", "level");
			if(isGenerated())
				root.addProperty("seed", seed);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public boolean isGenerated(){
		return isGenerated;
	}
	
	private void init(){
		if(ambientLight == null)
			ambientLight = new Vector3f(1f,1f,1f);
		if(skyboxLight == null)
			skyboxLight = new Vector3f(1f,1f,1f);
		
		if(!shadersSetup){
			setupShaders();
		}
		
		if(currentCamera == null){
			currentCamera = new Camera();
			currentCamera.getOrtho2DProjection(0, 180, 120, 0);
		}
		gameObjects = new ArrayList<GameObject>();
		
		levelTextures = new ArrayList<LevelTexture>();
		
		scripts = new ArrayList<Script>();
		
		current = this;
	}
	
	private void setupShaders(){
		if(type != SceneType.ORTHO){
			simpleShader = new ShaderProgram(new Shader("res/shaders/simple.vs",Shader.Type.VERTEX_SHADER),
					new Shader("res/shaders/simple.fs",Shader.Type.FRAGMENT_SHADER));
		}else{
			simpleShader = new ShaderProgram(new Shader("res/shaders/2d.vs", Shader.Type.VERTEX_SHADER),
					new Shader("res/shaders/2d.fs", Shader.Type.FRAGMENT_SHADER));
		}
		simpleShader.getUniformLocation("modelViewMatrix");
		simpleShader.getUniformLocation("projectionMatrix");
		simpleShader.getUniformLocation("ambientLight");
		simpleShader.getUniformLocation("texRepeat");
		simpleShader.getUniformLocation("hasSubOffset");
		simpleShader.getUniformLocation("subOffset");
		simpleShader.getUniformLocation("subSize");
		simpleShader.getUniformLocation("texSize");
		simpleShader.getUniformLocation("hasTex");
		simpleShader.getUniformLocation("color");
		
		
		guiShader = new ShaderProgram(new Shader("res/shaders/gui.vs", Shader.Type.VERTEX_SHADER),
				new Shader("res/shaders/gui.fs",Shader.Type.FRAGMENT_SHADER));
		guiShader.getUniformLocation("projectionModelMatrix");
		guiShader.getUniformLocation("color");
		guiShader.getUniformLocation("hasTexture");
		guiShader.getUniformLocation("hasSubOffset");
		guiShader.getUniformLocation("subSize");
		guiShader.getUniformLocation("subOffset");
		guiShader.getUniformLocation("texSize");
		
		shadersSetup = true;
	}
	
	public void input(float delta){
		Player.getInstance().input(delta);
		for(GameObject gameObject : gameObjects){
			gameObject.handleInput(delta);
		}
	}
	
	public void update(float delta){
		Player.getInstance().update(delta);
		if(gameObjects.size() > 0)
			for(GameObject gameObject : gameObjects)
				gameObject.update(delta);
		if(scripts.size() > 0)
			for(Script script : scripts)
				script.update(delta);
	}
	
	public void clear(){
		if(gameObjects == null)
			gameObjects = new ArrayList<GameObject>();
		
		gameObjects.clear();
	}
	
	public void render(){
	 if(type == SceneType.ORTHO && gameObjects.size() > 0){
			render2D();
		}
	}
	
	private void render2D(){
		simpleShader.bind();
		Matrix4f projection = currentCamera.getOrtho2D();
		Matrix4f view = currentCamera.updateView();
		simpleShader.setUniform("projectionMatrix", projection);
		simpleShader.setUniform("ambientLight", ambientLight);
		
		for(GameObject gameObject : gameObjects){
			if(gameObject.renderable){
				simpleShader.setUniform("modelViewMatrix", currentCamera.getModelViewMatrix2D(gameObject, view));
				simpleShader.setUniform("color", gameObject.getMaterial().getColor());
				if(gameObject.getMaterial().hasTexture()){
					simpleShader.setUniform("hasTex", 1);
					simpleShader.setUniform("texRepeat",gameObject.getMaterial().repeat);
					
					if(gameObject.getMaterial().getTexture().isSubTexture()){
						Texture t = gameObject.getMaterial().getTexture();
						
						simpleShader.setUniform("hasSubOffset", 1);
						simpleShader.setUniform("subSize", t.getSize());
						simpleShader.setUniform("subOffset", t.getOffset());
						simpleShader.setUniform("texSize", t.getSheet().getTexture().getSize());
					}else{
						simpleShader.setUniform("hasSubOffset", 0);
					}
				}else{
					simpleShader.setUniform("hasSubOffset", 0);
					simpleShader.setUniform("hasTex", 0);
				}
				Mesh.QUAD.render(gameObject.getMaterial());
			}
		}
		
		for(LevelTexture ltex : levelTextures){
			simpleShader.setUniform("modelViewMatrix", currentCamera.getModelViewMatrix2D(ltex, view));
			simpleShader.setUniform("color", ltex.color);
			if(ltex.texture != null){
				simpleShader.setUniform("hasTex", 1);
				simpleShader.setUniform("texRepeat", ltex.texRepeat);
				
				if(ltex.texture.isSubTexture()){
					simpleShader.setUniform("hasSubOffset", 1);
					simpleShader.setUniform("subSize", ltex.texture.getSize());
					simpleShader.setUniform("subOffset", ltex.texture.getOffset());
					simpleShader.setUniform("texSize", ltex.texture.getSheet().getTexture().getSize());
				}else{
					simpleShader.setUniform("hasSubOffset", 0);
				}
				
			}else{
				simpleShader.setUniform("hasSubOffset", 0);
				simpleShader.setUniform("hasTex", 0);
			}
			Mesh.QUAD.render(ltex.texture);
		}
		
		Material material = Player.getInstance().material;
		Matrix4f modelView = currentCamera.getModelViewMatrix(Player.getInstance(), view);
		simpleShader.setUniform("modelViewMatrix", modelView);
		simpleShader.setUniform("color", material.getColor());
		if(material.hasTexture()){
			simpleShader.setUniform("hasTex", 1);
			simpleShader.setUniform("texRepeat",material.repeat);
			
			if(material.getTexture().isSubTexture()){
				Texture t = material.getTexture();
				
				simpleShader.setUniform("hasSubOffset", 1);
				simpleShader.setUniform("subSize", t.getSize());
				simpleShader.setUniform("subOffset", t.getOffset());
				simpleShader.setUniform("texSize", t.getSheet().getTexture().getSize());
			}else{
				simpleShader.setUniform("hasSubOffset", 0);
			}
		}else{
			simpleShader.setUniform("hasSubOffset", 0);
			simpleShader.setUniform("hasTex", 0);
		}
		Mesh.QUAD.render(material);
		
		ShaderProgram.unbind();
	}
	
	public void addGameObject(GameObject gameObject){
		if(!gameObjects.contains(gameObject))
			gameObjects.add(gameObject);
	}
	
	public void add(GameObject...gameObjects){
		for(GameObject go : gameObjects){
			this.gameObjects.add(go);
		}
	}
	
	public void add(GameObject gameObject){
		gameObjects.add(gameObject);
	}
	
	public void add(ArrayList<GameObject> gameObjects){
		for(GameObject gameObject: gameObjects)
			add(gameObject);
	}
	
 	public void add(Script script){
 		scripts.add(script);
 	}
 	
 	public void add(LevelTexture levelTexture){
 		levelTextures.add(levelTexture);
 	}
 	
 	public void remove(LevelTexture levelTexture){
 		if(levelTextures.contains(levelTexture)){
 			levelTextures.remove(levelTexture);
 		}
 	}
 	
 	public void remove(Script script){
 		if(scripts.contains(script))
 			scripts.remove(script);
 	}
 	
 	public void removeAllOf(Script script){
 		Iterator<Script> iter = scripts.iterator();
 		while(iter.hasNext()){
 			Script n = iter.next();
 			if(n.getClass().equals(script.getClass()))
 				scripts.remove(n);
 		}
 	}
	
	
	public void remove(GameObject...gameObjects){
		for(GameObject gameObject: gameObjects)
			remove(gameObject);
	}
	
	public void remove(GameObject gameObject){
		if(gameObjects.contains(gameObject))
			gameObjects.remove(gameObject);
	}
	
	public void removeGameObject(GameObject gameObject){
		gameObjects.remove(gameObject);
	}
	
	public ArrayList<GameObject> getGameObjects(){
		return gameObjects;
	}
	
	public void setAmbientLight(Vector3f color){
		ambientLight = color;
	}
	
	public Vector3f getAmbientLight(){
		return ambientLight;
	}
	
	public void setSkyboxLight(Vector3f color){
		skyboxLight = color;
	}
	
	public Vector3f getSkyboxLight(){
		return skyboxLight;
	}
	
	/*
	public void addPointLight(PointLight light){
		if(pointLights == null)
			pointLights = new ArrayList<PointLight>();
		pointLights.add(light);
	}
	
	public void removePointLight(PointLight light){
		pointLights.remove(light);
	}
	
	public void removePointLight(int index){
		pointLights.remove(index);
	}
	
	public PointLight[] getPointLightsAsArray(){
		PointLight[] pointLightArray = new PointLight[pointLights.size()];
		for(int i=0;i<pointLights.size();i++)
			pointLightArray[i] = pointLights.get(i);
		return pointLightArray;
	}
	
	public ArrayList<PointLight> getPointLights(){
		return pointLights;
	}
	*/
	
	public SceneType getSceneType(){
		return type;
	}
	
	public boolean isDefault(){
		return type == SceneType.DEFAULT;
	}
	
	public boolean isMenu(){
		return type == SceneType.MENU;
	}
	
	public void destroy(){
		scripts.clear();
	}
	
	public static enum SceneType{
		DEFAULT,
		MENU,
		ORTHO,
	}
}
