package com.base.engine.rendering;

import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;

public class Vertex {
	
	private Vector3f pos, normal;
	private Vector2f textCoordinate;
	public static final int SIZE = 8;
	
	
	public Vertex(Vector3f pos) {
		this(pos, new Vector2f(0,0));
	}
	
	public Vertex(Vector3f pos, Vector2f textCoordinate) {
		this(pos, textCoordinate, new Vector3f(0, 0, 0));
	}
	
	public Vertex(Vector3f pos, Vector2f textCoordinate, Vector3f normal) {
		this.pos = pos;
		this.textCoordinate = textCoordinate;
		this.normal = normal;
	}
	
	public Vector2f getTextCoordinate() {
		return textCoordinate;
	}

	public void setTextCoordinate(Vector2f textCoordinate) {
		this.textCoordinate = textCoordinate;
	}	
	
	
	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public Vector3f getPos() {
		return pos;
	}
	
	public void setPos(Vector3f pos) {
		this.pos = pos;
	}
}
