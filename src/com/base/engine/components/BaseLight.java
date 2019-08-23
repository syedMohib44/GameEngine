package com.base.engine.components;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.RenderingEngine;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Shader;

//This class contain data for BaseLight struct in phongFragment...
public class BaseLight extends GameComponents{
	private Vector3f color;
	private float intensity;
	private Shader shader;
	
	public BaseLight(Vector3f color, float intensity) {
		this.color = color;
		this.intensity = intensity;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}
	
	public float getIntensity() {
		return intensity;
	}
	
	@Override
	public void addToEngine(CoreEngine engine)  {
		engine.getRenderingEngine().addLight(this);
		//rendringEngine.addDirectionalLight(this);
	}
	
	public Shader getShader() {
		return shader;
	}
	
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
}
