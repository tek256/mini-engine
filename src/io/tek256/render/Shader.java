package io.tek256.render;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;

import io.tek256.Util;

public class Shader {
	private String path;
	private Type type;
	private int id;
	
	public Shader(String path, Type type){
		this.path = path;
		this.type = type;
		init();
	}
	
	public Shader(String path){
		this.path = path;
		type = Type.FRAGMENT_SHADER;
		init();
	}
	
	private void init(){
		id = glCreateShader(type.getGLValue());
		String src = Util.getText(path);
		glShaderSource(id,src);
		glCompileShader(id);
		if(glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE)
			System.out.println(getInfoLog());
	}
	
	public String getInfoLog(int maxlen){
		return glGetShaderInfoLog(id,maxlen);
	}
	
	public String getInfoLog(){
		return glGetShaderInfoLog(id);
	}
	
	public int getId(){
		return id;
	}
	
	public Type getType(){
		return type;
	}
	
	public int getTypeValue(){
		return type.getGLValue();
	}
	
	public void destroy(){
		glDeleteShader(id);
	}
	
	public enum Type{
		VERTEX_SHADER(GL_VERTEX_SHADER),
		FRAGMENT_SHADER(GL_FRAGMENT_SHADER);
		//GEOMETRY_SHADER(GL_GEOMETRY_SHADER) TODO Add Geometry shader support
		
		int value;
		
		Type(int glvalue){
			value = glvalue;
		}
		
		public int getGLValue(){
			return value;
		}
	}
}
