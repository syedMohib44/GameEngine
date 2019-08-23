package com.base.engine.core;

public class Vector3f {
	
	float x,y,z;
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f abs() {
		return new Vector3f(Math.abs(x), Math.abs(y), Math.abs(z));
	}
	
	public float length() {
		return (float)(Math.sqrt(x * x + y * y + z * z));
	}
	
	public float Max() {
		return Math.max(x, Math.max(y, z));
	}
	
	public float dot(Vector3f r) {
		return x * r.getX() + y * r.getY() + z * r.getZ();
	}
	
	public String toString() {
		return "(" + x + " " + y + " " + z + ")";
	}
	
	//Swizziling From here 
	public Vector2f getXY() { return new Vector2f(x, y); }	
	public Vector2f getYZ() { return new Vector2f(y, z); }
	public Vector2f getZX() { return new Vector2f(z, x); }

	public Vector2f getYX() { return new Vector2f(y, z); }	
	public Vector2f getZY() { return new Vector2f(z, y); }
	public Vector2f getXZ() { return new Vector2f(x, z); }
	//till here...
	
	public Vector3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	public Vector3f set(Vector3f r) {
		set(r.getX(), r.getY(), r.getZ());
		return this;
	}
	
	public Vector3f cross(Vector3f r) {
		float x_ = y * r.getZ() - z * r.getY();
		float y_ = z * r.getX() - x * r.getZ();
		float z_ = x * r.getY() - y * r.getX();
		
		return new Vector3f(x_, y_, z_);
	}
	
	public Vector3f normalized() {
		float length = length();		
		return new Vector3f(x / length, y / length,	z / length);
	}
	 
	//Rotate a vector by some axis on some angle...
	public Vector3f rotate(Vector3f axis, float angle) {		
		/*Quaternion rotation = new Quaternion().initalRotation(axis, angle);
		//this conjugate removes the extra quaternion parts...
		Quaternion conjugate = rotation.conjugate();
		
		//This W will be our final x y and z axis.
		Quaternion w = rotation.mul(this).mul(conjugate);
		
		x = w.getX();
		y = w.getY(); 
		z = w.getZ();
		
		return this;*/
		//alternative..
		float sinAngle = (float)Math.sin(-angle);
		float cosAngle = (float)Math.cos(-angle);
		
		//equation of quaternion rotation...
		return this.cross(axis.mul(sinAngle)).add( //Rotation on local X
						   this.mul(cosAngle)).add( //Rotation on local Z
						   axis.mul(this.dot(axis.mul(1 - cosAngle)))); //Rotation on local Y

	}
	
	public Vector3f rotate(Quaternion rotation) {		
		//this conjugate removes the extra quaternion parts...
		Quaternion conjugate = rotation.conjugate();
		
		//This W will be our final x y and z axis.
		Quaternion w = rotation.mul(this).mul(conjugate);		
		return new Vector3f(w.getX(), w.getY(), w.getZ());
	}
	
	public Vector3f Lerp(Vector3f dest, float lerpFactor) {
		return dest.sub(this).mul(lerpFactor);
	}
	
	public Vector3f add(Vector3f r) {
		return new Vector3f(x + r.getX(), y + r.getY(), z + r.getZ()); 
	}
	
	public Vector3f add(float r) {
		return new Vector3f(x + r, y + r, z + r); 
	}
	
	public Vector3f sub(Vector3f r) {
		return new Vector3f(x - r.getX(), y - r.getY(), z - r.getZ()); 
	}
	
	public Vector3f sub(float r) {
		return new Vector3f(x - r, y - r, z - r); 
	}
	
	public Vector3f mul(Vector3f r) {
		return new Vector3f(x * r.getX(), y * r.getY(), z * r.getZ()); 
	}
	
	public Vector3f mul(float r) {
		return new Vector3f(x * r, y * r, z * r); 
	}
	
	public Vector3f div(Vector3f r) {
		return new Vector3f(x / r.getX(), y / r.getY(), z / r.getZ()); 
	}
	
	public Vector3f div(float r) {
		return new Vector3f(x / r, y / r, z / r); 
	}
	
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	public boolean equals(Vector3f r) {
		 return x == r.getX() && y == r.getY() && z == r.getZ(); 
	}
}
