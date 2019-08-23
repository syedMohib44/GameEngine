package com.base.engine.rendering;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.base.engine.core.Util;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.meshLoading.IndexModel;
import com.base.engine.rendering.meshLoading.OBJModel;
import com.base.engine.rendering.resourceManagement.MeshResources;

public class Mesh {
	
	private static HashMap<String, MeshResources> loadedModels = new HashMap<String, MeshResources>();
	private MeshResources resource;
	private String fileName;
	
	public Mesh(String fileName) {
		this.fileName = fileName;
		MeshResources oldResource = loadedModels.get(fileName);
		if(oldResource != null) {
			resource = oldResource;
			resource.addReference(); //Like Semaphore			
		}
		else {
			loadMesh(fileName);
			loadedModels.put(fileName, resource);
		}
	}
	
	public Mesh(Vertex[] vertices, int[] indices) {
		this(vertices, indices, false);
	}
	
	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals) {
		fileName = "";
		addVetices(vertices, indices, calcNormals);
	}
	
	@Override
	protected void finalize() {	
			////Like Semaphore		  !fileName.isEmpty() 
		if(resource.removeReference() && fileName != null) {
			loadedModels.remove(fileName);
		}
	}
	
	private void addVetices(Vertex[] vertices, int[] indices, boolean calcNormals) {
		if(calcNormals) {
			calcNormals(vertices, indices);
		}
		
		resource = new MeshResources(indices.length);
		
		glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
		glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);
		
		 glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
		 glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
	}
	
	public void draw() {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
		// Vertex is multiplied by 4 because each float is of 4 bytes
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);

		//After adding Vector2f in Vertices class we have more data to adjust our position we
		// have and extra corner so we put 2 we really need to worry about still same floating point
		// data which is 6 parameter which is offset where our texture coordinate stop so how long we 
		//in our computer memory storing our matrix we have 4 floating point number for
		//our position and 2 floating point number for our texture coordinates so how many bytes 
		//3 floating points take a floating point number is of 4 byte so 4 * 3 = 12bytes to get vertex
		//texture coordinates.
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
		
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
		//Because we are using indices so we donot use this method...
		//glDrawArrays(GL_TRIANGLES, 0, size);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
		glDrawElements(GL_TRIANGLES, resource.getSize(), GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
	}
	
	private  void calcNormals(Vertex[] vertices, int[] indices) {
		for (int i = 0; i < indices.length; i+=3) {
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];
			
			//This will give the right line of the triangle from top to right...
			Vector3f v1 = vertices[i1].getPos().sub(vertices[i0].getPos());
			
			//This will give the left line of the triangle from top to left...
			Vector3f v2 = vertices[i2].getPos().sub(vertices[i0].getPos());
			
			//this will calculate the up direction...
			Vector3f normal = v1.cross(v2).normalized();
			
			vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
			vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
			vertices[i2].setNormal(vertices[i2].getNormal().add(normal));
		}
		
		//This will normalize all the above vertices...
		for (int i = 0; i < vertices.length; i++) {
			vertices[i].setNormal(vertices[i].getNormal().normalized());
		}
	}
	
	//Loading File...
	private Mesh loadMesh(String fileName) {
		  String[] splitArray = fileName.split("\\.");
		  //Gets the last splitted string from file name...
		  String extension = splitArray[splitArray.length - 1];
		  
		  
		  //Checks if splitted file name is not obj or its not .obj file extension then show error message...
		  if(!extension.equals("obj")) {
			  System.err.println("Error: File format not supported for mesh data: " + extension);
			  new Exception().printStackTrace();
			  System.exit(1);
		  }

		  OBJModel test = new OBJModel("./res/models/" + fileName);
		  IndexModel model = test.toIndexModel();
		  model.calcNormals();
		  
		  ArrayList<Vertex> vertices = new ArrayList<>();

		  for (int i = 0; i < model.getPositions().size(); i++) {
			vertices.add(new Vertex(model.getPositions().get(i),
									model.getTextCoords().get(i),
									model.getNormals().get(i)));
		}
		  
		  Vertex[] vertexData = new Vertex[vertices.size()];
			vertices.toArray(vertexData);
			
			Integer[] indexData = new Integer[model.getIndices().size()];
			model.getIndices().toArray(indexData);
			
			addVetices(vertexData, Util.toIntArray(indexData), false);
		  /*  ArrayList<Vertex> vertices = new ArrayList<>();
		  ArrayList<Integer> indices = new ArrayList<>();
		  BufferedReader meshReader = null;
		  try {
				meshReader = new BufferedReader(new FileReader("./res/models/" + fileName));
				String line;
				while((line = meshReader.readLine()) != null) {
					String[] tokens = line.split(" ");
					tokens = Util.removeEmptyStrings(tokens);
					
					// continue means go on dont worry about it...
					if(tokens.length == 0 || tokens[0].equals(("#")))
							continue;
					
					//First value is f so we are using checking if it is f then use Vertex
					//Since we are splitting it by space tokens takes space seperated 
					//three values for square in Blender...
					else if(tokens[0].equals("v")) {
						vertices.add(new Vertex(new Vector3f(Float.valueOf(tokens[1]),
								                             Float.valueOf(tokens[2]), 
										                     Float.valueOf(tokens[3]))));
					}
					
					//First value is f so we are using checking if it is f then use indices
					//Since we are splitting it by space tokens takes space seperated 
					//three values for square in Blender...
					//In Blend first vertex is labeled as 1 where as in our Mesh first vertex
				    //vertex is labeled as thats why we used -1 in each of them...
					else if(tokens[0].equals("f")) {
						indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
						indices.add(Integer.parseInt(tokens[2].split("/")[0]) - 1);
						indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
						
						if(tokens.length > 4) {
							indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
							indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
							indices.add(Integer.parseInt(tokens[4].split("/")[0]) - 1);
						}
					}
				} 				
				
				meshReader.close();
				
				//All the things are took as array in mesh so we have
				//converted all data into array by making our own functions
				//in Util class...
				Vertex[] vertexData = new Vertex[vertices.size()];
				vertices.toArray(vertexData);
				
				Integer[] indexData = new Integer[indices.size()];
				indices.toArray(indexData);
				
				addVetices(vertexData, Util.toIntArray(indexData), true);
			} 
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}*/
		  return null;
		  }
}
