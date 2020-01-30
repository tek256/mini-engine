package io.tek256;

import org.joml.Vector3f;

import io.tek256.render.Material;
import io.tek256.render.Mesh;

public class GameObject extends Transform{
	private boolean renderable = true;
	private Mesh mesh;
	private Material material;
	private DrawType style;
	
	public GameObject(){
		position = new Vector3f();
		rotation = new Vector3f();
		scale = new Vector3f(1f,1f,1f);
		addToScene();
	}
	
	public GameObject(Mesh m){
		this.mesh = m;
		position = new Vector3f();
		rotation = new Vector3f();
		scale = new Vector3f(1f,1f,1f);
		addToScene();
	}
	
	public GameObject(Vector3f position){
		this.position = position;
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1f,1f,1f);
		addToScene();
	}
	
	public GameObject(Mesh mesh, Vector3f position){
		this.mesh = mesh;
		this.position = position;
		this.rotation = new Vector3f();
		this.scale = new Vector3f(1f,1f,1f);
		addToScene();
	}
	
	public GameObject(Vector3f position, Vector3f rotation){
		this.position = position;
		this.rotation = rotation;
		this.scale = new Vector3f(1f,1f,1f);
		addToScene();
	}
	
	public GameObject(Mesh mesh, Vector3f position, Vector3f rotation){
		this.mesh = mesh;
		this.position = position;
		this.rotation = rotation;
		this.scale = new Vector3f(1f,1f,1f);
		addToScene();
	}
	
	public GameObject(Vector3f position, Vector3f rotation, Vector3f scale){
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		addToScene();
	}
	
	public GameObject(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale){
		this.mesh = mesh;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		addToScene();
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
	
	private void addToScene(){
		if(mesh != null)
			Scene.getCurrent().addGameObject(this);
	}
	
	private void updateScene(){
		Scene.getCurrent().removeRenderObject(this);
		if(mesh != null)
			Scene.getCurrent().addRenderObject(this);
	}
	
	public void update(float delta){
		
	}
	
	public void handleInput(){
		
	}
	
	public void setMesh(Mesh mesh){
		if(this.mesh == null)
			addToScene();
		else if(this.mesh != null && this.mesh != mesh)
			updateScene();
		this.mesh = mesh;
	}
	
	public Mesh getMesh(){
		return mesh;
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
