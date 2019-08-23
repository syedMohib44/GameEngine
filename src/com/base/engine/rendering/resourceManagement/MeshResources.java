package com.base.engine.rendering.resourceManagement;
import static org.lwjgl.opengl.GL15.*;

public class MeshResources {
	
	private int vbo; // vbo works as Pointer  
	private int ibo; //Index buffer Object...
	private int size;
	private int refCount;
	
	public MeshResources(int size) {
		vbo = glGenBuffers();
		ibo = glGenBuffers();
		this.size = size;
		this.refCount = 1;
	}
	
	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		glDeleteBuffers(vbo);
		glDeleteBuffers(ibo);
	}
	
	public void addReference() {
		refCount++;
	}
	
	public boolean removeReference() {
		refCount--;
		
		return refCount == 0;
	}

	public int getVbo() {
		return vbo;
	}

	public int getIbo() {
		return ibo;
	}

	public int getSize() {
		return size;
	}
}
