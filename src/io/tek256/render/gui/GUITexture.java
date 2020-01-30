package io.tek256.render.gui;

import org.joml.Vector3f;

import io.tek256.render.Material;
import io.tek256.render.Mesh;
import io.tek256.render.MeshBuilder;
import io.tek256.render.Texture;

public class GUITexture extends GUIElement {
	public static Mesh guiMesh = MeshBuilder.getPlane(1f,1f);;
	private Texture texture;
	private boolean colorOnly = false,empty = false;	
	
	public GUITexture(Texture texture){
		this.texture = texture;
		init();
	}
	
	public GUITexture(Vector3f color){
		this.color = color;
		init();
	}
	
	public GUITexture(){
		init();
	}
	
	protected void init(){	
		if(texture == null)
			colorOnly = true;
		if(texture == null && color == null)
			empty = true;
	}
	
	public void prep(){
		if(guiMesh.getMaterial() == null)
			guiMesh.setMaterial(new Material());
		guiMesh.getMaterial().setColor(color);
		guiMesh.getMaterial().setTexture(texture);
	}
	
	public void setTexture(Texture texture){
		this.texture = texture;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public Vector3f getColor(){
		return color;
	}
	
	public void setColor(Vector3f color){
		this.color = color;
	}
	
	public AnchorX getAnchorX(){
		return anchorX;
	}
	
	public void setAnchorX(AnchorX anchorX){
		if(this.anchorX == anchorX)
			return;
		this.anchorX = anchorX;
		updatePosition();
	}
	
	public AnchorY getAnchorY(){
		return anchorY;
	}
	
	public void setAnchorY(AnchorY anchorY){
		if(this.anchorY == anchorY)
			return;
		this.anchorY = anchorY;
		updatePosition();
	}
	
	public void setAnchor(AnchorX anchorX, AnchorY anchorY){
		if(this.anchorX == anchorX && this.anchorY == anchorY)
			return;
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		updatePosition();
	}
	
	protected void resized(float width, float height){
		if(scalePosition){
			position.x = (position.x / displayWidth) * width;
			position.y = (position.y / displayHeight) * height;
		}
		if(scaleSize){
			scale.x *= width / displayWidth;
			scale.y *= height / displayHeight;
		}
	}
	
	public boolean isEmpty(){
		return empty;
	}
	
	public boolean isTexture(){
		return !colorOnly;
	}
	
	public boolean isColor(){
		return colorOnly;
	}

	@Override
	public void destroy(){
		GUIElement.elements.remove(this);
	}
}