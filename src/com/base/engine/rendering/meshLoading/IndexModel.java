package com.base.engine.rendering.meshLoading;

import java.util.ArrayList;

import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Vertex;

public class IndexModel {
	private ArrayList<Vector3f> positions, normals;
	private ArrayList<Vector2f> textCoordinates;
	private ArrayList<Integer> indices;
	
	public IndexModel()	{
		positions = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		textCoordinates = new ArrayList<Vector2f>();
		indices = new ArrayList<Integer>();
	}
	
	public void calcNormals() {
		for (int i = 0; i < indices.size(); i+=3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);
			
			//This will give the right line of the triangle from top to right...
			Vector3f v1 = positions.get(i1).sub(positions.get(i0));
			
			//This will give the left line of the triangle from top to left...
			Vector3f v2 = positions.get(i2).sub(positions.get(i0));
			
			//this will calculate the up direction...
			Vector3f normal = v1.cross(v2).normalized();
			
			normals.get(i0).set(normals.get(i0).add(normal));
			normals.get(i1).set(normals.get(i1).add(normal));
			normals.get(i2).set(normals.get(i2).add(normal));
		}
		
		//This will normalize all the above vertices...
		for (int i = 0; i < normals.size(); i++) {
			normals.get(i).set(normals.get(i).normalized());
		}
	}
	
	public ArrayList<Vector3f> getPositions(){
		return positions;
	}
	public ArrayList<Vector3f> getNormals(){
		return normals;
	}
	public ArrayList<Vector2f> getTextCoords(){
		return textCoordinates;
	}
	public ArrayList<Integer> getIndices(){
		return indices;
	}	
}
