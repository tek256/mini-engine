package io.tek256.render;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;

public class ShaderProgram {
	private static ShaderProgram current = null;
	private int id;
	private boolean deleted = false;
	private HashMap<String,Integer> attributes;
	private HashMap<String,Uniform> uniforms;
	
	public ShaderProgram(){
		id = GL20.glCreateProgram();
		attributes = new HashMap<>();
		uniforms = new HashMap<>();
	}
	
	public ShaderProgram(Shader ... shaders){
		id = GL20.glCreateProgram();
		for(Shader shader: shaders){
			attachShader(shader);
		}
		link();
		attributes = new HashMap<>();
		uniforms = new HashMap<>();
	}
	
	public ShaderProgram(Shader vertex, Shader fragment){
		id = glCreateProgram();
		attachShader(vertex);
		attachShader(fragment);
		link();
		attributes = new HashMap<>();
		uniforms = new HashMap<>();
	}
	
	public void attachShader(Shader shader){
		glAttachShader(id,shader.getId());
	}
	
	public void link(){
		glLinkProgram(id);
		if(glGetShaderi(id, GL_LINK_STATUS) == GL_FALSE)
			System.out.println(getInfoLog());
	}
	
	public void bind(){
		if(current == this)
			return;
		if(deleted)
			throw new RuntimeException("Cannot use deleted programs");
		glUseProgram(id);
		current = this;
	}
	
	public int getAttributeLocation(String name){
		bind();
		if(attributes.containsKey(name))
			return attributes.get(name);
		int loc = glGetAttribLocation(id,name);
		attributes.put(name, loc);
		return loc;
	}
	
	public int getUniformLocation(String name){
		bind();
		if(uniforms.containsKey(name))
			return uniforms.get(name).getLocation();
		int loc = glGetUniformLocation(id,name);
		uniforms.put(name, new Uniform(loc));
		return loc;
	}
	
	public void setupPointLightUniforms(String name, int max){
		for(int i=0;i<max;i++){
			createPointLightUniform(name+'['+i+']');
		}
	}
	
	public void createPointLightUniform(String name){
		bind();
		if(uniforms.containsKey(name))
			return;
		getUniformLocation(name+".color");
		getUniformLocation(name+".position");
		getUniformLocation(name+".intensity");
		getUniformLocation(name+".att.constant");
		getUniformLocation(name+".att.linear");
		getUniformLocation(name+".att.exponent");
		uniforms.put(name, new Uniform(-1));
	}
	
	public void createMaterialUniform(String name){
		getUniformLocation(name+".color");
		getUniformLocation(name+".tex");
		getUniformLocation(name+".hasTexture");
		getUniformLocation(name+".normalMap");
		getUniformLocation(name+".hasNormalMap");
		getUniformLocation(name+".reflect");
	}
	
	public void setUniform(String name, PointLight light, int pos){
		setUniform(name+'['+pos+']',light);
	}
	
	public void setUniform(String name, PointLight light){
		setUniform(name+".color",light.getColor());
		setUniform(name+".position",light.getPosition());
		setUniform(name+".intensity",light.getIntensity());
		PointLight.Attenuation a = light.getAttenuation();
		setUniform(name+".att.constant",a.getConstant());
		setUniform(name+".att.linear",a.getLinear());
		setUniform(name+".att.exponent",a.getExponent());
	}

	public void setUniform(String name, Material material){
		setUniform(name+".color",material.getColor());
		setUniform(name+".tex", 0);
		setUniform(name+".hasTexture",material.hasTexture() ? 1 : 0);
		setUniform(name+".normalMap",1);
		setUniform(name+".hasNormalMap", material.hasNormalMap() ? 1 :0);
		setUniform(name+".reflect",material.getReflect());
	}
	
	public void setUniform(String name, int value){
		Uniform u = uniforms.get(name);
		if(u == null)
			throw new RuntimeException("Uniform: "+name+" has not been setup");
		glUniform1i(u.getLocation(), value);
	}
	
	public void setUniform(String name, float value){
		Uniform u = uniforms.get(name);
		if(u == null)
			throw new RuntimeException("Uniform: "+name+" has not been setup");
		glUniform1f(u.getLocation(), value);
	}
	
	public void setUniform(String name, Vector3f value){
		Uniform u = uniforms.get(name);
		if(u == null)
			throw new RuntimeException("Uniform: "+name+" has not been setup");
		glUniform3f(u.getLocation(), value.x, value.y, value.z);
	}
	
	public void setUniform(String name, Matrix4f matrix){
		Uniform u = uniforms.get(name);
		if(u == null)
			throw new RuntimeException("Uniform: "+name+" has not been setup");
		FloatBuffer buf = u.getBuffer();
		if(buf == null){
			buf = BufferUtils.createFloatBuffer(16);
			u.setBuffer(buf);
		}
		matrix.get(buf);
		glUniformMatrix4fv(u.getLocation(), false, buf);		
	}
	
	public String getInfoLog(int maxlen){
		return glGetProgramInfoLog(id,maxlen);
	}
	
	public String getInfoLog(){
		return glGetProgramInfoLog(id);
	}
	
	public boolean isDeleted(){
		return deleted;
	}
	
	public void delete(){
		if(deleted) return;
		deleted = true;
		glDeleteProgram(id);
	}
	
	public static void unbind(){
		if(current == null) return;
		current = null;
		glUseProgram(0);
	}
	
	public class Uniform{
		private int loc;
		private FloatBuffer buf;
		
		public Uniform(int location){
			loc = location;
		}
		
		public int getLocation(){
			return loc;
		}
		
		public FloatBuffer getBuffer(){
			return buf;
		}
		
		public void setBuffer(FloatBuffer buf){
			this.buf = buf;
		}
		
	}
}
