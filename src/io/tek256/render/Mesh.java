package io.tek256.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
	private int vao,vertexCount;
	private List<Integer> vboids;
	private Material material;
	private boolean destroyed = false;
	
	public Mesh(float[] verts, float[] texcoords, float[] normals, int[] indices){
		vertexCount = indices.length;
		vboids = new ArrayList<Integer>();
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		//vertices
		int vbo = glGenBuffers();
		vboids.add(vbo);
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(verts.length);
		vertBuffer.put(verts).flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		//texcoords
		vbo = glGenBuffers();
		vboids.add(vbo);
		FloatBuffer texcoordBuffer = BufferUtils.createFloatBuffer(texcoords.length);
		texcoordBuffer.put(texcoords).flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, texcoordBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		//normals
		vbo = glGenBuffers();
		vboids.add(vbo);
		FloatBuffer normalsBuffer = BufferUtils.createFloatBuffer(normals.length);
		normalsBuffer.put(normals).flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		//indices
		vbo = glGenBuffers();
		vboids.add(vbo);
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	public void render(){
		if(destroyed) return;
		Texture t = material.getTexture();
		if(t != null){
			glActiveTexture(GL_TEXTURE0);
			t.bind();
		}
		Texture nm = material.getNormalMap();
		if(nm != null){
			glActiveTexture(GL_TEXTURE1);
			nm.bind();
		}
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		Texture.unbind();
	}
	
	public void render(Material customMaterial){
		if(customMaterial == null)
			return;
		if(destroyed)
			return;
		Texture t = customMaterial.getTexture();
		if(t != null){
			glActiveTexture(GL_TEXTURE0);
			t.bind();
		}
		Texture n = customMaterial.getNormalMap();
		if(n != null){
			glActiveTexture(GL_TEXTURE1);
			n.bind();
		}
		
		glBindVertexArray(vao);
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		Texture.unbind();
	}
	
	public int getVertexCount(){
		return vertexCount;
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public void setMaterial(Material material){
		if(destroyed)
			return;
		this.material = material;
	}
	
	public void destroy(){
		if(destroyed)
			return;
		destroyed = true;
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		for(int vbo : vboids)
			glDeleteBuffers(vbo);
		
		material.destroy();
		
		glBindVertexArray(0);
		glDeleteVertexArrays(vao);
	}
}
