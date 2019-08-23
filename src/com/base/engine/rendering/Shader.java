package com.base.engine.rendering;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collector.Characteristics;

import com.base.engine.components.BaseLight;
import com.base.engine.components.DirectionalLight;
import com.base.engine.components.PointLight;
import com.base.engine.components.SpotLight;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.RenderingEngine;
import com.base.engine.core.Transform;
import com.base.engine.core.Util;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.resourceManagement.ShaderResource;
import com.base.engine.rendering.resourceManagement.TextureResource;
// Shader is just like program like we made program on pc's but the difference  is
// it run on graphics cards
public class Shader {
	private static HashMap<String, ShaderResource> loadedShader = new HashMap<>(); 
	
	private ShaderResource resource;
	
	private String fileName; 
	public Shader(String fileName) {
	
		this.fileName = fileName;
		ShaderResource oldResource = loadedShader.get(fileName);
		
		if(oldResource != null) {
			resource = oldResource;
			resource.addReference(); //Like Semaphore			
		}
		else {
			//otherwise create new resource...
			resource = new ShaderResource();		
			
			String vertexShaderText = loadShader(fileName + ".vs");
			String fragmentShaderText = loadShader(fileName + ".fs");
			
			addVertexShader(vertexShaderText);		
			addFragmentShader(fragmentShaderText);
			
			addAllAttributes(vertexShaderText);
			
			compileShader();
			
			addAllUniforms(vertexShaderText);
			addAllUniforms(fragmentShaderText);
		}
	}	
	
	public void bind() {
		glUseProgram(resource.getProgram());
	}
	
	//If data comming from vs or fs files starts from R than it's comming from RenderingEngine class if starts with T then from Transform class if there is no prefix then its from Material...
	public  void updateUniforms(Transform transform, Material material, RenderingEngine rendringEngine) {
		Matrix4f worldMatrix = transform.getTransformation();
		Matrix4f /*projectedMatrix*/MVPMatrix = rendringEngine.getMainCamera().GetViewProjection().mul(worldMatrix);
		
		for (int i = 0; i < resource.getUniformNames().size(); i++) {
			String uniformName = resource.getUniformNames().get(i);
			String uniformType = resource.getUniformTypes().get(i);
			
			if(uniformType.equals("sampler2D"))
			{
				int samplerSlot = rendringEngine.getSamplerSlot(uniformName);
				material.getTexture(uniformName).bind(samplerSlot);
				setUniformi(uniformName, samplerSlot);
			}			
			else if(uniformName.startsWith("T_")) {
				if(uniformName.equals("T_MVP"))
					setUniform(uniformName, MVPMatrix);
				else if(uniformName.equals("T_model"))
					setUniform(uniformName, worldMatrix);
				else 
					throw new IllegalArgumentException(uniformName + " is not valid component of Transform");
			}
			else if(uniformName.startsWith("R_")) {
				String unPrefiexUniformName = uniformName.substring(2);				
				//can add more types like int and decimal if you want...
				if(uniformType.equals("vec3"))
					setUniform(uniformName, rendringEngine.getVector3f(unPrefiexUniformName));
				else if(uniformType.equals("float"))
					setUniformf(uniformName, rendringEngine.getFloat(unPrefiexUniformName));
				else if (uniformType.equals("DirectionalLight"))
						setUniformDirectionalLight(uniformName, (DirectionalLight)rendringEngine.getActiveLight());
				else if (uniformType.equals("PointLight"))
					setUniformPointLight(uniformName, (PointLight)rendringEngine.getActiveLight());
				else if (uniformType.equals("SpotLight"))
					setUniformSpotLight(uniformName, (SpotLight)rendringEngine.getActiveLight());
				else 
					rendringEngine.updateUniformStruct(transform, material, this, uniformName, uniformType);
			}
			else if (uniformName.startsWith("C_")) {
				if(uniformName.equals("C_eyePos"))
					setUniform(uniformName, rendringEngine.getMainCamera().getTransform().getTransformedPos());
				else 
					throw new IllegalArgumentException(uniformName + " is not valid component of Camera");
			}
			
			else {
				if(uniformType.equals("vec3"))
					setUniform(uniformName, material.getVector3f(uniformName));
				else if(uniformType.equals("float"))
					setUniformf(uniformName, material.getFloat(uniformName));
				else 
					throw new IllegalArgumentException(uniformType + " is not a supported type in Material");
			}
		}
	}
	
	private class GLSLStruct{
		public String name;
		public String type;
	}

	//	private HashMap<String, ArrayList<String>[]> findUniformStruct(String shaderText)
	
	private HashMap<String, ArrayList<GLSLStruct>> findUniformStruct(String shaderText){
		
		
		//ArrayList<String>[] listArr = new ArrayList[2];		
		//HashMap<String, ArrayList<String>[]> result = new HashMap<String, ArrayList<String>[]>();
		
		HashMap<String, ArrayList<GLSLStruct>> result = new HashMap<String, ArrayList<GLSLStruct>>();
		
		final String STRUCT_KEYWORD = "struct";
		int structStartLocation = shaderText.indexOf(STRUCT_KEYWORD);
		while(structStartLocation != -1) {//If uniform is not null...
			int nameBegin = structStartLocation + STRUCT_KEYWORD.length() + 1;
			int braceBegin = shaderText.indexOf("{", nameBegin);
			//int end = shaderText.indexOf(";", begin);
			int braceEnd = shaderText.indexOf("}", braceBegin);
			String structName = shaderText.substring(nameBegin, braceBegin).trim();
			
			ArrayList<GLSLStruct> glslStructs = new ArrayList<GLSLStruct>();
			
			//ArrayList<String> structComponentNames = new ArrayList<String>();
			//ArrayList<String> structComponentTypes = new ArrayList<String>();			
			//listArr[0] = structComponentNames;
			//listArr[1] = structComponentTypes;
			
			int componentSemiColonsPos = shaderText.indexOf(";", braceBegin);
			while(componentSemiColonsPos != -1 && componentSemiColonsPos < braceEnd) {
				int componentNameStart = componentSemiColonsPos;
				
				while (!Character.isWhitespace(shaderText.charAt(componentNameStart - 1)))
					componentNameStart--;
				
				int componentTypeEnd = componentNameStart - 1;
				int componentTypeStart = componentTypeEnd;
				
				while (!Character.isWhitespace(shaderText.charAt(componentTypeStart - 1)))
					componentTypeStart--;					
					
				String componentName = shaderText.substring(componentNameStart, componentSemiColonsPos);
				String componentType = shaderText.substring(componentTypeStart, componentTypeEnd);
				//structComponent.add(shaderText.substring(componentNameStart, componentSemiColonsPos));
				System.out.println(componentType);
				
				GLSLStruct glslStruct = new GLSLStruct();
				glslStruct.name = componentName;
				glslStruct.type = componentType;

				glslStructs.add(glslStruct);
				
				glslStructs.add(glslStruct);
				componentSemiColonsPos = shaderText.indexOf(";", componentSemiColonsPos + 1);
			}			
			//result.put(structName, listArr);
			result.put(structName, glslStructs);
			structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation + STRUCT_KEYWORD.length());
		}
		return result;
	}
	
	private void addAllUniforms(String shaderText) {
	
		//HashMap<String, ArrayList<String>[]> structs = findUniformStruct(shaderText);
		HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStruct(shaderText);

		final String UNIFORM_KEYWORD = "uniform";
		int uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD);
		while(uniformStartLocation != -1)
		{
			int begin = uniformStartLocation + UNIFORM_KEYWORD.length() + 1;
			int end = shaderText.indexOf(";", begin);

			String uniformLine = shaderText.substring(begin, end);

			int whiteSpacePos = uniformLine.indexOf(' ');
			String uniformName = uniformLine.substring(whiteSpacePos + 1, uniformLine.length());
			String uniformType = uniformLine.substring(0, whiteSpacePos);

			resource.getUniformNames().add(uniformName);
			resource.getUniformTypes().add(uniformType);
			addUniform(uniformName, uniformType, structs);

			uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation + UNIFORM_KEYWORD.length());
		}
	}
	
	private void addUniform(String uniformName, String uniformType, HashMap<String, ArrayList<GLSLStruct>> structs) {
		boolean addThis = true;
		ArrayList<GLSLStruct> structComponents = structs.get(uniformType);
		
		if(structComponents != null) {
			addThis = false;
			for(GLSLStruct struct : structComponents) {
				addUniform(uniformName + "." + struct.name, struct.type, structs);
			}
		}
		if(!addThis)
			return;
		//Get the uniform location in the memory.
		int uniformLocation = glGetUniformLocation(resource.getProgram(), uniformName);
		// 0xFFFFFFFF is equal -1 we can also write -1 instead
		if(uniformLocation == 0xFFFFFFFF) {
			System.err.println("Error: Could not find uniform " + uniformName);
			new Exception().printStackTrace();
			System.exit(1);
			}
		resource.getUniforms().put(uniformName, uniformLocation);
	}
	
	private void addAllAttributes(String shaderText) {
		
		final String ATTRIBUTE_KEYWORD = "attribute";
		int attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD);
		int attribNumber = 0;
		while(attributeStartLocation != -1)
		{
			int begin = attributeStartLocation + ATTRIBUTE_KEYWORD.length() + 1;
			int end = shaderText.indexOf(";", begin);

			String attributeLine = shaderText.substring(begin, end);
			String attributeName = attributeLine.substring(attributeLine.indexOf(' ') + 1, attributeLine.length());

			SetAttribLocation(attributeName, attribNumber);
			attribNumber++;

			attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD, attributeStartLocation + ATTRIBUTE_KEYWORD.length());
		}
	}
	
	//Previous code...
	/*public void addVertexShaderFromFile(String text) {
		addProgram(loadShader(text), GL_VERTEX_SHADER);
	}
	
	public void addFragmentShaderFromFile(String text) {
		addProgram(loadShader(text), GL_FRAGMENT_SHADER);		
	}
	
	public void addGeometryShaderFromFile(String text) {
		addProgram(loadShader(text), GL_GEOMETRY_SHADER);
	}*/
	
	
	
	
	public void addVertexShader(String text) {
		addProgram(text, GL_VERTEX_SHADER);
	}
	
	public void addGeometryShader(String text) {
		addProgram(text, GL_GEOMETRY_SHADER);
	}
	
	public void addFragmentShader(String text) {
		addProgram(text, GL_FRAGMENT_SHADER);		
	}
	
	// It compiles all the parts like geometry shader part fragment shader part.
	public void compileShader() {
		glLinkProgram(resource.getProgram());
		
		if(glGetProgrami(resource.getProgram(), GL_LINK_STATUS) == 0){
			System.err.println(glGetProgramInfoLog(resource.getProgram(), 1024));
			System.exit(1);
		}
		
		glValidateProgram(resource.getProgram());
		
		if(glGetProgrami(resource.getProgram(), GL_VALIDATE_STATUS) == 0){
			System.err.println(glGetProgramInfoLog(resource.getProgram(), 1024));
			System.exit(1);
		}
	}
	
	private void addProgram(String text, int type) {
		int shader = glCreateShader(type);
		
		if(shader == 0) {
			System.err.println("Shader creation failed: Could not find valid memory location when adding shader");
			System.exit(1);
		}
		
		glShaderSource(shader, text);
		glCompileShader(shader);
		
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0){
			System.err.println(glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}
		
		glAttachShader(resource.getProgram(), shader);
	}
	
	//get uniform integer value...
	public void setUniformi(String uniformName, int value) {
		glUniform1i(resource.getUniforms().get(uniformName), value);
	}
	
	//get uniform float value...
	public void setUniformf(String uniformName, float value) {
		glUniform1f(resource.getUniforms().get(uniformName), value);
	}
	
	// set uniform for vector3f
	public void setUniform(String uniformName, Vector3f value) {
		glUniform3f(resource.getUniforms().get(uniformName), value.getX(), value.getY(), value.getZ());
	}
	
	// set uniform for Matrix4f
	public void setUniform(String uniformName, Matrix4f value) {
		//It is true because we are in row major order... false if column major order... 
		glUniformMatrix4(resource.getUniforms().get(uniformName), true, Util.createFlippedBuffer(value));
	}
	
	
	//Loading File...
	public static String loadShader(String fileName) {
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader shaderReader = null;
		final String INCLUDE_DIRECTIVE = "#include";
		
		//include file.h"
		try {
			shaderReader = new BufferedReader(new FileReader("./res/shaders/" + fileName));
			String line;
			
			while((line = shaderReader.readLine()) != null) {
				
				if(line.startsWith(INCLUDE_DIRECTIVE)) {
					shaderSource.append(loadShader(line.substring(INCLUDE_DIRECTIVE.length() + 2, line.length() - 1)));
				}
					
				else
					shaderSource.append(line).append("\n");
			}
			shaderReader.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return shaderSource.toString();
	}
	
	public void SetAttribLocation(String attributeName, int location) {
		glBindAttribLocation(resource.getProgram(), location, attributeName);
	}
	
	//for Forward DirectionalLight...
	public void setUniformDirectionalLight(String uniformName, DirectionalLight directionalLight) {
		setUniformBaseLight(uniformName + ".base", directionalLight);
		setUniform(uniformName + ".direction", directionalLight.getDirection());
	}
	
	public void setUniformBaseLight(String uniformName, BaseLight baseLight) {
		setUniform(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());
	}
	
	//for Forward PointLight...
	public void setUniformPointLight(String uniformName, PointLight pointLight) {
		setUniformBaseLight(uniformName + ".base", pointLight);
		setUniformf(uniformName + ".atten.constant", pointLight.getAttenuation().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAttenuation().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAttenuation().getExponent());
		setUniform(uniformName + ".position", pointLight.getTransform().getTransformedPos());
		setUniformf(uniformName + ".range", pointLight.getRange());
	}
	
	//for Forward SpotLight...
	public void setUniformSpotLight(String uniformName, SpotLight spotLight) {
	setUniformPointLight(uniformName + ".pointLight", spotLight);
	setUniform(uniformName + ".direction", spotLight.getDirection());
	setUniformf(uniformName + ".cutoff", spotLight.getCutoff());
	}
}