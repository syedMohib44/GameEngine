package com.src.engine.game;


import com.base.engine.components.BaseLight;
import com.base.engine.components.Camera;
import com.base.engine.components.DirectionalLight;
import com.base.engine.components.FreeLook;
import com.base.engine.components.FreeMove;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.PointLight;
import com.base.engine.components.SpotLight;
import com.base.engine.core.Game;
import com.base.engine.core.GameObject;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Attenuation;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.Vertex;
import com.base.engine.rendering.Window;

public class TestGame extends Game {
	  
        //private Camera camera;
	    //All shaders works from basicshader class now...
	    //private Shader shader;
	    //private Shader shader;
	    //private Transform transform;
	    //private Material material;
	    //private Mesh mesh;
	    //All texture work is done in Material and Basicshader class...
	    //private Texture texture;
	    //PointLight pLight1 = new PointLight(new BaseLight(new Vector3f(1, 0.5f, 0),0.8f), new Attenuation(0, 0, 1), new Vector3f(-2, 0, 5f), 2f);
	    //PointLight pLight2 = new PointLight(new BaseLight(new Vector3f(0, 0.5f, 1),0.8f), new Attenuation(0, 0, 1), new Vector3f(2, 0, 7f), 2f);
	    
	    //SpotLight sLight1 = new SpotLight(new PointLight(new BaseLight(new Vector3f(0f, 1f, 1f),0.8f), new Attenuation(0, 0, 0.1f), new Vector3f(-2, 0, 5f), 30),
	    		//new Vector3f(1,1,1), 0.7f);
	    

		public void init() {
	    	  //2nd parameter takes color... 
	        
	    	//mesh = ResourceLoader.loadMesh("square.obj");
	    	//texture = ResourceLoader.loadTexture("test.png");
	        //shader = new Shader();
	       // shader = PhongShader.getInstance();
	    	//shader = BasicShader.getInstance();
	        //camera = new Camera();
	        //transform = new Transform();
	        
	        float fieldDepth = 10.0f;
	        float fieldWidth = 10.0f;
	        //To add texture we need to add Vector2f in vertices it is necessary because after loading
	        //the text	ure how does it know that to draw texture on 3d triangle it does not know how to 
	        //to draw 2d texture on 3d object so we tell it that where is our texture by Vector2f and 
	        //Vector3f tells it's a 3d object...
	        Vertex[] data = new Vertex[] {new Vertex(new Vector3f(-fieldWidth, 0.0f, -fieldDepth), new Vector2f(0.0f, 0.0f)),
	                                      new Vertex(new Vector3f(-fieldWidth, 0.0f, fieldDepth * 3), new Vector2f(0.0f, 1.0f)),
	                                      new Vertex(new Vector3f(fieldWidth * 3, 0.0f, -fieldDepth), new Vector2f(1.0f, 0.0f)),
	                                      new Vertex(new Vector3f(fieldWidth * 3, 0.0f, fieldDepth * 3), new Vector2f(1.0f, 1.0f))};
	        int[] indices = new int[] {0, 1, 2,
	        		                   2, 1, 3};
	        
	        Vertex[] data2 = new Vertex[] {new Vertex(new Vector3f(-fieldWidth/10, 0.0f, -fieldDepth/10), new Vector2f(0.0f, 0.0f)),
                    new Vertex(new Vector3f(-fieldWidth/10, 0.0f, fieldDepth/10 * 3), new Vector2f(0.0f, 1.0f)),
                    new Vertex(new Vector3f(fieldWidth/10 * 3, 0.0f, -fieldDepth/10), new Vector2f(1.0f, 0.0f)),
                    new Vertex(new Vector3f(fieldWidth/10 * 3, 0.0f, fieldDepth/10 * 3), new Vector2f(1.0f, 1.0f))};
	        int[] indices2 = new int[] {0, 1, 2,
                 2, 1, 3};
	        
	        Mesh mesh2 = new Mesh(data2, indices2, true);
	        
	        Mesh mesh = new Mesh(data, indices, true);
	        Material material = new Material(/*new Texture("test.png"), new Vector3f(1, 1, 1), 1, 8*/);
	        //Material material = new Material(new Texture("test.png"), new Vector3f(1, 1, 1), 1, 8);
	        material.addTexture("diffuse", new Texture("BRICK.jpg"));
	        material.addFloat("specularIntensity", 1);
	        material.addFloat("specularPower", 8);
	        
	        Material material2 = new Material(/*new Texture("test.png"), new Vector3f(1, 1, 1), 1, 8*/);
	        //Material material = new Material(new Texture("test.png"), new Vector3f(1, 1, 1), 1, 8);
	        material2.addTexture("diffuse", new Texture("test.png"));
	        material2.addFloat("specularIntensity", 1);
	        material2.addFloat("specularPower", 8);
	        
	        Mesh tempMesh = new Mesh("Monkey.obj");
	        
	        MeshRenderer meshRenderer = new MeshRenderer(mesh, material);
	        
	        GameObject planeObject = new GameObject();
	        planeObject.addComponents(meshRenderer);
	        planeObject.getTransform().getTranslation().set(0,-1,5);
	        
	        GameObject directionalLightObject = new GameObject();
	        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0, 0, 1), 0.4f);
	        directionalLightObject.addComponents(directionalLight);
	        
	        
	        GameObject pointLightObject = new GameObject();
	        PointLight pointLight = new PointLight(new Vector3f(0, 1, 0), 0.4f, new Attenuation(0, 0, 1));
	        pointLightObject.addComponents(pointLight);
	        
	        SpotLight spotLight = new SpotLight(new Vector3f(0, 1, 1), 0.4f,
	        		new Attenuation(1, 0, 0), 0.7f);
	        GameObject spotLightObject = new GameObject();
	        spotLightObject.addComponents(spotLight);
	        spotLightObject.getTransform().getTranslation().set(5, 0, 5); 
	        spotLightObject.getTransform().setRotation(new Quaternion(new Vector3f(0, 1, 0), (float)Math.toRadians(90.0f))); 
	        
	        addObject(planeObject);
	        addObject(directionalLightObject);
	        addObject(pointLightObject);
	        addObject(spotLightObject);
	        
	        //getRootObject().addChild(new GameObject().addComponents(new Camera((float)Math.toRadians(70.0f), (float)Window.getWidth()/(float)Window.getHeight(), 0.01f, 1000.0f)));
	        
	        GameObject testMesh1 = new GameObject().addComponents(new MeshRenderer(mesh2, material));
	        GameObject testMesh2 = new GameObject().addComponents(new MeshRenderer(mesh2, material));
			//GameObject testMesh3 = new GameObject().addComponents(new LookAtComponent()).addComponents(new MeshRenderer(tempMesh, material));
			GameObject testMesh3 = new GameObject().addComponents(new LookAtComponent()).addComponents(new MeshRenderer(tempMesh, material));
			
	        testMesh1.getTransform().getTranslation().set(0, 2, 0);
	        testMesh1.getTransform().setRotation(new Quaternion(new Vector3f(0, 1, 0), 0.4f));
	        
	        testMesh2.getTransform().getTranslation().set(0, 0, 5);
	        
	        testMesh1.addChild(testMesh2);
	        testMesh2.addChild(new GameObject().addComponents(new FreeLook(0.5f)).addComponents(new FreeMove(10.5f)).addComponents(new Camera((float)Math.toRadians(70.0f), (float)Window.getWidth()/(float)Window.getHeight(), 0.01f, 1000.0f)));
	        addObject(testMesh1);
	        addObject(testMesh3);
	        
	        testMesh3.getTransform().getTranslation().set(5,5,5);
	        testMesh3.getTransform().setRotation(new Quaternion(new Vector3f(0, 1, 0), (float)Math.toRadians(-70.0f)));
	        
	        addObject(new GameObject().addComponents(new MeshRenderer(new Mesh("Monkey.obj"), material2)));
	        
	        directionalLight.getTransform().setRotation(new Quaternion(new Vector3f(1, 0, 0), (float)Math.toRadians(-45)));

	        //getRootObject().addComponents(meshRenderer);
	        //root.addComponents(meshRenderer);
	        
	        //Here camera is initialzed in Transform Class...
	        //Transform.setProjection(70f, Window.getWidth(), Window.getHeight(), 0.1f, 1000);
	        //Transform.setCamera(camera);
	        
	       /* shader.addVertexShader(ResourceLoader.loadShader("basicVertex.vs"));
	        shader.addFragmentShader(ResourceLoader.loadShader("basicFragment.fs"));
	        shader.compileShader();
	        shader.addUniform("transform");*/
	        /*PhongShader.setAmbdientLight(new Vector3f(0.1f, 0.1f, 0.1f));
	        PhongShader.setDirectionalLight(new DirectionalLight(new BaseLight(new Vector3f(1, 1, 1), 0.1f), new Vector3f(1, 1, 1)));
	        
	        PhongShader.setSpotLight(new SpotLight[]{sLight1});
	        
	        PhongShader.setPointLight(new PointLight[] {pLight1, pLight2});*/
	    }
	    
	  /*  public void input() {
	    	camera.input();
	    	root.input();
	        if (Input.getKeyDown(Keyboard.KEY_UP)) {
	            System.out.println("We've just pressed up");
	        }
	        if (Input.getKeyUp(Keyboard.KEY_UP)) {
	            System.out.println("We've just released up");
	        }

	        if (Input.getMouseDown(1)) {
	            System.out.println("We've just pressed right-mouse at "
	                    + Input.getMousePosition().toString());
	        }
	        if (Input.getMouseUp(1)) {
	            System.out.println("We've just released right-mouse");
	        }
	    }

	    float temp = 0.0f;
	    //float tempAmount = 0.0f;
	    public void update() {
	    	root.getTransform().setTranslation(0,  -1, 5);
	    	//root.getTransform().setTranslation(0, -1, 5);
	    	root.update();
	        //tempAmount = (float)(Math.sin(temp));
	        //transform.setTranslation(sinTemp, 0, 5);
	        
	        
	        temp += Time.getDelta();
	        float sinTemp = (float)Math.sin(temp);
	        pLight1.setPosition(new Vector3f(3, 0, 8.0f * (float)(Math.sin(temp) + 1.0/2.0) + 10));
	        pLight2.setPosition(new Vector3f(7, 0, 8.0f * (float)(Math.sin(temp) + 1.0/2.0) + 10));
	        transform.setTranslation(0, -1, 5);
	        
	        //transform.setRotation(0, sinTemp * 180, 0);
	        //transform.setScale(0.7f * sinTemp, 0.7f * sinTemp, 0.7f * sinTemp);
	        //shader.setUniformf("uniformFloat", (float)Math.sin(temp));
	        //sLight1.getPointLight().setPosition(camera.getPos());
	        //sLight1.setDirection(camera.getForward());
	    }

	    public void render() {
	    	root.render();
	    	RenderUtl.setClearColor(Transform.getCamera().getPos().div(2048f).abs());
	    	shader.bind();
	    	shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
	        //shader.bind();
	        
	        //shader.setUniform("transform", transform.getTransformation());
	        //shader.setUniform("transform", transform.getProjectedTransformation());
	        //texture.bind();
	        //mesh.draw();
	    }*/
}
