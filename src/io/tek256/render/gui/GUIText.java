package io.tek256.render.gui;

import io.tek256.render.Mesh;
import io.tek256.runtime.Scene;

public class GUIText extends GUIElement{
	private Mesh mesh;
	private GUIFont font = GUIFont.DEFAULT;
	private String text;
	private int fontSize = 3;
	
	public GUIText(GUIFont font){
		this.font = font;
	}
	
	public GUIText(GUIFont font, String text){
		this.text = text;
		this.font = font;
	}
	
	public GUIText(String text){
		this.text = text;
		this.font = GUIFont.DEFAULT;
	}
	public GUIFont getFont(){
		return font;
	}
	
	public void draw(){
		font.drawString(targetPosition, rotation, scale, color, fontSize, text);
	}
	
	public void setFont(GUIFont font){
		if(this.font == font)
			return;
		this.font = font;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public int getFontSize(){
		return font.getFontSize(fontSize);
	}
	
	public void setFontSize(int fontSize){
		this.fontSize = fontSize;
	}
	
	public Mesh getMesh(){
		return mesh;
	}	
	
	@Override
	public void destroy(){
		elements.remove(this);
		Scene.getCurrent().remove(this);
	}
}