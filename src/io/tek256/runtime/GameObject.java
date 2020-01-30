package io.tek256.runtime;

import org.joml.Vector3f;

import io.tek256.render.Material;
import io.tek256.render.Mesh;

public class GameObject extends Transform{
	protected boolean renderable = true,active = false;
	protected Transform lookAt;
	protected Material material;
	protected DrawType style = DrawType.DEFAULT;
	
	public GameObject(){
		position = new Vector3f();
		rotation = new Vector3f();
		scale = new Vector3f(1f,1f,1f);
	}
	
	public GameObject(Vector3f position){
		this.position = position;
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1f,1f,1f);
	}
	
	public GameObject(Vector3f position, Vector3f rotation){
		this.position = position;
		this.rotation = rotation;
		this.scale = new Vector3f(1f,1f,1f);
	}
	
	public GameObject(Vector3f position, Vector3f rotation, Vector3f scale){
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public void setRenderable(boolean renderable){
		this.renderable = renderable;
	}
	
	public boolean isRenderable(){
		return renderable;
	}
	
	public void setMaterial(Material material){
		this.material = material;
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public boolean hasOwnMaterial(){
		return material != null;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public void update(float delta){
		if(!active)
			return;
	}
	
	public void handleInput(float delta){
		
	}
	
	public DrawType getDrawType(){
		return style;
	}
	
	public void setDrawType(DrawType style){
		this.style = style;
	}
	
	public boolean isBillboard(){
		return style == DrawType.BILLBOARD;
	}
	
	public boolean isDefaultDraw(){
		return style == DrawType.DEFAULT;
	}
	
	public enum DrawType{
		DEFAULT,
		BILLBOARD,
	}
}
