package io.tek256.asset;

import io.tek256.render.Mesh;
import io.tek256.render.Texture;
import io.tek256.render.TextureSheet;
import io.tek256.runtime.Scene;

public class Asset {
	private AssetType type;
	private String resourcePath;
	private Object resource = null;
	private boolean loaded = false;
	
	public Asset(AssetType type, String resourcePath){
		this.resourcePath = resourcePath;
		this.type = type;
	}
	
	public void setType(AssetType type){
		this.type = type;
	}
	
	public AssetType getType(){
		return type;
	}
	
	public void setResourcePath(String path){
		this.resourcePath = path;
	}
	
	public String getResourcePath(){
		return resourcePath;
	}
	
	public void setResource(Object object){
		if(resource != null)
			return;
		resource = object;
	}
	
	public void setLoaded(boolean loaded){
		this.loaded = loaded;
	}
	
	public Object getResource(){
		return resource;
	}
	
	public Mesh getModel(){
		if(!isModel())
			return null;
		return (Mesh)resource;
	}
	
	public TextureSheet getTextureSheet(){
		if(!isTextureSheet())
			return null;
		return (TextureSheet)resource;
	}
	
	public Texture getTexture(){
		if(!isTexture())
			return null;
		return (Texture)resource;
	}
	
	public String getText(){
		if(!isText())
			return null;
		return (String)resource;
	}
	
	public Scene getScene(){
		if(!isScene())
			return null;
		return (Scene)resource;
	}
	
	public boolean isModel(){
		return type == AssetType.MODEL || type == AssetType.MESH;
	}
	
	public boolean isShader(){
		return type == AssetType.SHADER;
	}
	
	public boolean isTexture(){
		return type == AssetType.TEXTURE;
	}
	
	public boolean isTextureSheet(){
		return type == AssetType.TEXTURESHEET;
	}
	
	public boolean isText(){
		return type == AssetType.TEXT;
	}
	
	public boolean isScene(){
		return type == AssetType.SCENE;
	}
	
	public boolean isLoaded(){
		return loaded;
	}
	
	public boolean requiresGL(){
		return isTexture() || isModel() || isShader();
	}
	
	public static enum AssetType{
		MODEL,
		MESH,
		SHADER,
		TEXTURE,
		TEXTURESHEET,
		SCENE,
		TEXT,
	}
}
