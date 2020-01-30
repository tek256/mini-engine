package io.tek256.render;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class MeshBuilder {
	private static MeshBuilder instance = new MeshBuilder(true);
	private ArrayList<Vertex> vertices;
	private ArrayList<Face> faces;
	private ArrayList<Integer> indices;
	private ArrayList<Instruction> instructions;
	
	private boolean indexed = false;
	private Matrix4f mat;
	
	public MeshBuilder(boolean indexed){
		this.indexed = indexed;
		vertices = new ArrayList<Vertex>();
		faces = new ArrayList<Face>();
		instructions = new ArrayList<Instruction>();
		mat = new Matrix4f();
		if(indexed)
			indices = new ArrayList<Integer>();
	}
	
	public void addCube(Vector3f center, Vector3f size, Vector3f rotation){
		mat.identity();
		mat.translate(center);
		mat.rotate((float)Math.toRadians(rotation.x), new Vector3f(1f,0,0));
		mat.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1f,0));
		mat.rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1f));
		
		float hw = size.x * 0.5f, hh = size.y * 0.5f, hd = size.z * 0.5f;
		addQuad(new Vector3f(0f,0f, hd), new Vector2f(size.x,size.y), new Vector3f(0,180f,0));
		addQuad(new Vector3f(0f,0f,-hd), new Vector2f(size.x,size.y), new Vector3f(0f,0f,0f));
		
		addQuad(new Vector3f( hw,0f,0f), new Vector2f(size.z,size.y), new Vector3f(0f,-90f,0f));
		addQuad(new Vector3f(-hw,0f,0f), new Vector2f(size.z,size.y), new Vector3f(0f,90f,0f));
		
		addQuad(new Vector3f(0f, hh,0f), new Vector2f(size.x,size.z), new Vector3f(90f,0f,0));
		addQuad(new Vector3f(0f,-hh,0f), new Vector2f(size.x,size.z), new Vector3f(-90f,0f,0));
		
		instructions.add(new Instruction(InstructionType.CUBE, center, size, rotation));
	}
	
	public void addQuad(Vector3f center, Vector2f size, Vector3f rotation){
		mat.identity();
		mat.translate(center);
		mat.rotate((float)Math.toRadians(rotation.x), new Vector3f(1f,0,0));
		mat.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1f,0));
		mat.rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1f));
		
		float hw = size.x * 0.5f, hh = size.y * 0.5f;
		
		Vertex[] verts = new Vertex[]{
			new Vertex(new Vector3f(-hw,-hh,0), new Vector2f(0,0)),
			new Vertex(new Vector3f(-hw,hh,0), new Vector2f(0,1)),
			new Vertex(new Vector3f(hw,hh,0), new Vector2f(1,1)),
			new Vertex(new Vector3f(hw,-hh,0), new Vector2f(1,0)),
		};
		
		//012 230
		int index = vertices.size();
		Face[] faces = new Face[]{
			new Face(index,index+1,index+2),	
			new Face(index+2,index+3,index),	
		};
		
		for(Vertex v: verts)
			v.position.mulPoint(mat);
		
		addVertices(verts);
		addFaces(faces);
		
		
		instructions.add(new Instruction(InstructionType.QUAD, center, new Vector3f(size, 0f), rotation));
	}
	
	public void addQuad(Vector3f center, Vector2f size, Vector3f rotation, Vector2f[] texcoords){
		mat.identity();
		mat.translate(center);
		mat.rotate((float)Math.toRadians(rotation.x), new Vector3f(1f,0,0));
		mat.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1f,0));
		mat.rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1f));
		
		float hw = size.x * 0.5f, hh = size.y * 0.5f;
		
		Vertex[] verts = new Vertex[]{
			new Vertex(new Vector3f(-hw,-hh,0), texcoords[0]),	
			new Vertex(new Vector3f(-hw,hh,0), texcoords[1]),	
			new Vertex(new Vector3f(hw,hh,0), texcoords[2]),	
			new Vertex(new Vector3f(hw,-hh,0), texcoords[3]),	
		};
		
		int index = vertices.size();
		Face[] faces = new Face[]{
			new Face(index, index+1, index+2),	
			new Face(index+2, index+3, index),	
		};
		
		for(Vertex v : verts)
			v.position.mulPoint(mat);
		
		addVertices(verts);
		addFaces(faces);
	}
	
	public void addQuad(Vector2f[] points, Vector2f[] texcoords){
		Vertex[] verts = new Vertex[]{
			new Vertex(new Vector3f(points[0], 0), texcoords[0]),	
			new Vertex(new Vector3f(points[1], 0), texcoords[1]),	
			new Vertex(new Vector3f(points[2], 0), texcoords[2]),	
			new Vertex(new Vector3f(points[3], 0), texcoords[3]),	
		};
		
		int index = vertices.size();
		Face[] faces = new Face[]{
			new Face(index,index+1,index+2),	
			new Face(index+2,index+3,index),	
		};
		
		addVertices(verts);
		addFaces(faces);
	}
	
	public void addQuad(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1){
		Vertex[] verts = new Vertex[]{
				new Vertex(new Vector3f(x0,y0,0), new Vector2f(s0, t0)),
				new Vertex(new Vector3f(x0,y1,0), new Vector2f(s0, t1)),
				new Vertex(new Vector3f(x1,y1,0), new Vector2f(s1, t1)),
				new Vertex(new Vector3f(x1,y0,0), new Vector2f(s1, t0)),
		};
		
		int index = vertices.size();
		Face[] faces = new Face[]{
			new Face(index,index+1,index+2),	
			new Face(index+2,index+3,index),	
		};
		
		addVertices(verts);
		addFaces(faces);
	}
	
	public void addFace(Face f){
		faces.add(f);
		if(f.isIndexed())
			addIndices(f.indices);
	}
	
	public void addFace(int[] indices){
		faces.add(new Face(indices));
		addIndices(indices);
	}
	
	public void addFace(Vertex[] vertices){
		faces.add(new Face(vertices));
		addVertices(vertices);
	}
	
	public void addFaces(Face...faces){
		for(int i=0;i<faces.length;i++)
			addFace(faces[i]);
	}
	
	public void addFaces(List<Face> faces){
		for(int i=0;i<faces.size();i++)
			addFace(faces.get(i));
	}
	
	public void addVertices(Vertex...vertices){
		for(int i=0;i<vertices.length;i++)
			this.vertices.add(vertices[i]);
	}
	
	public void addVertices(List<Vertex> vertices){
		for(int i=0;i<vertices.size();i++)
			this.vertices.add(vertices.get(i));
	}
	
	public void addVertices(Vertex[] vertices,Vector3f position, Vector3f rotation){
		mat.identity();
		mat.translate(position);
		mat.rotate((float)Math.toRadians(rotation.x), new Vector3f(1f,0,0));
		mat.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1f,0));
		mat.rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1f));
		for(Vertex v : vertices){
			v.position.mulPoint(mat);
		}
		addVertices(vertices);
	}
	
	public void addVertices(List<Vertex> vertices, Vector3f position, Vector3f rotation){
		mat.identity();
		mat.translate(position);
		mat.rotate((float)Math.toRadians(rotation.x), new Vector3f(1f,0,0));
		mat.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1f,0));
		mat.rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1f));
		for(Vertex v : vertices)
			v.position.mulPoint(mat);
		addVertices(vertices);
	}
	
	public void addIndices(int...indices){
		for(int i=0;i<indices.length;i++)
			this.indices.add(indices[i]);
	}
	
	public void addIndices(List<Integer> indices){
		for(int i=0;i<indices.size();i++)
			this.indices.add(indices.get(i));
	}
	
	public Face[] getFaces(int[] faceIndices){
		Face[] array = new Face[faceIndices.length];
		for(int i=0;i<faceIndices.length;i++)
			array[i] = getFace(faceIndices[i]);
		return array;
	}
	
	public Face getFace(int index){
		return faces.get(index);
	}
	
	public Face getNonIndexed(Face face){
		return new Face(getVertices(face.indices));
	}
	
	public Vertex getVertex(int index){
		return vertices.get(index);
	}
	
	public Vertex[] getVertices(int[] indices){
		Vertex[] array = new Vertex[indices.length];
		for(int i=0;i<indices.length;i++)
			array[i] = getVertex(indices[i]);
		return array;
	}
	
	public int[] getIndices(){
		int[] array = new int[indices.size()];
		for(int i=0;i<indices.size();i++)
			array[i] = indices.get(i);
		return array;
	}
	
	public List<Vertex> getVertexList(){
		return vertices;
	}
	
	public List<Integer> getIndicesList(){
		return indices;
	}
	
	public List<Face> getFaceList(){
		return faces;
	}
	
	public float[] getPositions(){
		ArrayList<Float> positions = new ArrayList<Float>();
		for(int i=0;i<vertices.size();i++){
			Vector3f pos = vertices.get(i).position;
			positions.add(pos.x);
			positions.add(pos.y);
			positions.add(pos.z);
		}
		float[] array = new float[positions.size()];
		for(int i=0;i<positions.size();i++){
			array[i] = positions.get(i);
		}
		return array;
	}
	
	public float[] getNormals(){
		ArrayList<Float> normals = new ArrayList<Float>();
		for(int i=0;i<vertices.size();i++){
			Vector3f nor = vertices.get(i).normal;
			normals.add(nor.x);
			normals.add(nor.y);
			normals.add(nor.z);
		}
		float[] array = new float[normals.size()];
		for(int i=0;i<normals.size();i++){
			array[i] = normals.get(i);
		}
		return array;
	}
	
	public float[] getTexCoords(){
		ArrayList<Float> texCoords = new ArrayList<Float>();
		for(int i=0;i<vertices.size();i++){
			Vector2f tex = vertices.get(i).texCoord;
			texCoords.add(tex.x);
			texCoords.add(tex.y);
		}
		float[] array = new float[texCoords.size()];
		for(int i=0;i<texCoords.size();i++){
			array[i] = texCoords.get(i);
		}
		return array;
	}

	public boolean isIndexed(){
		return indexed;
	}
	
	public Mesh toMesh(){
		if(indexed)
			return new Mesh(getPositions(),getTexCoords(),getNormals(),getIndices());
		else
			return new Mesh(getPositions(),getTexCoords(),getNormals());
	}
	
	public void clear(){
		vertices.clear();
		faces.clear();
		indices.clear();
		mat.identity();
	}
	
	public static Mesh getPlane(float width, float height){
		instance.clear();
		instance.addQuad(Vector3f.ZERO, new Vector2f(width,height), Vector3f.ZERO);
		return instance.toMesh();
	}
	
	public static MeshBuilder getInstance(){
		if(instance == null)
			instance = new MeshBuilder(true);
		return instance;
	}
	
	public static class Vertex{
		public Vector3f position,normal;
		public Vector2f texCoord;
		
		public Vertex(){
			this(new Vector3f());
		}
		
		public Vertex(Vector3f position){
			this(position, new Vector2f());
		}
		
		public Vertex(Vector3f position, Vector2f texCoord){
			this(position,texCoord,new Vector3f());
		}
		
		public Vertex(Vector3f position, Vector2f texCoord, Vector3f normal){
			this.position = position;
			this.texCoord = texCoord;
			this.normal = normal;
		}
	}
	
	public static class Face{
		private Vertex[] vertices;
		private int[] indices;
		
		public Face(Vertex[] vertices){
			if(vertices.length != 3)
				return;
			this.vertices = vertices;
		}
		
		public Face(Vertex v1, Vertex v2, Vertex v3){
			this.vertices = new Vertex[]{v1,v2,v3};
		}
		
		public Face(int[] indices){
			if(indices.length != 3)
				return;
			this.indices = indices;
		}
		
		public Face(int v1, int v2, int v3){
			this.indices = new int[]{v1,v2,v3};
		}
		
		public boolean isIndexed(){
			return vertices == null;
		}
	}
	
	public static enum GenerationType{
		QUAD,
		CUBE,
		OTHER,
	}
	
	public static enum InstructionType{
		QUAD,
		QUAD_TEXCOORD,
		CUBE,
		CUBE_TEXCOORD,
		OTHER
	}
	
	public static class Instruction{
		InstructionType type;
		public Vector3f center,size,rotation;
		
		public Instruction(InstructionType type, Vector3f center, Vector3f size, Vector3f rotation){
			this.type = type;
			this.center = center;
			this.size = size;
			this.rotation = rotation;
		}
	}
}
