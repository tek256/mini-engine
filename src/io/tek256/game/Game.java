package io.tek256.game;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.tek256.GameObject;
import io.tek256.Keyboard;
import io.tek256.Mouse;
import io.tek256.Scene;
import io.tek256.Timer;
import io.tek256.Keyboard.Button;
import io.tek256.Util;
import io.tek256.Window;
import io.tek256.render.EventHandler;
import io.tek256.render.FontTexture;
import io.tek256.render.GUIElement;
import io.tek256.render.Material;
import io.tek256.render.Mesh;
import io.tek256.render.Texture;

public class Game implements GameInterface{
	private Player player;
	
	private GUIElement fpscounter,upscounter;
	private GUIElement testtext,tex,menutext;
	
	private ArrayList<GameObject> cubes;
	
	@Override
	public void start() {
		System.out.println("game has started");
		setupButtons();
		player = new Player();
		Mesh m = Util.getMesh("res/models/cube.obj");
		Material material = new Material(new Texture("res/textures/rock.png"));
		m.setMaterial(material);
		
		createCubes(new Vector3f[]{
			new Vector3f(5f,0,0),	
			new Vector3f(0,5f,0),	
			new Vector3f(-5f,0,0),	
			new Vector3f(0,-5f,0),	
			new Vector3f(0,0,5f),	
			new Vector3f(0,0,-5f),	
		}, m);
		
		Scene.getCurrent().gameObjects.get(0).setMaterial(new Material(Texture.getTexture("res/textures/testarrows.png")));
		
		//gui test
		FontTexture.defaultFont = new FontTexture(new Font("04b03",Font.PLAIN,16),"ISO-8859-1");
		fpscounter = new GUIElement("FPS: 0");
		fpscounter.setPosition(new Vector3f(10,10,0));
		upscounter = new GUIElement("UPS: 0");
		upscounter.setPosition(new Vector3f(80,10,0));
		testtext = new GUIElement("DEMO");
		testtext.setPosition(new Vector3f(100,100,0));
		testtext.setScale(new Vector3f(2f,2f,1f));
		testtext.setAnchor(GUIElement.Anchor.BOTTOM_CENTER);
		testtext.setColor(0.9f,0.9f,0.9f);
		tex = new GUIElement(Texture.getTexture("res/textures/testarrows.png"));
		tex.setScale(new Vector3f(0.1f,0.1f,1f));
		tex.setPosition(new Vector3f(100,100,0));
		
		EventHandler eventHandle = new EventHandler();
		eventHandle.addAction(eventHandle.createAction("0",tex,"test"));
		tex.setEventHandler(eventHandle);
		menutext = new GUIElement("MENU");
		menutext.setAnchor(GUIElement.Anchor.TOP_CENTER);
		menutext.setRenderable(false);
		menutext.setPadding(new Vector2f(0,100f));
		Scene.getCurrent().add(menutext);
		Scene.getCurrent().add(tex);
		Scene.getCurrent().add(fpscounter);
		Scene.getCurrent().add(upscounter);
		Scene.getCurrent().add(testtext);
	}
	
	private void createCubes(Vector3f[] cubePositions, Mesh mesh){
		cubes = new ArrayList<GameObject>();
		for(int i=0;i<cubePositions.length;i++){
			GameObject gameObject = new GameObject(cubePositions[i]);
			gameObject.setMesh(mesh);
			cubes.add(gameObject);
		}
	
		Scene.getCurrent().add(cubes);
	}
	
	private void setupButtons(){
		Button horizontal = new Button(Keyboard.KEY_D, Keyboard.KEY_A, Keyboard.KEY_RIGHT, Keyboard.KEY_LEFT);
		Button vertical = new Button(Keyboard.KEY_W, Keyboard.KEY_S, Keyboard.KEY_UP, Keyboard.KEY_DOWN);
		Button sprint = new Button(Keyboard.KEY_LEFT_SHIFT, -1, Keyboard.KEY_RIGHT_SHIFT, -1);
		Keyboard.addButton("horizontal", horizontal);
		Keyboard.addButton("vertical", vertical);
		Keyboard.addButton("sprint", sprint);
	}

	Random r = new Random(new Date().getTime());
	private void generateScene(){
		Scene.getCurrent().clear();
		Mesh m = Util.getMesh("res/models/cube.obj");
		Material material = new Material(new Texture("res/textures/rock.png"));
		m.setMaterial(material);
		createCubes(new Vector3f[]{
			new Vector3f(getRandom(0,5f),getRandom(-5f,5f),getRandom(-5f,5f)),	
			new Vector3f(getRandom(-5f,5f),getRandom(-5f,5f),getRandom(-5f,5f)),	
			new Vector3f(getRandom(-5f,5f),getRandom(-5f,5f),getRandom(-5f,5f)),	
			new Vector3f(getRandom(-5f,5f),getRandom(-5f,5f),getRandom(-5f,5f)),	
			new Vector3f(getRandom(-5f,5f),getRandom(-5f,5f),getRandom(-5f,5f)),	
			new Vector3f(getRandom(-5f,5f),getRandom(-5f,5f),getRandom(-5f,5f)),	
		},m);
	}
	
	private float getRandom(float min, float max){
		return min + ((max - min) * r.nextFloat());
	}
	
	@Override
	public void input() {
		player.input();
		
		if(Mouse.isClicked(0)){
			Mouse.setGrabbed(true);
		}
		
		if(Keyboard.isClicked(Keyboard.KEY_ESCAPE))
			Mouse.setGrabbed(false);
		
		if(Keyboard.isClicked(Keyboard.KEY_I))
			generateScene();
		
		if(Keyboard.isPressed(Keyboard.KEY_TAB))
			menutext.setRenderable(true);
		else
			menutext.setRenderable(false);
	}

	@Override
	public void update(float delta) {
		player.update(delta);
		upscounter.setText("Updates: "+Timer.getUPS());
		fpscounter.setText("FPS: "+Timer.getFPS());
		
		if(Window.getCurrent().isResized())
			Scene.getCurrent().resized();
	}

	@Override
	public void render() {
		
	}

	@Override
	public void end() {
		System.out.println("Goodbye!");
	}
		
}
