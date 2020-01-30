package io.tek256.render.gui;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.tek256.runtime.Transform;

public abstract class GUIElement extends Transform {
	protected static ArrayList<GUIElement> elements = new ArrayList<GUIElement>();
	public static float displayWidth = 0,displayHeight = 0;
	protected Vector2f padding,targetPosition;
	protected boolean visible = true;
	protected boolean scalePosition = true, scaleSize = true;
	protected Vector3f color;
	protected AnchorX anchorX;
	protected AnchorY anchorY;
	
	public GUIElement(){
		init();
	}
	
	private void init(){
		padding = new Vector2f();
		targetPosition = new Vector2f();
		if(color == null)
			color = new Vector3f(1f,1f,1f);
		
		if(anchorX == null)
			anchorX = AnchorX.FREE;
		if(anchorY == null)
			anchorY = AnchorY.FREE;
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
	
	public void updatePosition(){
		if(targetPosition == null)
			targetPosition = new Vector2f();
		Vector3f gs = getGameSize();
		if(anchorX != AnchorX.FREE){
			switch(anchorX){
			case LEFT:
				position.x = gs.x / 2f;
				break;
			case RIGHT:
				position.x = displayWidth - gs.x /2f;
				break;
			case CENTER:
				position.x = (displayWidth - gs.x) / 2;
				break;
			case FREE:
				break;
			}
		}
		if(anchorY != AnchorY.FREE){
			switch(anchorY){
			case TOP:
				position.y = gs.y / 2f;
				break;
			case CENTER:
				position.y = (displayHeight - gs.y) / 2f;
				break;
			case BOTTOM:
				position.y = displayHeight - (gs.y / 2f);
				break;
			case FREE:
				break;
			}
		}
		targetPosition.x = position.x + (gs.x/2f) + padding.x;
		targetPosition.y = position.y + (gs.y/2f) + padding.y;
	}
	
	public void setPadding(float x, float y){
		if(padding != null){
			if(padding.x == x && padding.y == y)
				return;
			padding.set(x,y);
		}else{
			padding = new Vector2f(x,y);
		}
		updatePosition();
	}
	
	public void setPadding(Vector2f padding){
		if(this.padding.equals(padding))
			return;
		this.padding.set(padding);
		updatePosition();
	}
	
	public Vector2f getPadding(){
		return padding;
	}
	
	public boolean isSizeScale(){
		return scaleSize;
	}
	
	public boolean isPositionScale(){
		return scalePosition;
	}
	
	public void setPositionScale(boolean scale){
		scalePosition = scale;
	}
	
	public void setSizeScale(boolean scale){
		scaleSize = scale;
	}
	
	public Vector2f getTargetPosition(){
		return targetPosition;
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public static void resizedDisplay(float width, float height){
		for(GUIElement e : elements){
			if(e.scalePosition || e.scaleSize)
				e.resized(width,height);
		}
		displayWidth = width;
		displayHeight = height;
	}
	
	public static void destroyAll(){
		GUITexture.guiMesh.destroy();
		for(GUIElement e: elements)
			e.destroy();
	}
	
	public static enum AnchorX{
		FREE,
		LEFT,
		RIGHT,
		CENTER,
	};
	
	public static enum AnchorY{
		FREE,
		TOP,
		CENTER,
		BOTTOM,
	};
	
	public abstract void destroy();
}