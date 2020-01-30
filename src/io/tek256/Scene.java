package io.tek256;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import io.tek256.render.Camera;
import io.tek256.render.GLContext;
import io.tek256.render.GUIElement;
import io.tek256.render.Mesh;
import io.tek256.render.PointLight;
import io.tek256.render.Shader;
import io.tek256.render.ShaderProgram;
import io.tek256.render.ShadowMap;

import static org.lwjgl.opengl.GL13.*;


public class Scene {
	private static Scene current;
	
	public static Scene getCurrent(){
		return current;
	}
	
	public Map<Mesh,List<GameObject>> meshList;
	public ArrayList<GameObject> gameObjects;
	
	private ArrayList<GUIElement> guiElements;
	
	private static int MAX_POINT_LIGHTS = 5;
	
	private static Camera currentCamera;
	
	private Vector3f ambientLight,skyboxLight;
	private ArrayList<PointLight> pointLights;
	private ShaderProgram sceneShader;
	private ShaderProgram depthShader;
	private ShaderProgram simpleShader;
	private ShaderProgram billboardShader;
	private ShaderProgram guiShader;
	private float specularPower = 10f;
	private ShadowMap shadowMap;
	
	public Scene(){
		meshList = new HashMap<Mesh,List<GameObject>>();
		init();
	}
	
	private void init(){
		if(ambientLight == null)
			ambientLight = new Vector3f(0.5f,0.5f,0.5f);
		if(skyboxLight == null)
			skyboxLight = new Vector3f(1f,1f,1f);
		shadowMap = new ShadowMap();
		sceneShader = new ShaderProgram(new Shader("res/shaders/default.vs",Shader.Type.VERTEX_SHADER),
				new Shader("res/shaders/default.fs",Shader.Type.FRAGMENT_SHADER));
		sceneShader.getUniformLocation("projectionMatrix");
		sceneShader.getUniformLocation("modelViewMatrix");
		sceneShader.getUniformLocation("material");
		sceneShader.getUniformLocation("camera_position");
		sceneShader.getUniformLocation("specularPower");
		sceneShader.getUniformLocation("ambientLight");
		sceneShader.setupPointLightUniforms("pointLights", MAX_POINT_LIGHTS);
		sceneShader.createMaterialUniform("material");
		sceneShader.getUniformLocation("shadowMap");
		sceneShader.getUniformLocation("orthoProjectionMatrix");
		sceneShader.getUniformLocation("modelLightViewMatrix");
		
		depthShader = new ShaderProgram(new Shader("res/shaders/depth.vs",Shader.Type.VERTEX_SHADER),
				new Shader("res/shaders/depth.fs",Shader.Type.FRAGMENT_SHADER));
		depthShader.getUniformLocation("modelLightViewMatrix");
		depthShader.getUniformLocation("orthoProjectionMatrix");
		
		simpleShader = new ShaderProgram(new Shader("res/shaders/simple.vs",Shader.Type.VERTEX_SHADER),
				new Shader("res/shaders/simple.fs",Shader.Type.FRAGMENT_SHADER));
		simpleShader.getUniformLocation("modelViewMatrix");
		simpleShader.getUniformLocation("projectionMatrix");
		simpleShader.getUniformLocation("ambientLight");
		
		guiShader = new ShaderProgram(new Shader("res/shaders/gui.vs", Shader.Type.VERTEX_SHADER),
				new Shader("res/shaders/gui.fs",Shader.Type.FRAGMENT_SHADER));
		guiShader.getUniformLocation("projectionModelMatrix");
		guiShader.getUniformLocation("color");
		guiShader.getUniformLocation("hasTexture");
		currentCamera = new Camera();
		//float fov, float width, float height, float near, float far
		currentCamera.getProjection(60f, Window.getWidth(), Window.getHeight(), 0.01f, 100f);
		gameObjects = new ArrayList<GameObject>();
		guiElements = new ArrayList<GUIElement>();
		current = this;
	}
	
	public void resized(){
		for(GUIElement e: guiElements)
			e.align(Window.getWidth(), Window.getHeight());
	}
	
	public void input(){
		for(GameObject gameObject : gameObjects){
			gameObject.handleInput();
		}
		
		Vector2d m = Mouse.getPosition();
		for(GUIElement e : guiElements){
			if(e.contains((float)m.x,(float)m.y)){
				e.handle(Mouse.getButtonEvents());
				if(Mouse.isPressed(Mouse.BUTTON_LEFT))
					System.out.println(e.getWidth() + "," + e.getHeight());
			}
		}
	}
	
	public void update(long delta){
		for(GameObject gameObject : gameObjects){
			gameObject.update(delta);
		}
	}
	
	public void clear(){
		if(meshList != null)
			for(Mesh m : meshList.keySet())
				m.destroy();
		if(guiElements != null)
			for(GUIElement e : guiElements)
				e.destroy();
		if(gameObjects == null)
			gameObjects = new ArrayList<GameObject>();
		
		gameObjects.clear();
		meshList = new HashMap<Mesh,List<GameObject>>();
		guiElements = new ArrayList<GUIElement>();
	}
	
	public void render(){
		//gui
		renderGUI();
		
		//regular rendering
		simpleShader.bind();
		Matrix4f projection = currentCamera.getProjectionMatrix();
		currentCamera.updateView();
		Matrix4f view = currentCamera.getView();
		simpleShader.setUniform("projectionMatrix", projection);
		simpleShader.setUniform("ambientLight", ambientLight);
		for(Mesh mesh: meshList.keySet()){
			for(GameObject gameObject : meshList.get(mesh)){
				if(gameObject.isRenderable()){
					Matrix4f modelViewMatrix = currentCamera.getModelViewMatrix(gameObject, view);
					simpleShader.setUniform("modelViewMatrix", modelViewMatrix);
					if(gameObject.hasOwnMaterial()){
						mesh.render(gameObject.getMaterial());
					}else{
						mesh.render();
					}
				}
			}
		}
		
		ShaderProgram.unbind();
	}
	
	private void renderGUI(){
		guiShader.bind();
		
		Matrix4f ortho = Camera.getCurrent().getOrtho2DProjection(0, Window.getWidth(), Window.getHeight(), 0);
		for(GUIElement guiElement : guiElements){
			if(guiElement.allowRender()){
				Mesh m = guiElement.getMesh();
				Matrix4f projectionModelMatrix = Camera.getCurrent().getOrthoProjectionModel(guiElement, ortho);
				guiShader.setUniform("projectionModelMatrix", projectionModelMatrix);
				guiShader.setUniform("color", m.getMaterial().getColor());
				guiShader.setUniform("hasTexture", m.getMaterial().hasTexture() ? 1 : 0);
				m.render();
			}
		}
		
		ShaderProgram.unbind();
	}
	
	public void setGameObjects(GameObject...gameObjects){
		int numobjects = gameObjects != null ? gameObjects.length : 0;
		for(int i=0;i<numobjects;i++){
			GameObject g = gameObjects[i];
			Mesh m = g.getMesh();
			List<GameObject> l = meshList.get(m);
			if(l == null){
				l = new ArrayList<>();
				meshList.put(m, l);
			}
			l.add(g);
		}
	}
	
	public void addGameObject(GameObject gameObject){
		if(gameObject.getMesh() != null)
			addRenderObject(gameObject);
		gameObjects.add(gameObject);
	}
	
	public void add(GameObject...gameObjects){
		for(GameObject go : gameObjects){
			if(go.getMesh() != null)
				addRenderObject(go);
			this.gameObjects.add(go);
		}
	}
	
	public void add(GameObject gameObject){
		if(gameObject.getMesh() != null)
			addRenderObject(gameObject);
		gameObjects.add(gameObject);
	}
	
	public void add(GUIElement...elements){
		for(GUIElement element : elements)
			guiElements.add(element);
	}
	
	public void add(ArrayList<GameObject> gameObjects){
		for(GameObject gameObject: gameObjects)
			add(gameObject);
	}
	
	public void add(List<GUIElement> elements){
		for(GUIElement e: guiElements)
			add(e);
	}
	
 	public void add(GUIElement e){
		guiElements.add(e);
	}
	
	public void remove(ArrayList<GUIElement> elements){
		for(GUIElement e: elements)
			if(guiElements.contains(e))
				guiElements.remove(e);
	}
	
	public void remove(GUIElement...elements){
		for(GUIElement e: elements){
			if(guiElements.contains(e))
				guiElements.remove(e);
		}
	}
	
	public void remove(GUIElement element){
		if(guiElements.contains(element))
			guiElements.remove(element);
	}
	
	public void remove(GameObject...gameObjects){
		for(GameObject gameObject: gameObjects)
			remove(gameObject);
	}
	
	public void remove(GameObject gameObject){
		if(gameObjects.contains(gameObject))
			gameObjects.remove(gameObject);
		if(gameObject.getMesh() != null)
			removeRenderObject(gameObject);
	}
	
	public void addRenderObject(GameObject gameObject){
		if(meshList.containsKey(gameObject.getMesh())){
			meshList.get(gameObject.getMesh()).add(gameObject);
		}else{
			ArrayList<GameObject> newlist = new ArrayList<GameObject>();
			newlist.add(gameObject);
			meshList.put(gameObject.getMesh(),newlist);
		}
	}
	
	public void removeRenderObject(GameObject gameObject){
		if(meshList.containsKey(gameObject.getMesh()))
				meshList.get(gameObject.getMesh()).remove(gameObject);
	}
	
	public void removeGameObject(GameObject gameObject){
		if(gameObject.getMesh() != null)
			removeRenderObject(gameObject);
		gameObjects.remove(gameObject);
	}
	
	public void removeGameObject(GameObject gameObject, boolean removeRender){
		if(gameObject.getMesh() != null && removeRender)
			removeRenderObject(gameObject);
		gameObjects.remove(gameObject);
	}
	
	public void addGUIElement(GUIElement guiElement){
		guiElements.add(guiElement);
	}
	
	public void removeGUIElement(GUIElement guiElement){
		guiElements.remove(guiElement);
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
	
	public void destroy(){
		for(Mesh m: meshList.keySet())
			m.destroy();
		for(GUIElement e: guiElements)
			e.destroy();
	}
}
