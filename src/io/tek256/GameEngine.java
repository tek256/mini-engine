package io.tek256;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import io.tek256.game.GameInterface;
import io.tek256.render.Camera;
import io.tek256.render.GLContext;
import io.tek256.render.Material;
import io.tek256.render.Mesh;
import io.tek256.render.PointLight;
import io.tek256.render.Texture;

public class GameEngine implements Runnable{
	public static int TARGET_FPS = 60;
	public static int TARGET_UPS = 30;
	
	private int curframes = 0;
	private int curupdates = 0;
	
	private Thread thread;
	private Timer timer;
	private Mouse mouse;
	private Keyboard keyboard;
	private Window window;
	private Scene scene;
	
	private GameInterface game;
	
	private GameObject gameObject,gameObject2,gameObject3;
	
	public GameEngine(GameInterface game){
		this.game = game;
		thread = new Thread(this,"game_main");
	}
	
	public void start(){
		thread.start();
	}
	
	@Override
	public void run(){
		init();
		float time = 0;
		float delta;
		float updateNS = 1 / TARGET_UPS;
		float reset = 0;
		boolean running = true;
		while(running){
			delta = timer.getDelta();
			time = delta;
			reset += delta;
			if(reset >= 1){
				timer.setFPS(curframes);
				timer.setUPS(curupdates);
				curupdates = 0;
				curframes = 0;
				reset = 0;
			}
			
			if(time >= updateNS){
				input();
				update(updateNS);
				time -= updateNS;
				curupdates ++;
			}
			render();
			curframes ++;
			
			if(!window.isVSync()){
				sync();
			}
			
			if(window.isCloseRequested())
				running = false;
		}
	}
	
	public void init(){
		timer = new Timer();
		window = new Window(720,480);
		mouse = new Mouse();
		keyboard = new Keyboard();
		scene = new Scene();
		
		game.start();
	}
	
	public void input(){
		window.pollInput();
		Mouse.update();
		Keyboard.update();
		scene.input();
		game.input();
		
		//grab cursor
		
	}	
	
	public void update(float delta){
		game.update(delta);
	}
	
	public void render(){
		window.clearColorAndDepth();
		scene.render();
		game.render();
		window.swapBuffers();
	}
	
	public void sync(){
		float frameSlot = 1f / TARGET_FPS;
		float end = timer.getLastUpdate() + frameSlot;
		while(timer.getTime() < end){
			sleep(1);
		}
	}

	public void sleep(float ms){
		try{
			Thread.sleep(1);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
