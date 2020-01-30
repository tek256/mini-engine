package io.tek256;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import io.tek256.render.Camera;
import io.tek256.render.GLContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Color;
import java.nio.DoubleBuffer;

public class Window {
	public static final int DEFAULT_WIDTH = 720, DEFAULT_HEIGHT = 480;
	private static Window current = null;
	private long handle;
	private String title;
	private int width,height;
	private int x,y;
	private boolean visible = false, resized = false, vsync = true,
			fullscreen = false,iconified = false;
	private GLCapabilities glCapabilities;
	private GLFWVidMode vidMode;
	private GLFWErrorCallback errorCallback;
	private GLFWWindowSizeCallback sizeCallback;
	private GLFWWindowPosCallback posCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWCursorPosCallback mousePosCallback;
	private GLFWCursorEnterCallback mouseEnterCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	
	public Window(){
		this(DEFAULT_WIDTH,DEFAULT_HEIGHT, "Beyond the Void", true);
	}
	
	public Window(int width, int height){
		this(width, height, "Beyond the Void", true);
	}
	
	public Window(int width, int height, String title){
		this(width, height, title, true);
	}
	
	public Window(int width, int height, String title, boolean vsync){
		this.width = width;
		this.height = height;
		this.title = title;
		this.vsync = vsync;
		init();
	}
	
	protected void init(){
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		
		if(glfwInit() != GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints();
		setHint(GLFW_VISIBLE, false);
		setHint(GLFW_RESIZABLE, true);
		
		handle = glfwCreateWindow(width, height, title, 0L, 0L);
		if(handle == 0L)
			throw new RuntimeException("Unable to create window");
		
		glfwSetWindowSizeCallback(handle, sizeCallback = new GLFWWindowSizeCallback(){
			@Override
			public void invoke(long handle, int width, int height) {
				Window.this.width = width;
				Window.this.height = height;
				Window.this.resized = true;
			}
		});
		
		glfwSetWindowPosCallback(handle, posCallback = new GLFWWindowPosCallback(){
			@Override
			public void invoke(long handle, int x, int y) {
				Window.this.x = x;
				Window.this.y = y;
				Window.this.resized = true;
			}
		});
		
		glfwSetKeyCallback(handle, keyCallback = new GLFWKeyCallback(){
			@Override
			public void invoke(long handle, int key, int scancode, int action, int mods) {
				Keyboard.setKey(key, action == GLFW_PRESS ? true : (action == GLFW_REPEAT) ? true : false);
			}
		});
		
		glfwSetCursorPosCallback(handle, mousePosCallback = new GLFWCursorPosCallback(){
			@Override
			public void invoke(long handle, double x, double y){
				Mouse.setPosition(x,y);
			}
		});
		
		glfwSetMouseButtonCallback(handle, mouseButtonCallback = new GLFWMouseButtonCallback(){
			@Override
			public void invoke(long handle, int button, int action, int mods) {
				Mouse.setButton(button, action == GLFW_PRESS ? true : false);
			}
		});
		
		glfwSetCursorEnterCallback(handle, mouseEnterCallback = new GLFWCursorEnterCallback(){
			@Override
			public void invoke(long handle, int entered) {
				Mouse.setCursorEntered(entered == 1);
			}
		});
		
		center();
		current = this;
		glfwMakeContextCurrent(handle);
		if(isVSync()){
			glfwSwapInterval(1);
		}
				
		show();
		glCapabilities = GL.createCapabilities();
		setClearColor(0.2f,0.2f,0.2f,1f);
		GLContext.viewport(0, 0, width, height);
		GLContext.enable(GL11.GL_BLEND);
		GLContext.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GLContext.enable(GL11.GL_CULL_FACE);
		GLContext.cullFace(GL11.GL_BACK);
		GLContext.enable(GL11.GL_DEPTH_TEST);
		GLContext.depthFunc(GL11.GL_LEQUAL);
		//TODO USELESS
	}
	
	public static Window getCurrent(){
		return current;
	}
	
	public void pollInput(){
		glfwPollEvents();
	}
	
	public void clear(int buffers){
		glClear(buffers);
	}
	
	public void clearColorAndDepth(){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void swapBuffers(){
		glfwSwapBuffers(handle);
		if(resized){
			glViewport(0,0,width,height);
			resized = false;
			Camera.getCurrent().updateProjection();
		}
	}
	
	public long getHandle(){
		return handle;
	}
	
	public static int getWidth(){
		return current.width;
	}
	
	public static int getHeight(){
		return current.height;
	}
	
	public int getPositionX(){
		return x;
	}
	
	public int getPositionY(){
		return y;
	}
	
	public void setCursorPos(double x, double y){
		glfwSetCursorPos(handle,x,y);
	}

	public void setClearColor(float r, float g, float b, float a){
		glClearColor(r,g,b,a);
	}
	
	public void setClearColor(Color color){
		setClearColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
	}
	
	protected void setInputMode(int input, int mode){
		glfwSetInputMode(handle, input, mode);
	}
	
	public void setHint(int hint, boolean value){
		setHint(hint, value ? 1 : 0);
	}
	
	public void setHint(int hint, int value){
		glfwWindowHint(hint, value);
	}
	
	public void show(){
		if(visible) return;
		visible = true;
		glfwShowWindow(handle);
	}
	
	public void hide(){
		if(!visible) return;
		visible = false;
		glfwHideWindow(handle);
	}
	
	public void setVisible(boolean visible){
		if(this.visible == visible) return;
		this.visible = visible;
		if(visible)
			show();
		else
			hide();
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public boolean isIconified(){
		return iconified;
	}
	
	public boolean isResized(){
		return resized;
	}
	
	public void iconify(){
		iconified = true;
		glfwIconifyWindow(handle);
	}
	
	public void restore(){
		iconified = false;
		glfwRestoreWindow(handle);
	}
	
	public GLCapabilities getGLCapabilities(){
		return glCapabilities;
	}
	
	public void center(){
		if(fullscreen) return;
		if(vidMode == null)
			vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		setPosition((vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
	}
	
	public void setPosition(int x, int y){
		if(fullscreen) return;
		this.x = x;
		this.y = y;
		glfwSetWindowPos(handle,x,y);
	}
	
	public void setSize(Vector2d vector){
		setSize((int)vector.x,(int)vector.y);
	}
	
	public void setSize(Vector2f vector){
		setSize((int)vector.x,(int)vector.y);
	}
	
	public void setSize(int width, int height){
		if(this.width == width && this.height == height) return;
		this.width = width;
		this.height = height;
		glfwSetWindowSize(handle,x,y);
	}
	
	public void setVSync(boolean vsync){
		if(this.vsync == vsync) return;
		this.vsync = vsync;
		glfwSwapInterval((vsync) ? 1 : 0);
	}
	
	public Vector2d getMousePos(){
		Vector2d pos = new Vector2d();
		DoubleBuffer posBuf = BufferUtils.createDoubleBuffer(2);
		long xpos = memAddress(posBuf);
		long ypos = xpos + Double.BYTES;
		nglfwGetCursorPos(handle, xpos, ypos);
		pos.x = posBuf.get(0);
		pos.y = posBuf.get(1);
		return pos;
	}
	
	public boolean isVSync(){
		return vsync;
	}
	
	public boolean isCloseRequested(){
		return glfwWindowShouldClose(handle) == 1;
	}
	
	public void setShouldClose(){
		glfwSetWindowShouldClose(handle, 1);
	}
	
	public void destroy(){
		releaseCallbacks();
		glfwDestroyWindow(handle);
	}
	
	public void releaseCallbacks(){
		mouseEnterCallback.release();
		mousePosCallback.release();
		mouseButtonCallback.release();
		sizeCallback.release();
		posCallback.release();
		keyCallback.release();
		errorCallback.release();
	}
}
