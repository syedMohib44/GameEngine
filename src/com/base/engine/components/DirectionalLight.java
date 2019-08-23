package com.base.engine.components;

import com.base.engine.core.RenderingEngine;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Shader;

//This class contain data for DirectionalLight struct in phongFragment...
public class DirectionalLight extends BaseLight{
	
	private BaseLight base;
	private Vector3f direction;
	
	public DirectionalLight(Vector3f color, float intensity) {
		super(color, intensity);
		
		setShader(new Shader("forward-directional"));
	}
	

	public Vector3f getDirection() {
		return getTransform().getTransformedRot().getForward();
	}
}
