package com.base.engine.components;

import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Attenuation;
import com.base.engine.rendering.Shader;

public class SpotLight extends PointLight{
	
	private PointLight pointLight;
	float cutoff;
	
	
	public SpotLight(Vector3f color, float intensity, Attenuation attenuation, float cutoff) {
		super(color, intensity, attenuation);
		this.cutoff = cutoff;
		
		setShader(new Shader("forward-spot"));
	}
	
	/*public PointLight getPointLight() {
		return pointLight;
	}
	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}*/
	public Vector3f getDirection() {
		return getTransform().getTransformedRot().getForward();
	}
	
	public float getCutoff() {
		return cutoff;
	}
	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}
}
