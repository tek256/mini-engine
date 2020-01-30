package io.tek256;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
    public static final int BUTTON_1 = GLFW_MOUSE_BUTTON_1;
    public static final int BUTTON_2 = GLFW_MOUSE_BUTTON_2;
    public static final int BUTTON_3 = GLFW_MOUSE_BUTTON_3;
    public static final int BUTTON_4 = GLFW_MOUSE_BUTTON_4;
    public static final int BUTTON_5 = GLFW_MOUSE_BUTTON_5;
    public static final int BUTTON_6 = GLFW_MOUSE_BUTTON_6;
    public static final int BUTTON_7 = GLFW_MOUSE_BUTTON_7;
    public static final int BUTTON_8 = GLFW_MOUSE_BUTTON_8;
    public static final int BUTTON_LEFT   = GLFW_MOUSE_BUTTON_LEFT;
    public static final int BUTTON_RIGHT  = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;
    public static final int BUTTON_LAST = GLFW_MOUSE_BUTTON_LAST;
	
	private static boolean entered = false,grabbed = false;
	private static Vector2d position,previous,d;
	private static ArrayList<Integer> events;
	private static ArrayList<Integer> eventsThisFrame;
	private static ArrayList<Integer> eventsLastFrame;

	public Mouse(){
		events = new ArrayList<>();
		eventsThisFrame = new ArrayList<>();
		eventsLastFrame = new ArrayList<>();
		position = new Vector2d(0,0);
		previous = new Vector2d();
		d = new Vector2d();
	}
	
	public static void update(){
		eventsLastFrame.clear();
		eventsLastFrame.addAll(eventsThisFrame);
		eventsThisFrame.clear();
		eventsThisFrame.addAll(events);
		
	}
	
	public static boolean isPressed(int button){
		return events.contains((Integer)button);
	}
	
	public static boolean isReleased(int button){
		return !isPressed(button);
	}
	
	public static boolean isClicked(int button){
		if(eventsThisFrame.contains(button) && !eventsLastFrame.contains(button))
			return true;
		return false;
	}
	
	public static boolean isMouseEntered(){
		return entered;
	}
	
	public static boolean isGrabbed(){
		return grabbed;
	}
	
	public static void setGrabbed(boolean grabbed){
		if(Mouse.grabbed == grabbed)
			return;
		Mouse.grabbed = grabbed;
		Window.getCurrent().setInputMode(GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
	}
	
	public static float getX(){
		return (float)position.x;
	}
	
	public static float getY(){
		return (float)position.y;
	}
	
	public static Vector2d getPosition(){
		return position;
	}
	
	public static float getDX(){
		float df = (float)d.x;
		d.x = 0;
		return df;
	}
	
	public static float getDY(){
		float df = (float)d.y;
		d.y = 0;
		return df;
	}
	
	public static Vector2d getDelta(){
		Vector2d df = d;
		d = new Vector2d();
		return df;
	}
	
	public static ArrayList<Integer> getButtonEvents(){
		return events;
	}
	
	public static ArrayList<Integer> getButtonEventsThisFrame(){
		return eventsThisFrame;
	}
	
	protected static void setCursorEntered(boolean enter){
		entered = enter;
	}
	
	protected static void setButton(int button, boolean pressed){
		if(pressed && !events.contains(button))
			events.add(button);
		else if(!pressed && events.contains(button))
			events.remove((Integer)button);
	}
	
	protected static void setPosition(double x, double y){
		previous.x = position.x;
		previous.y = position.y;
		position.x = x;
		position.y = y;
		d = new Vector2d(x - previous.x, y - previous.y);
	}
}
