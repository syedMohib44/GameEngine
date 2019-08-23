package com.base.engine.core;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;

import com.base.engine.components.Camera;

public class Transform {
		
	// Taking control over box size incement and decrement...
	/*private static float zNear;
	private static float zFar;
	private static float width;
	private static float height; 
	private static float fov;*/  
	
	private Transform parent;
	private Matrix4f parentMatrix;

	private Vector3f translation;
	private Quaternion rotation;
	private Vector3f scale;

	private Vector3f oldPos;
	private Quaternion oldRot;
	private Vector3f oldScale;

	public Transform() {
		translation = new Vector3f(0, 0, 0);
		rotation = new Quaternion(0, 0, 0, 1); // It means the is no rotation on any axis 1 is at W which is not an axis.
		scale = new Vector3f(1, 1, 1);
		
		parentMatrix = new Matrix4f().initIdentity();
	}
	
	
	public void update() {
		if(oldPos != null){
			oldPos.set(translation);
			oldRot.set(rotation);
			oldScale.set(scale);
			}
		
		else{
			oldPos = new Vector3f(0, 0, 0).set(translation).add(1.0f);
			oldRot = new Quaternion(0, 0, 0, 0).set(rotation).mul(0.5f);
			oldScale = new Vector3f(0, 0, 0).set(scale).add(1.0f);
		}
	}
	
	public void lookAt(Vector3f point, Vector3f up)
	{
		rotation = getLookAtRotation(point, up);
	}

	public Quaternion getLookAtRotation(Vector3f point, Vector3f up)
	{
		return new Quaternion(new Matrix4f().initRotation(point.sub(translation).normalized(), up));
	}
	
	public void rotate(Vector3f axis, float angle) {
		rotation = new Quaternion(axis, angle).mul(rotation).normalized();
	}
	
	public boolean hasChanged() {		
		if(parent != null && parent.hasChanged())
			return true;
		
		if(!translation.equals(oldPos))
			return true;
		
		if(!rotation.equals(oldRot))
			return true;
		
		if(!scale.equals(oldScale))
			return true;
		
		return false;
	}

	public Matrix4f getTransformation() {
		Matrix4f translationMatrix = new Matrix4f().initTranslation(translation.getX(), translation.getY(), translation.getZ());
		Matrix4f rotationMatrix = rotation.toRotationMatrix();
		Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());
				
		/*if(oldPos != null){
		oldPos.set(translation);
		oldRot.set(rotation);
		oldScale.set(scale);
		}*/
		//1st it scale then apply rotation and finally set transformation...
		 return getParentMatrix().mul(translationMatrix.mul(rotationMatrix.mul(scaleMatrix)));
	}
	
	private Matrix4f getParentMatrix() {
		if(parent != null && parent.hasChanged()) // Walkup the hirarchy and reach the one that's changed... 
			parentMatrix = parent.getTransformation();
		
		return parentMatrix;
	}
	
	public void setParent(Transform parent) {
		this.parent = parent;
	}
	
	public Vector3f getTransformedPos() {
		return getParentMatrix().transform(translation);
	}
	
	public Quaternion getTransformedRot() {
		Quaternion parentRotation = new Quaternion(0, 0, 0, 1);
		
		if(parent != null) { 
			parentRotation = parent.getTransformedRot();
		}
		
		return parentRotation.mul(rotation);
	}
/*	public Matrix4f getProjectedTransformation(Camera camera) {
			Matrix4f transformMatrix = getTransformation();
		Matrix4f projectionMatrix = new Matrix4f().initProjection(fov, width, height, zNear, zFar);
		
		//Move camera in backward (opposite) direction so it fells like world is moving forward...
		Matrix4f cameraRotation = new Matrix4f().initCamera(camera.getForward(),camera.getUp());
		Matrix4f cameraTranslation = new Matrix4f().initTranslation(-camera.getPos().getX(), -camera.getPos().getY(), -camera.getPos().getZ());
		
		
		return projectionMatrix.mul(cameraRotation.mul(cameraTranslation.mul(transformMatrix)));
		return camera.GetViewProjection().mul(getTransformation());
	}*/
	
	public Vector3f getTranslation() {
		return translation;
	}
	
	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}
	
	//Creating Projection
/*	public static void setProjection(float fov, float width, float height, float zNear, float zFar) {
		Transform.fov = fov;
		Transform.height = height;
		Transform.width = width;
		Transform.zNear = zNear;
		Transform.zFar = zFar;
	}*/
	
	
	public Quaternion getRotation() {
		return rotation;
	}
	
	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}
	
	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
}
