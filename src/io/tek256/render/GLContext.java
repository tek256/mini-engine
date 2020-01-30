package io.tek256.render;

import static org.lwjgl.opengl.GL11.*;

public class GLContext {
	public static void viewport(int x, int y, int w, int h){
		glViewport(x,y,w,h);
	}
	
	public static void clear(int buffers){
		glClear(buffers);
	}
	
	public static void enable(int value){
		glEnable(value);
	}
	
	public static void disable(int value){
		glDisable(value);
	}
	
	public static void blendFunc(int src, int dst){
		glBlendFunc(src, dst);
	}
	
	public static void depthFunc(int depthFunc){
		glDepthFunc(depthFunc);
	}
	
	public static void cullFace(int face){
		glCullFace(face);
	}
}
