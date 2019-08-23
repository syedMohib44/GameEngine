package com.base.engine.rendering;

import java.util.HashMap;

import com.base.engine.core.Vector3f;
import com.base.engine.rendering.resourceManagement.MappedValues;

public class Material extends MappedValues{
	
	private HashMap<String, Texture> textureHashMap;
	//private HashMap<String, Vector3f> vector3fHashMap;
	//private HashMap<String, Float> floatHashMap;

	private Texture texture;
	private Vector3f color;
	private float specularIntensity, specularPower;
	
	public Material() {
		super();
		textureHashMap = new HashMap<String, Texture>();
		//vector3fHashMap = new HashMap<String, Vector3f>();
		//floatHashMap = new HashMap<String, Float>();
		 
	}
	
	public void addTexture(String name, Texture texture) {
		textureHashMap.put(name, texture);
	}
	
/*	public void addVector3f(String name, Vector3f vector3f) {
		vector3fHashMap.put(name, vector3f);
	}
	
	public void addFloat(String name, float floatValue) {
		floatHashMap.put(name, floatValue);
	}*/
	
	
	public Texture getTexture(String name) {
		Texture result = textureHashMap.get(name);
		
		if(result != null)
			return result;
		
		return new Texture("test.png");
	}
	
	/*public Vector3f getVector3f(String name) {
		Vector3f result = vector3fHashMap.get(name);
		
		if(result != null)
			return result;
		
		return new Vector3f(0, 0, 0);
	}
	
	public float getFloat(String name) {
		Float result = floatHashMap.get(name);
		
		if(result != null)
			return result;
		
		return 0;
	}*/
}
