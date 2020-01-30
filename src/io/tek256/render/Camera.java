package io.tek256.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import io.tek256.GameObject;
import io.tek256.Transform;
import io.tek256.Window;

public class Camera {
	private static Camera current = null;
	private Vector3f position,rotation;
	private float fov = 60f,near = 0.01f,far = 100f,width,height;
	public float maxX = 80f,minX = -80f;
	private Matrix4f projection,model,modelView,
		modelLight,modelLightView,view,lightView,
		orthoProjection,ortho2D,orthoModel;
	
	public Camera(){
		position = new Vector3f();
		rotation = new Vector3f();
		this.width = Window.getWidth();
		this.height = Window.getHeight();
		init();
	}
	
	public Camera(float fov){
		position = new Vector3f();
		rotation = new Vector3f();
		this.fov = fov;
		this.width = Window.getWidth();
		this.height = Window.getHeight();
		init();
	}
	
	public Camera(float fov, float near, float far){
		this.fov = fov;
		this.near = near;
		this.far = far;
		this.width = Window.getWidth();
		this.height = Window.getHeight();
		init();
	}
	
	private void init(){
		projection = new Matrix4f();
		model = new Matrix4f();
		modelView = new Matrix4f();
		modelLight = new Matrix4f();
		modelLightView = new Matrix4f();
		view = new Matrix4f();
		lightView = new Matrix4f();
		orthoProjection = new Matrix4f();
		ortho2D = new Matrix4f(); 
		orthoModel = new Matrix4f();
		if(current == null)
			current = this;
	}
	
	public void makeCurrent(){
		current = this;
	}
	
	public static Camera getCurrent(){
		if(current == null)
			current = new Camera();
		return current;
	}
	
	public Vector3f getPosition(){
		return position;
	}
	
	public Vector3f getRotation(){
		return rotation;
	}
	
	public void setPosition(float x, float y, float z){
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public void setPosition(Vector3f position){
		setPosition(position.x,position.y,position.z);
	}
	
	public void setRotation(float x, float y, float z){
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}
	
	public void setRotation(Vector3f rotation){
		setRotation(rotation.x,rotation.y,rotation.z);
	}
	
	public void resized(){
		width = Window.getWidth();
		height = Window.getHeight();
	}
	
	public Matrix4f getProjection(){
		return projection;
	}
	
	public Matrix4f getProjection(float fov, float width, float height, float near, float far){
		this.near = near;
		this.far = far;
		this.fov = fov;
		projection.identity();
		projection.perspective(fov, width/height, near, far);
		return projection;
	}
	
	public Matrix4f getProjection(float fov, float width, float height){
		this.fov = fov;
		projection.identity();
		projection.perspective(fov, width/height, near, far);
		return projection;
	}
	
	public Matrix4f getProjection(float width, float height){
		projection.identity();
		projection.perspective(fov, width/height, near, far);
		return projection;
	}
	
	public void updateProjection(){
		projection = getProjectionMatrix(fov, Window.getWidth(), Window.getHeight(), near, far);
	}
	
	public Matrix4f getProjectionMatrix(float fov, float width, float height, float near, float far){
		float len = far - near;
		float aspect = width/height;
		float yscale = 1 / (float)Math.tan(Math.toRadians(fov / 2));
		float xscale = yscale / aspect;
		
		if(projection == null)
			projection = new Matrix4f();
		projection.identity();
		projection.m00 = xscale;
		projection.m11 = yscale;
		projection.m22 = -((far +  near) / len);
		projection.m23 = -1;
		projection.m32 = -((2 * near * far) / len);
		projection.m33 = 0;
		return projection;
	}
	
	public Matrix4f getProjectionMatrix(){
		return getProjectionMatrix(fov, Window.getWidth(), Window.getHeight(), near, far);
	}
	
	public Matrix4f getOrthoProjection(){
		return orthoProjection;
	}
	
	public Matrix4f updateOrthoProjection(float left, float right, float bottom, float top, float near, float far){
		orthoProjection.identity();
		orthoProjection.setOrtho(left, right, bottom, top, near, far);
		return orthoProjection;
	}
	
	public Matrix4f updateOrthoProjection(float left, float right, float bottom, float top){
		orthoProjection.identity();
		orthoProjection.setOrtho(left, right, bottom, top, near, far);
		return orthoProjection;
	}
	
	public Matrix4f getView(){
		return view;
	}
	
	public Matrix4f updateView(){
		return updateViewMatrix(position,rotation,view);
	}
	
	public Matrix4f getLightView(){
		return lightView;
	}
	
	public void setLightView(Matrix4f lightView){
		this.lightView = lightView;
	}
	
	public Matrix4f updateLightView(Vector3f position, Vector3f rotation){
		return updateViewMatrix(position,rotation,lightView);
	}
	
	public static Matrix4f updateViewMatrix(Vector3f position, Vector3f rotation, Matrix4f mat){
		mat.identity();
		mat.rotate((float)Math.toRadians(rotation.x),new Vector3f(1,0,0));
		mat.rotate((float)Math.toRadians(rotation.y),new Vector3f(0,1,0));
		mat.rotate((float)Math.toRadians(rotation.z),new Vector3f(0,0,1));
		mat.translate(-position.x,-position.y,-position.z);
		return mat;
	}
	
	public Matrix4f getOrtho2DProjection(float left, float right, float bottom, float top){
		ortho2D.identity();
		ortho2D.setOrtho2D(left, right, bottom, top);
		return ortho2D;
	}
	
	public Matrix4f getModelViewMatrix(Transform gameObject, Matrix4f view){
		Vector3f rotation = gameObject.getRotation();
		Vector3f position = gameObject.getPosition();
		model.identity();
		model.scale(gameObject.getScale());
		model.translate(position);
		model.rotateX((float)Math.toRadians(-rotation.x));
		model.rotateY((float)Math.toRadians(-rotation.y));
		model.rotateZ((float)Math.toRadians(-rotation.z));
		modelView.set(view);
		return modelView.mul(model);
	}
	
	public Matrix4f getModelLightView(Transform gameObject, Matrix4f mat){
		Vector3f rotation = gameObject.getRotation();
		modelLight.identity();
		modelLight.translate(gameObject.getPosition());
		modelLight.rotate((float)Math.toRadians(rotation.x),new Vector3f(1,0,0));
		modelLight.rotate((float)Math.toRadians(rotation.y),new Vector3f(0,1,0));
		modelLight.rotate((float)Math.toRadians(rotation.z),new Vector3f(0,0,1));
		modelLightView.set(mat);
		return modelLightView.mul(modelLight);
	}
	
	public Matrix4f getOrthoProjectionModel(Transform gameObject, Matrix4f orthoMatrix){
		Vector3f rotation = gameObject.getRotation();
		model.identity();
		model.translate(gameObject.getPosition());
		model.rotateX((float)Math.toRadians(-rotation.x));
		model.rotateY((float)Math.toRadians(-rotation.y));
		model.rotateZ((float)Math.toRadians(-rotation.z));
		model.scale(gameObject.getScale());
		orthoModel.set(orthoMatrix);
		return orthoModel.mul(model);
	}
	
}
