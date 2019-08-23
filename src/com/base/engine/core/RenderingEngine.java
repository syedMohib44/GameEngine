package com.base.engine.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import java.util.ArrayList;
import java.util.HashMap;

import com.base.engine.components.BaseLight;
import com.base.engine.components.Camera;
import com.base.engine.rendering.*;
import com.base.engine.rendering.resourceManagement.MappedValues;

public class RenderingEngine extends MappedValues{
	
/*	private DirectionalLight ActiveDirectionalLight, directionalLight2;
	private PointLight ActivePointLight;
	private SpotLight spotLight;*/
	
	//private PointLight[] pointLightList;
	
	//*Permanent* Structures	
	/*private ArrayList<DirectionalLight> directionalLights;
	private ArrayList<PointLight> pointLights;*/
	
	//Permanent Structure...
	private ArrayList<BaseLight> lights;
	private HashMap<String, Integer> samplerMap;

	private BaseLight activeLight;
	private Shader forwardAmbient;
	private Camera mainCamera;
/*	private HashMap<String, Vector3f> vector3fHashMap;
	private HashMap<String, Float> floatHashMap;*/

	
	public RenderingEngine() {	
		super();
		lights = new ArrayList<BaseLight>();
		samplerMap = new HashMap<String, Integer>();
		//vector3fHashMap = new HashMap<String, Vector3f>();
		/*directionalLights = new ArrayList<DirectionalLight>();
		pointLights = new ArrayList<PointLight>();*/
	
		samplerMap.put("diffuse", 0);//To put glsl code.
		//vector3fHashMap.put("ambient", new Vector3f(0.1f, 0.1f, 0.1f));
		addVector3f("ambient", new Vector3f(0.1f, 0.1f, 0.1f));
		
		forwardAmbient = new Shader("forward-ambient");
		
	    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// What ever is drawing in clock wise order is front face.
		glFrontFace(GL_CW);
		
		//Which face not to draw and in our case it's back one.
		glCullFace(GL_BACK);
		// Whenever you draw something in openGl by default it draw two things:
		// 1st of this drawing one of them facing us and 2nd it create another 
		// instance of it facing away from us it is good in some cases but if we are
		// drawing a square what is the point of drawing another square that can never 
		// be seen by camera. If we want second face we can add manually.
		glEnable(GL_CULL_FACE);
		
		// This is importent when we are drawing more than one thing.
		// It helps not to draw things on top of each other.
		glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_DEPTH_CLAMP);	
		glEnable(GL_TEXTURE_2D);
		// It do it exponential correction means give us the accurate eye pleasing
		// color it changes the color if its too dark or too light.
		
		//mainCamera = new Camera((float)Math.toRadians(70.0f), (float)Window.getWidth()/(float)Window.getHeight(), 0.01f, 1000.0f);
		
		//ambientLight = new Vector3f(0.1f, 0.1f, 0.1f);
		/*ActiveDirectionalLight = new DirectionalLight(new BaseLight(new Vector3f(0, 0, 1), 0.4f), new Vector3f(1, 1, 1));
		directionalLight2 = new DirectionalLight(new BaseLight(new Vector3f(1, 0, 0), 0.4f), new Vector3f(-1, 1, -1));
		
		int lightFieldWidth = 5; //Width of light spot 
		int lightFieldDepth = 5; //Intensity of light spot
		
		int lightFieldStartX = 0; //Light starts position on x-axis
		int lightFieldStartY = 0; //Light starts position on y-axis
		int lightFieldStepX = 7; //Light moving position on x-axis
		int lightFieldStepY = 7; //Light moving position on x-axis
		
		pointLightList = new PointLight[lightFieldWidth * lightFieldDepth];
		for (int i = 0; i < lightFieldWidth; i++) {
			for (int j = 0; j < lightFieldDepth; j++) {																								//This is position of light which is needed to be changed on x and y axis...								
				pointLightList[i * lightFieldWidth + j] = new PointLight(new BaseLight(new Vector3f(0, 1, 0), 0.4f), new Attenuation(0,0,1), new Vector3f(lightFieldStartX + lightFieldStepX * i, 0, lightFieldStartY + lightFieldStepY * j), 100);	
			}
		}
		
		ActivePointLight = pointLightList[0];new PointLight(new BaseLight(new Vector3f(1, 0, 0), 0.4f), new Attenuation(0,0,1), new Vector3f(5, 0, 5), 100);
		spotLight = new SpotLight(new PointLight(new BaseLight(new Vector3f(0, 1, 1), 0.4f), new Attenuation(0,0,0.1f), new Vector3f(lightFieldStartX, 0, lightFieldStartY), 100), new Vector3f(1, 0, 0), 0.7f);*/
	}																//Color for spot light	
	
/*	private void clearLightList() {
		directionalLights.clear();
		pointLights.clear();
	}*/
	
	public void updateUniformStruct(Transform transform, Material material, Shader shader, String uniformName, String uniformType) {
		throw new IllegalArgumentException(uniformType + " is not a supported type in RenderingEngine");
	}
	
	public void render(GameObject object) {
		//clearScreen();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		/*lights.clear();//clearLightList();		
		object.addToRenderingEngine(this);*/
		
		/*Shader forwardPoint = ForwardPoint.getInstance();
		Shader forwardSpot = ForwardSpot.getInstance();
		Shader forwardDirectional = ForwardDirectional.getInstance(); */
	/*	forwardDirectional.SetRenderingEngine(this);
		forwardPoint.SetRenderingEngine(this);
		forwardSpot.SetRenderingEngine(this);
		*/
		object.render(forwardAmbient, this);
		
		
		glEnable(GL_BLEND);
		//Setting Blending function...
		glBlendFunc(GL_ONE, GL_ONE); //Works as (Existing Color * 1) + (New Color * 1);
		glDepthMask(false); //Disabling lighting to Depth Bufffer...
		glDepthFunc(GL_EQUAL); //Writes or Create the pixle...
		
		for(BaseLight light : lights) {			
			activeLight = light;
			//TODO: "Acitive Light" replacement
			object.render(light.getShader(), this);
		}
	/*	for(DirectionalLight light : directionalLights) {
			ActiveDirectionalLight = light;
			object.render(forwardDirectional);
  		}
		
		for(PointLight light : pointLights) {
			ActivePointLight = light;
			object.render(forwardPoint);
  		}*/
		//Code here will blended into the engine
		/*object.render(forwardDirectional);
		
		DirectionalLight temp = directionalLight;
		directionalLight = directionalLight2;
		directionalLight2 = temp;
		
		object.render(forwardDirectional);
		
		temp = directionalLight;
		directionalLight = directionalLight2;
		directionalLight2 = temp;
		
		//Traverse all the point lights in the list and render it on screen...
		for (int i = 0; i < pointLightList.length; i++) {
			pointLight = pointLightList[i];
			object.render(forwardPoint);			
		}
		
		object.render(forwardSpot);*/
		
		glDepthFunc(GL_LESS);
		glDepthMask(true); //Enabling lighting to Depth Bufffer...
		glEnable(GL_BLEND);

		/*Shader shader = BasicShader.getInstance();
		shader.SetRenderingEngine(this);
		
		object.render(BasicShader.getInstance());*/
		
	}
	public int getSamplerSlot(String samplerSlotName) {
		return samplerMap.get(samplerSlotName);
	}
	
	/*public Vector3f getAmbientLight() {
		return ambientLight;
	}*/
	
	
/*	public DirectionalLight getDirectionalLight() {
		return ActiveDirectionalLight;
	}
	
	public PointLight getPointLight() {
		return ActivePointLight;
	}
	
	public SpotLight getSpotLight() {
		return spotLight;
	}*/
	
	/*public static void clearScreen() {
		// this means bit wise OR the color buffer with depth buffer.
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	private static void unbindTextures() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	private static void setClearColor(Vector3f color) {
		glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);
	}
	
	private static void setTexture(boolean enabled) {
		if(enabled)
			glEnable(GL_TEXTURE_2D);
		else
			glDisable(GL_TEXTURE_2D);
	}*/
	
	public BaseLight getActiveLight() {
		return activeLight;
	}
	
/*	public static void initGraphics() {
	
	}*/
	/*public void addDirectionalLight(DirectionalLight ActiveDirectionalLight) {
		directionalLights.add(ActiveDirectionalLight);
	}
	
	public void addPointLight(PointLight ActivePointLight) {
		pointLights.add(ActivePointLight);
	}*/
	
	public void addLight(BaseLight light) {
		lights.add(light);
	}
	
	public static String getOpenGLVersion() {
		return glGetString(GL_VERSION);
	}

	public void addCamera(Camera camera) {
		mainCamera = camera;
	}
	
	
	public Camera getMainCamera() {
		return mainCamera;
	}

	public void setMainCamera(Camera mainCamera) {
		this.mainCamera = mainCamera;
	}
}
