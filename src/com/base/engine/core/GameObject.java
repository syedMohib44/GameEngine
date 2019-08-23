package com.base.engine.core;
import java.util.ArrayList;

import com.base.engine.components.GameComponents;
import com.base.engine.rendering.Shader;


// This creates Hirarchy (Scene Graph)...
public class GameObject{
	
	private ArrayList<GameObject> children;
	private ArrayList<GameComponents> components;
	private Transform transform;
	private CoreEngine engine;
	
	public GameObject() {
		children = new ArrayList<GameObject>();
		components = new ArrayList<GameComponents>();
		transform = new Transform();
		engine = null;
	}
	
	public void addChild(GameObject child) {
		children.add(child);
		child.setEngine(engine);
		child.getTransform().setParent(transform);
	}
	
	public GameObject addComponents(GameComponents component) {
		components.add(component);
		component.setParent(this);
		
		return this;
	}
	
	public ArrayList<GameObject> getAllAttached()
	{
		ArrayList<GameObject> result = new ArrayList<GameObject>();

		for(GameObject child : children)
			result.addAll(child.getAllAttached());

		result.add(this);
		return result;
	}
	
	public void inputAll(float delta)
	{
		input(delta);

		for(GameObject child : children)
			child.inputAll(delta);
	}

	public void updateAll(float delta)
	{
		update(delta);

		for(GameObject child : children)
			child.updateAll(delta);
	}

	public void renderAll(Shader shader, RenderingEngine renderingEngine)
	{
		render(shader, renderingEngine);

		for(GameObject child : children)
			child.renderAll(shader, renderingEngine);
	}
	
	public void input(float delta) {
		transform.update();
		//For every gameobject we first need componets for it
		//either we are doing input update or render...
		for(GameComponents component : components)
			component.input(delta);
		
		for (GameObject child : children)
			child.input(delta);
	}
		
	public void update(float delta) {
		for(GameComponents component : components)
			component.update(delta);
		
		for (GameObject child : children) 
			child.update(delta);
	}
	
	public void render(Shader shader, RenderingEngine rendringEngine) {
		for(GameComponents component : components)
			component.render(shader, rendringEngine);
		
		for (GameObject child : children) 
			child.render(shader, rendringEngine);		
	}
	
	public Transform getTransform() {
		return transform;
	}
	
	public void setEngine(CoreEngine engine) {
		if(this.engine != engine) {
			this.engine = engine;
			
			for (GameComponents component : components)
				component.addToEngine(engine);
			
			for (GameObject child : children)
				child.setEngine(engine);
		}
	}
}
