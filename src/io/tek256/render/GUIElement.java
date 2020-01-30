package io.tek256.render;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import io.tek256.Transform;
import io.tek256.Util;
import io.tek256.Window;

public class GUIElement extends Transform{
	private boolean renderable = false;
	public Texture texture;
	private Vector2f padding;
	private FontTexture fontTexture;
	private String message;
	private Vector3f color;
	private float bufferedWidth = 0,bufferedHeight = 0,z = 0;
	private final GUIType type;
	private int displayWidth,displayHeight;
	private Mesh mesh;
	private EventHandler eventHandler;
	private Anchor anchor = Anchor.FREE;
	
	public GUIElement(String message){
		this.message = message;
		fontTexture = FontTexture.defaultFont;
		type = GUIType.MESSAGE;
		buffer();
	}
	
	public GUIElement(String message, FontTexture font){
		this.message = message;
		fontTexture = font;
		type = GUIType.MESSAGE;
		buffer();
	}
	
	public GUIElement(Texture texture){
		this(texture, new Vector3f(1f,1f,1f));
	}
	
	public GUIElement(Texture texture, Vector3f color){
		bufferedWidth = texture.getWidth() / 10;
		bufferedHeight = texture.getHeight() / 10;
		this.texture = texture;
		this.color = color;
		type = GUIType.TEXTURE;
		buffer();
	}
	
	public GUIElement(Vector3f color){
		type = GUIType.COLOR;
		this.color = color;
		buffer();
	}
	
	private void buffer(){
		if(padding == null)
			padding = new Vector2f();
		
		if(type == GUIType.MESSAGE){
			char[] chars = message.toCharArray();
			int vertsPerQuad = 4;
			
			ArrayList<Integer> indices = new ArrayList<Integer>();
			ArrayList<Float> positions = new ArrayList<Float>();
			ArrayList<Float> texCoords = new ArrayList<Float>();
			float[] normals = new float[0];
			
			float sx = 0;
			for(int i=0;i<chars.length;i++){
				FontTexture.CharInfo charInfo = fontTexture.getCharInfo(chars[i]);
				
				positions.add(sx);
				positions.add(0f);
				positions.add(z);
				texCoords.add((float)charInfo.getOffset() / (float)fontTexture.getWidth());
				texCoords.add(0f);
				indices.add(i*vertsPerQuad);
				
				positions.add(sx);
				positions.add((float)fontTexture.getHeight());
				positions.add(z);
				texCoords.add((float)charInfo.getOffset() / (float)fontTexture.getWidth());
				texCoords.add(1f);
				indices.add(i*vertsPerQuad+1);
				
				positions.add(sx + charInfo.getWidth());
				positions.add((float)fontTexture.getHeight());
				positions.add(z);
				texCoords.add((float)(charInfo.getOffset() + charInfo.getWidth()) / (float)fontTexture.getWidth());
				texCoords.add(1f);
				indices.add(i*vertsPerQuad+2);
				
				positions.add(sx + charInfo.getWidth());
				positions.add(0f);
				positions.add(z);
				texCoords.add((float)(charInfo.getOffset() + charInfo.getWidth()) / (float)fontTexture.getWidth());
				texCoords.add(0f);
				indices.add(i*vertsPerQuad+3);
				
				indices.add(i*vertsPerQuad);
				indices.add(i*vertsPerQuad+2);
				sx += charInfo.getWidth();
			}
			bufferedWidth = sx;
			bufferedHeight = fontTexture.getHeight();
			
			float[] pos = new float[positions.size()];
			for(int i=0;i<positions.size();i++){
				pos[i] = positions.get(i);
			}
			
			float[] texcoords = new float[texCoords.size()];
			for(int i=0;i<texCoords.size();i++){
				texcoords[i] = texCoords.get(i);
			}
			
			int[] inds = new int[indices.size()];
			for(int i=0;i<indices.size();i++){
				inds[i] = indices.get(i);
			}
			Mesh m = new Mesh(pos, texcoords, normals, inds);
			m.setMaterial(new Material(fontTexture.getTexture()));
			mesh = m;
		}else{
			bufferedWidth = (texture != null) ? texture.getWidth() : 10f;
			bufferedHeight = (texture != null) ? texture.getHeight() : 10f;
			
			int[] indices = new int[]{
				0,1,2,
				2,3,0
			};
			float[] positions = new float[]{
				0,0,0,
				0,bufferedHeight,0,
				bufferedWidth,bufferedHeight,0,
				bufferedWidth,0,0
			};
			
			float[] texCoords = new float[]{
				0,0,
				0,1,
				1,1,
				1,0,
			};
			
			float[] normals = new float[0];
			mesh = new Mesh(positions, texCoords, normals, indices);
			Material mat;
			if(texture != null)
				mat = new Material(color, texture);
			else
				mat = new Material(color);
			mesh.setMaterial(mat);
		}
		displayWidth = Window.getWidth();
		displayHeight = Window.getHeight();
		renderable = true;
	}
	
	public void setRenderable(boolean renderable){
		this.renderable = renderable;
	}
	
	public void setAnchor(Anchor anchor){
		if(this.anchor == anchor)
			return;
		this.anchor = anchor;
		float width = getWidth(), height = getHeight();
		switch(anchor){
		case BOTTOM_LEFT:
			position.x = 0;
			position.y = displayHeight - height;
			break;
		case BOTTOM_CENTER:
			position.x = displayWidth / 2 - (width / 2);
			position.y = displayHeight - height;
			break;
		case BOTTOM_RIGHT:
			position.x = displayWidth - width;
			position.y = displayHeight - height;
			break;
		case CENTER_LEFT:
			position.x = 0;
			position.y = displayHeight / 2 - (height / 2);
			break;
		case CENTER:
			position.x = displayWidth / 2 - (width / 2);
			position.y = displayHeight / 2 - (height / 2);
			break;
		case CENTER_RIGHT:
			position.x = displayWidth - width;
			position.y = displayHeight / 2 - (height / 2);
			break;
		case TOP_LEFT:
			position.x = 0;
			position.y = 0;
			break;
		case TOP_CENTER:
			position.x = displayWidth / 2 - (width / 2);
			position.y = 0;
			break;
		case TOP_RIGHT:
			position.x = displayWidth - width;
			position.y = 0;
			break;
		case FREE:
			break;
		}
	}
	
	public void align(int width, int height){
		float x = position.x / displayWidth;
		float y = position.y / displayHeight;
		position.x = x * width;
		position.y = y * height;
		displayWidth = width;
		displayHeight = height;
	}
	
	public void setColor(float r, float g, float b){
		color = new Vector3f(r,g,b);
		mesh.getMaterial().setColor(color);
	}
	
	public void setColor(Vector3f color){
		this.color = color;
		mesh.getMaterial().setColor(color);
	}
	
	public void setText(String text){
		if(text.equals(message))
			return;
		message = text;
		buffer();
	}
	
	public String getText(){
		return message;
	}
	
	public void render(){
	}
	
	public boolean allowRender(){
		return renderable;
	}
	
	public Mesh getMesh(){
		return mesh;
	}
	
	public GUIType getType(){
		return type;
	}
	
	public FontTexture getFontTexture(){
		return fontTexture;
	}
	
	public void setSize(float width, float height){
		scale.x = width / bufferedWidth;
		scale.y = height / bufferedHeight;
	}
	
	public boolean contains(float x, float y){
		return x > position.x && x < position.x + getWidth()
				&& y < position.y + getHeight() && y > position.y;
	}
	
	public void handle(ArrayList<Integer> events){
		if(eventHandler != null)
			for(Integer event : events)
				eventHandler.handle(event);
	}
	
	public EventHandler getEventHandler(){
		return eventHandler;
	}
	
	public void setEventHandler(EventHandler eventHandler){
		this.eventHandler = eventHandler;
	}
	
	public void setPadding(Vector2f padding){
		Vector2f oldpad = this.padding;
		this.padding = padding;
		position.x += padding.x - oldpad.x;
		position.y += padding.y - oldpad.y;
	}
	
	public Vector2f getPadding(){
		return padding;
	}
	
	public float getBufferedWidth(){
		return bufferedWidth;
	}
	
	public float getBufferedHeight(){
		return bufferedHeight;
	}
	
	public float getWidth(){
		return bufferedWidth * scale.x;
	}
	
	public float getHeight(){
		return bufferedHeight * scale.y;
	}
	
	public boolean hasTexture(){
		return texture != null;
	}
	
	public void destroy(){
		mesh.destroy();
	}
	
	public enum GUIType{
		COLOR,
		TEXTURE,
		MESSAGE;
	}
	
	public enum Anchor{
		FREE,
		BOTTOM_LEFT,
		BOTTOM_CENTER,
		BOTTOM_RIGHT,
		CENTER_LEFT,
		CENTER,
		CENTER_RIGHT,
		TOP_LEFT,
		TOP_CENTER,
		TOP_RIGHT;
	}
	
	public static void test(){
		System.out.println("YOLO");
	}
}
