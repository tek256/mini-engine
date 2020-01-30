package io.tek256.render.gui;

import static org.lwjgl.system.MemoryUtil.memAllocFloat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import io.tek256.Util;
import io.tek256.Window;
import io.tek256.render.Material;
import io.tek256.render.Mesh;
import io.tek256.render.MeshBuilder;
import io.tek256.render.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

public class GUIFont {
	public static GUIFont DEFAULT;
	
	public String path;
	private int id;
	
	protected Material material;
	
	//stb
	private STBTTAlignedQuad q = STBTTAlignedQuad.malloc();
	private STBTTPackedchar.Buffer chardata;
	private FloatBuffer xb = memAllocFloat(1);
	private FloatBuffer yb = memAllocFloat(1);
	
	private String compatibleChars;
	private Texture psuedoTexture;
	
	private int BITMAP_W = 1024, BITMAP_H = 1024;
	
	private float[] scale = new float[]{
		12,
		24
	};
	
	public GUIFont(String path){
		this.path = path;
		init();
	}
	
	private void init(){
		id = glGenTextures();
		chardata = STBTTPackedchar.mallocBuffer(6 * 128);

		try {
			ByteBuffer ttf = Util.resourceToByteBuffer("res/fonts/default.ttf", 160 * 1024);

			ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);

			STBTTPackContext pc = STBTTPackContext.malloc();
			stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, null);
			for ( int i = 0; i < 2; i++ ) {
				chardata.position((i * 3 + 0) * 128 + 32);
				stbtt_PackSetOversampling(pc, 1, 1);
				stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, 96, chardata);

				chardata.position((i * 3 + 1) * 128 + 32);
				stbtt_PackSetOversampling(pc, 2, 2);
				stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, 96, chardata);

				chardata.position((i * 3 + 2) * 128 + 32);
				stbtt_PackSetOversampling(pc, 3, 1);
				stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, 96, chardata);
			}
			stbtt_PackEnd(pc);
			pc.free();

			glBindTexture(GL_TEXTURE_2D, id);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glBindTexture(GL_TEXTURE_2D, 0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		psuedoTexture = new Texture(id);
		material = new Material(psuedoTexture);
		
		StringBuilder compatible = new StringBuilder();
		for(int i=0;i<96;i++)
			compatible.append((char)i);
		compatibleChars = compatible.toString();
	}
	
	public STBChar getChar(char c, int font){
		xb.put(0, 0);
		yb.put(0, 0);
		chardata.position(font * 128);
		stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, c, xb, yb, q, 0);
		return new STBChar(q.x0(),q.x1(),q.y0(),q.y1(),q.s0(),q.s1(),q.t0(),q.t1());
	}
	
	public Mesh getText(String text){
		MeshBuilder b = new MeshBuilder(true);
		float x0=0,y0=0,x1=0,y1=0,s0=0,t0=0,s1=0,t1=0;
		xb.put(0,30f);
		yb.put(0,30f);
		chardata.position(3 * 128);
		for(char c : text.toCharArray()){
			stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, (int)c, xb, yb, q, 0);
			x0 = q.x0();
			x1 = q.x1();
			y0 = q.y0();
			y1 = q.y1();
			s0 = q.s0();
			s1 = q.s1();
			t0 = q.t0();
			t1 = q.t1();
			
			b.addQuad(new Vector2f[]{
				new Vector2f(x0,y0),
				new Vector2f(x0,y1),
				new Vector2f(x1,y1),
				new Vector2f(x1,y0),
			}, new Vector2f[]{
				new Vector2f(s0,t0),
				new Vector2f(s0,t1),
				new Vector2f(s1,t1),
				new Vector2f(s1,t0),
			});
		}
		return b.toMesh();
	}
	
	public String getCompatibleChars(){
		return compatibleChars;
	}
	
	public void bind(){
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public static void unbind(){
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void drawString(float x, float y, int font, String text){
		xb.put(0, x);
		yb.put(0, y);

		chardata.position(font * 128);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, Window.getWidth(), Window.getHeight(), 0.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		glTranslatef(x,y,0);
		glBindTexture(GL_TEXTURE_2D, id);
		glColor3f(1f,1f,1f);
		
		glBegin(GL_QUADS);
		for ( int i = 0; i < text.length(); i++ ) {
			stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, text.charAt(i), xb, yb, q, 0);
			drawBoxTC(
				q.x0(), q.y0(), q.x1(), q.y1(),
				q.s0(), q.t0(), q.s1(), q.t1()
			);
		}
		glEnd();
	}
	
	public void drawString(Vector2f position, Vector3f rotation, Vector3f scale, Vector3f color, int font, String text){
		if(text.length() == 0 || text.trim().length() == 0)
			return;		
		xb.put(0, position.x);
		yb.put(0, position.y);
		chardata.position(font * 128);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, Window.getWidth(), Window.getHeight(), 0.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		glScalef(scale.x, scale.y, scale.z);
		glRotatef((float)(Math.toRadians(rotation.x)), 1, 0, 0);
		glRotatef((float)(Math.toRadians(rotation.y)), 0, 1, 0);
		glRotatef((float)(Math.toRadians(rotation.z)), 0, 0, 1);
		
		glBindTexture(GL_TEXTURE_2D, id);
		glColor3f(color.x, color.y, color.z);
		glBegin(GL_QUADS);
		for ( int i = 0; i < text.length(); i++ ) {
			stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, text.charAt(i), xb, yb, q, 0);
			drawBoxTC(
				q.x0(), q.y0(), q.x1(), q.y1(),
				q.s0(), q.t0(), q.s1(), q.t1()
			);
		}
		glEnd();
	}
	
	public void drawTexture(){
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, Window.getWidth(), Window.getHeight(), 0.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		float w = 128;
		float h= 128;
		glBindTexture(GL_TEXTURE_2D, id);
		glTranslatef(w,h,0f);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0);
			glVertex2f(0, 0);
			glTexCoord2f(1, 0);
			glVertex2f(w, 0);
			glTexCoord2f(1, 1);
			glVertex2f(w, h);
			glTexCoord2f(0, 1);
			glVertex2f(0, h);
		glEnd();
	}
	
	private void drawBoxTC(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1){
		glTexCoord2f(s0, t0);
		glVertex2f(x0, y0);
		glTexCoord2f(s0, t1);
		glVertex2f(x0, y1);
		glTexCoord2f(s1, t1);
		glVertex2f(x1, y1);
		glTexCoord2f(s1, t0);
		glVertex2f(x1, y0);
	}
	
	public int getFontSize(int size){
		return (int) scale[size % 3];
	}
	
	public static class STBChar{
		float x0,x1,y0,y1,s0,s1,t0,t1;
		
		public STBChar(float x0, float x1, float y0, float y1, float s0, float s1, float t0, float t1){
			this.x0 = x0;
			this.x1 = x1;
			this.y0 = y0;
			this.y1 = y1;
			this.s0 = s0;
			this.s1 = s1;
			this.t0 = t0;
			this.t1 = t1;
		}
		
		public Vector2f[] getTexCoords(){
			return new Vector2f[]{
				new Vector2f(s0, t0),	
				new Vector2f(s0, t1),	
				new Vector2f(s1, t1),	
				new Vector2f(s1, t0),	
			};
		}
		
		public Vector2f[] getVertices(){
			return new Vector2f[]{
				new Vector2f(x0, y0),
				new Vector2f(x0, y1),
				new Vector2f(x1, y1),
				new Vector2f(x1, y0),
			};
		}
		
		@Override
		public String toString(){
			return "x{"+x0+","+x1+"} y{"+y0+","+y1+"} s{"+s0+","+s1+"} t{"+t0+","+t1+"}";
		}
	}
}
