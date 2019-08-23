package com.base.engine.rendering.meshLoading;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.spi.DirStateFactory.Result;

import com.base.engine.core.Util;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Vertex;

public class OBJModel {
	private ArrayList<Vector3f> positions, normals;
	private ArrayList<Vector2f> textCoordinates;
	private ArrayList<OBJIndex> indices;
	
	private boolean hasTextCoords, hasNormals;
	
	public OBJModel(String fileName)	{
		positions = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		textCoordinates = new ArrayList<Vector2f>();
		indices = new ArrayList<OBJIndex>();
		hasTextCoords = hasNormals = false;
		
		 BufferedReader meshReader = null;
		  try {
				meshReader = new BufferedReader(new FileReader(fileName));
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
						positions.add(new Vector3f(Float.valueOf(tokens[1]),
								                             Float.valueOf(tokens[2]), 
										                     Float.valueOf(tokens[3])));
					}
					else if(tokens[0].equals("vt")) {
						textCoordinates.add(new Vector2f(Float.valueOf(tokens[1]),
	                             Float.valueOf(tokens[2])));
					}
					else if(tokens[0].equals("vn")) {
						normals.add(new Vector3f(Float.valueOf(tokens[1]),
	                             Float.valueOf(tokens[2]), 
			                     Float.valueOf(tokens[3])));
					}
					
					//First value is f so we are using checking if it is f then use indices
					//Since we are splitting it by space tokens takes space seperated 
					//three values for square in Blender...
					//In Blend first vertex is labeled as 1 where as in our Mesh first vertex
				    //vertex is labeled as thats why we used -1 in each of them...
					else if(tokens[0].equals("f")) {
						
						for(int i = 0; i < tokens.length - 3; i++) {
						indices.add(parseOBJIndex(tokens[1]));
						indices.add(parseOBJIndex(tokens[2 + i]));
						indices.add(parseOBJIndex(tokens[3 + i]));
						}
						/*indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
						indices.add(Integer.parseInt(tokens[2].split("/")[0]) - 1);
						indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
						
						if(tokens.length > 4) {
							indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
							indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
							indices.add(Integer.parseInt(tokens[4].split("/")[0]) - 1);
						}*/
					}
				} 				
				
				meshReader.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
	}
	
	//Graph Implementation...
	public IndexModel toIndexModel() {
		IndexModel result = new IndexModel();
		IndexModel normalModel = new IndexModel();

		HashMap<OBJIndex, Integer> resultIndexMap = new HashMap<OBJIndex, Integer>();
		HashMap<Integer, Integer> normalIndexMap = new HashMap<Integer	, Integer>();
		HashMap<Integer, Integer> indexMap = new HashMap<Integer	, Integer>();

		
		for (int i = 0; i < indices.size(); i++) {
			OBJIndex currentIndex = indices.get(i);
			
			Vector3f currentPosition = positions.get(currentIndex.vertexIndex);			
			Vector3f currentNormal;
			Vector2f currentTexCoord;
			
			if(hasTextCoords)
				currentTexCoord = textCoordinates.get(currentIndex.texCoordIndex);			
			else
				currentTexCoord = new Vector2f(0, 0);
			
			if(hasNormals)
				currentNormal = normals.get(currentIndex.normalIndex);			
			else
				currentNormal = new Vector3f(0, 0, 0);
			
		
			//Without objindex we need to do this to get previous index... 
			/*	for (int j = 0; j < i; j++) { //Bubble Sort...
				OBJIndex oldIndex = indices.get(j);
				if(currentIndex.vertexIndex == oldIndex.vertexIndex
						&& currentIndex.texCoordIndex == oldIndex.texCoordIndex
						&& currentIndex.normalIndex == oldIndex.normalIndex) {
					
					previousVertexIndex = j;
					break;
				}
			}*/
			
			Integer modelVertexIndex = resultIndexMap.get(currentIndex);

			//If node is not set than value -1
			if(modelVertexIndex == null) {
				resultIndexMap.put(currentIndex, result.getPositions().size());//Put the value to node.
				modelVertexIndex = result.getPositions().size();
				
				result.getPositions().add(currentPosition);
				result.getTextCoords().add(currentTexCoord);
				if(hasNormals)
					result.getNormals().add(currentNormal);
			}			
		
			Integer normalModelIndex = normalIndexMap.get(currentIndex.vertexIndex);
			
			if(normalModel == null) {
				normalIndexMap.put(currentIndex.vertexIndex, normalModel.getPositions().size());//Put the value to node.
				normalModelIndex = normalModel.getPositions().size();
				
				normalModel.getPositions().add(currentPosition);
				normalModel.getTextCoords().add(currentTexCoord);
				normalModel.getNormals().add(currentNormal);
			}
			
			result.getIndices().add(modelVertexIndex);
			normalModel.getIndices().add(normalModelIndex);
			indexMap.put(modelVertexIndex, normalModelIndex);			
		}
		if(!hasNormals) {
			normalModel.calcNormals();
			
			for (int i = 0; i < result.getPositions().size(); i++) {
				result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
				//result.getNormals().get(i).set(normalModel.getNormals().get(indexMap.get(i)));
			}
		}
		return result;
	}
	
	private OBJIndex parseOBJIndex(String token) {
		String[] values = token.split("/");
		OBJIndex result = new OBJIndex();
		result.vertexIndex = Integer.parseInt(values[0]) - 1;
		
		if(values.length > 1) {
			hasTextCoords = true;
			result.texCoordIndex = Integer.parseInt(values[1]) - 1;
			 if(values.length > 2) {
				result.normalIndex = Integer.parseInt(values[2]) - 1;
				hasNormals = true;
			}
		}
		return result;
	}
	
	/*public ArrayList<Vector3f> getPositions(){
		return positions;
	}
	public ArrayList<Vector3f> getNormals(){
		return normals;
	}
	public ArrayList<Vector2f> getTextCoords(){
		return textCoordinates;
	}
	public ArrayList<OBJIndex> getIndices(){
		return indices;
	}	*/
}
