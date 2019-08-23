package com.base.engine.components;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Input;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Quaternion;
import com.base.engine.core.RenderingEngine;
import com.base.engine.core.Time;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Window;

public class Camera extends GameComponents{
		
	/*private Vector3f pos;
	private Vector3f forward;
	private Vector3f up;*/
	private Matrix4f projection;
	
	//When ever we initialize this class anywhere all the 
	//attributes in a parameterized constructor will be set in
	//non-parameterized constructor initially position is  null
	//forward direction is 1 and upward direction 1 on y axis
	
	
	public Camera(float fov, float aspect, float zNear, float zFar) {
		
		//If we normalize 0 all values will become 0...
		/*this.pos = new Vector3f(0, 0, 0);
		this.forward = new Vector3f(0, 0, 1).normalized();
		this.up = new Vector3f(0, 1, 0).normalized();*/
		this.projection = new Matrix4f().initPerspective(fov, aspect, zNear, zFar);
		
		//up.normalized();
		//forward.normalized();
	}

	public void move(Vector3f dir, float amt) {
		getTransform().setTranslation(getTransform().getTranslation().add(dir.mul(amt)));
	}
	
	/*public void rotateY(float angle) {
		Vector3f Haxis = yAxis.cross(forward).normalized();
		
		forward = forward.rotate(yAxis, angle).normalized();
		
		up = forward.cross(Haxis).normalized();
	}
	
	public void rotateX(float angle) {
		Vector3f Haxis = yAxis.cross(forward);
		Haxis.normalized();
		
		forward.rotate(Haxis, angle);
		forward.normalized();
		
		up = forward.cross(Haxis);
		up.normalized();
	}
	
	public Vector3f getLeft() {
		Vector3f left = forward.cross(up);
		left.normalized();
		return left;
	}
	
	public Vector3f getRight() {
		Vector3f right = up.cross(forward);
		right.normalized();
		return right;
	}*/
	
	boolean mouseLocked = false;
	Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	
	public Matrix4f GetViewProjection() {
		Matrix4f cameraRotation = getTransform().getTransformedRot().conjugate().toRotationMatrix();//conjugate will invert the rotation...
		Vector3f cameraPos = getTransform().getTransformedPos().mul(-1); 
		
		Matrix4f cameraTranslation = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
		
		return projection.mul(cameraRotation.mul(cameraTranslation));
	}
	
	@Override
	public void addToEngine(CoreEngine engine) {
		engine.getRenderingEngine().addCamera(this);
	}
	
		/*if(Input.getKey(Input.KEY_UP))
			rotateX(-rotAmt);
		
		if(Input.getKey(Input.KEY_DOWN))
			rotateX(rotAmt);
		
		if(Input.getKey(Input.KEY_LEFT))
			rotateY(-rotAmt);
		
		if(Input.getKey(Input.KEY_RIGHT))
			rotateY(rotAmt);*/
	
/*	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getForward() {
		return forward;
	}

	public void setForward(Vector3f forward) {
		this.forward = forward;
	}

	public Vector3f getUp() {
		return up;
	}

	public void setUp(Vector3f up) {
		this.up = up;
	}*/
}
