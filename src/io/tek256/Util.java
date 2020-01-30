package io.tek256;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import io.tek256.render.Mesh;

public class Util {
	public static float getFloat(String value){
		try{
			return Float.parseFloat(value);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return 0f;
	}
	
	public static double getDouble(String value){
		try{
			return Double.parseDouble(value);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return 0f;
	}
	
	public static int getInt(String value){
		try{
			return Integer.parseInt(value);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public static short getShort(String value){
		try{
			Short.valueOf(value);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public static byte getByte(String value){
		try{
			Byte.valueOf(value);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int[] toIntArray(Vector3f vec){
		return new int[]{
				(int)vec.x,
				(int)vec.y,
				(int)vec.z,
		};
	}
	
	public static float[] toFloatArray(Vector3f vec){
		return new float[]{
				vec.x,
				vec.y,
				vec.z
		};
	}
	
	public static Mesh getMesh(String path){
		String[] lines = getText(path).split("\n");
		
		ArrayList<Vector3f> verts = new ArrayList<Vector3f>();
		ArrayList<Vector2f> texcoords = new ArrayList<Vector2f>();
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		ArrayList<Face> faces = new ArrayList<Face>();
		
		for(String line : lines){
			String[] values = line.split("\\s+");
			switch(values[0]){
				case "v":
					Vector3f vert = new Vector3f(getFloat(values[1]),
							getFloat(values[2]),
							getFloat(values[3]));
					verts.add(vert);
					break;
				case "vt":
					Vector2f texcoord = new Vector2f(getFloat(values[1]),
							getFloat(values[2]));
					texcoords.add(texcoord);
					break;
				case "vn":
					Vector3f normal = new Vector3f(getFloat(values[1]),
							getFloat(values[2]),
							getFloat(values[3]));
					normals.add(normal);
					break;
				case "f":
					Face face = new Face(values[1],values[2],values[3]);
					faces.add(face);
					break;
			}
		}
		return reorderMesh(verts,texcoords,normals,faces);
	}
	
	public static Mesh reorderMesh(ArrayList<Vector3f> verts, ArrayList<Vector2f> texcoords, ArrayList<Vector3f> normals, ArrayList<Face> faces){
		ArrayList<Integer> indices = new ArrayList<Integer>();
		float[] positions = new float[verts.size()*3];
		float[] texcoordfloats = new float[positions.length*2];
		float[] normalfloats = new float[positions.length*3];
		
		for(int i=0;i<verts.size();i++){
			Vector3f pos = verts.get(i);
			positions[i*3] = pos.x;
			positions[i*3+1] = pos.y;
			positions[i*3+2] = pos.z;
		}
		
		for(int i=0;i<faces.size();i++){
			Face f = faces.get(i);
			for(int v=0;v<3;v++){ //per vertex
				int pos = f.getIndexPositionCoord(v);
				indices.add(pos);
				if(f.hasTexCoord(v)){
					Vector2f texCoord = texcoords.get(f.getIndexTexCoordCoord(v));
					texcoordfloats[pos*2] = texCoord.x;
					texcoordfloats[pos*2+1] = 1 - texCoord.y;
				}
				if(f.hasNormal(v)){
					Vector3f normal = normals.get(f.getIndexNormalCoord(v));
					normalfloats[pos*3] = normal.x;
					normalfloats[pos*3+1] = normal.y;
					normalfloats[pos*3+2] = normal.z;
				}
			}
		}
		
		int[] indicesArray = new int[indices.size()];
		for(int i=0;i<indices.size();i++){
			indicesArray[i] = indices.get(i);
		}
		Mesh mesh = new Mesh(positions,texcoordfloats,normalfloats,indicesArray);
		return mesh;
	}
	
	public static Vector3f getVector3f(String value){
		String regex = getRegex(value);
		String[] floatStrings = value.split(regex);
		if(floatStrings.length != 3)
			throw new RuntimeException("Vector3f requires 3 values");
		float[] floats = new float[floatStrings.length];
		for(int i=0;i<floatStrings.length;i++){
			floatStrings[i].trim();
			floats[i] = getFloat(floatStrings[i]);
		}
		return new Vector3f(floats[0],floats[1],floats[2]);
	}
	
	public static Vector3d getVector3d(String value){
		String regex = getRegex(value);
		String[] doubleStrings = value.split(regex);
		if(doubleStrings.length != 3)
			throw new RuntimeException("Vector3d requires 3 values");
		double[] doubles = new double[doubleStrings.length];
		for(int i=0;i<doubleStrings.length;i++){
			doubleStrings[i].trim();
			doubles[i] = getFloat(doubleStrings[i]);
		}
		return new Vector3d(doubles[0],doubles[1],doubles[2]);
	}
	
	public static Vector2f getVector2f(String value){
		String regex = getRegex(value);
		String[] floatStrings = value.split(regex);
		if(floatStrings.length != 2)
			throw new RuntimeException("Vector2d requires 2 values");
		float[] floats = new float[floatStrings.length];
		for(int i=0;i<floatStrings.length;i++){
			floatStrings[i].trim();
			floats[i] = getFloat(floatStrings[i]);
		}
		return new Vector2f(floats[0],floats[1]);
	}
	
	public static Vector2d getVector2d(String value){
		String regex = getRegex(value);
		String[] doubleStrings = value.split(regex);
		if(doubleStrings.length != 2)
			throw new RuntimeException("Vector2d requires 2 values");
		double[] doubles = new double[doubleStrings.length];
		for(int i=0;i<doubleStrings.length;i++){
			doubleStrings[i].trim();
			doubles[i] = getDouble(doubleStrings[i]);
		}
		return new Vector2d(doubles[0],doubles[1]);
	}
	
	public static Matrix4f getMatrix4f(String value){
		String regex = getRegex(value);
		String[] floatStrings = value.split(regex);
		if(floatStrings.length != 16)
			throw new RuntimeException("Matrix4f requires 16 values");
		float[] floats = new float[floatStrings.length];
		for(int i=0;i<floatStrings.length;i++){
			floatStrings[i].trim();
			floats[i] = getFloat(floatStrings[i]);
		}
		return new Matrix4f().set(floats);
	}
	
	public static String getRegex(String value){
		value.trim();
		return (value.contains("/")) ? "/" : (value.contains(",")) ? "," : (value.contains(":") ? ":" : " ");
	}
	
	protected static class Face{
		public static int POSITION_INDICE = 0;
		public static int TEXCOORD_INDICIE = 1;
		public static int NORMAL_INDICE = 2;
		
		Vector3f[] indices;
		 
		public Face(Vector3f v0, Vector3f v1, Vector3f v2){
			indices = new Vector3f[3];
			indices[0] = v0;
			indices[1] = v1;
			indices[2] = v2;
		}
		
		public Face(Vector3f... vectors){
			if(vectors.length != 3)
				throw new RuntimeException("Faces are made up of 3 vertices");
			indices = new Vector3f[3];
			for(int i=0;i<3;i++)
				indices[i] = vectors[i];
		}
		
		public Face(String v0, String v1, String v2){
			indices = new Vector3f[3];
			indices[0] = getVector3f(v0);
			indices[1] = getVector3f(v1);
			indices[2] = getVector3f(v2);
		}
		 
		public Face(String... values){
			if(!(values.length == 3 || values.length == 9))
				throw new RuntimeException("Faces are made with a set of 3 or 9 values");
			indices = new Vector3f[3];
			if(values.length == 3){
				for(int i=0;i<3;i++){
					int len = values[i].split(getRegex(values[i])).length;
					if(len == 3){
						indices[i] = getVector3f(values[i]);
					}else{
						Vector2f indx = getVector2f(values[i]);
						indices[i] = new Vector3f(indx.x, -1, indx.y);
					}
				}
			}else if(values.length == 9){
				for(int i=0;i<3;i++)
					indices[i] = new Vector3f(getInt(values[i]),
							getInt(values[i+1]),
							getInt(values[i+2])); 
			}
		}
		
		public Vector3f getIndice(int vertex){
			if(vertex > 3 || vertex < 0)
				throw new RuntimeException("There are only 3 vertices per triangle face");
			return indices[vertex];
		}
		
		public int getIndice(int vertex,int type){
			if(vertex > 3 || vertex < 0)
				throw new RuntimeException("There are only 3 vertices per triangle face");
			if(type > 3 || type < 0)
				throw new RuntimeException("There are only 3 type arrays per vertex");
			switch(type){
			case 0:
				return (int)indices[vertex].x;
			case 1:
				return (int)indices[vertex].y;
			case 2:
				return (int)indices[vertex].z;
			}
			return -1;
		}
		
		public int getIndexPositionCoord(int index){
			return (int)indices[index].x - 1;
		}
		
		public int getIndexTexCoordCoord(int index){
			return (int)indices[index].y - 1;
		}
		
		public int getIndexNormalCoord(int index){
			return (int)indices[index].z - 1;
		}
		
		public boolean hasPosition(int index){
			return indices[index].x != -1;
		}
		
		public boolean hasTexCoord(int index){
			return indices[index].y != -1;
		}
		
		public boolean hasNormal(int index){
			return indices[index].z != -1;
		}
	}
	
	public static String getText(String path){
		try{
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line;
			StringBuilder out = new StringBuilder();
			while((line = in.readLine()) != null){
				out.append(line+'\n');
			}
			in.close();
			return out.toString();
		}catch(IOException e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static ByteBuffer resizeBuffer(ByteBuffer buffer, int size){
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(size);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
	
	public static ByteBuffer resourceToByteBuffer(String resource) throws IOException {
		File f = new File(resource);
		if(!f.isFile())
			return null;
		
		FileInputStream in = new FileInputStream(f);
		FileChannel fc = in.getChannel();
		ByteBuffer buf = BufferUtils.createByteBuffer((int)fc.size()+1);
		
		while(fc.read(buf) != -1);
		
		in.close();
		fc.close();
		return buf;
	}
	
	public static ByteBuffer resourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buf;
		File f = new File(resource);
		if(f.isFile()){
			FileInputStream in = new FileInputStream(f);
			FileChannel fc = in.getChannel();
			buf = BufferUtils.createByteBuffer((int)fc.size()+1);
			while(fc.read(buf) != -1);
			in.close();
			fc.close();
		}else{
			buf = BufferUtils.createByteBuffer(bufferSize);
			InputStream src = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
			if(src == null)
				throw new FileNotFoundException(resource);
			ReadableByteChannel rbc = Channels.newChannel(src);
			while(true){
				int bytes = rbc.read(buf);
				if(bytes == -1)
					break;
				if(buf.remaining() == 0)
					buf = resizeBuffer(buf, buf.capacity() * 2);
			}
			rbc.close();
			src.close();
		}
		buf.flip();
		return buf;
	}
	
}
