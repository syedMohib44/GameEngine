package com.base.engine.rendering;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;
import com.base.engine.core.Util;
import com.base.engine.rendering.resourceManagement.TextureResource;

public class Texture {
	private static HashMap<String, TextureResource> loadedTextures = new HashMap<String, TextureResource>();
	String fileName;
	private TextureResource resource;
	
	public Texture(String fileName) {
		this.fileName = fileName;
		TextureResource oldResource = loadedTextures.get(fileName);
		if(oldResource != null) {
			resource = oldResource;
			resource.addReference(); //Like Semaphore			
		}
		else {
			resource = loadTexture(fileName);
			loadedTextures.put(fileName, resource);
		}
	}
	
	@Override
	protected void finalize() {	
			////Like Semaphore		  !fileName.isEmpty() 
		if(resource.removeReference() && fileName != null) {
			loadedTextures.remove(fileName);
		}
	}
	
	public void bind() {
		bind(0);
	}
	
	public void bind(int samplerSlot) {
		assert(samplerSlot >= 0 && samplerSlot <= 31);
		glActiveTexture(GL_TEXTURE0 + samplerSlot);
		glBindTexture(GL_TEXTURE_2D, resource.getId());
	}
	
	public int getID() {
		return resource.getId();	
	}
	
	//Loading File...
	private static TextureResource loadTexture(String fileName) {
		String[] splitArray = fileName.split("\\.");
		
		//This gets file extension like .txt .docx
		String ext = splitArray[splitArray.length - 1];
		
		try { 
			
			BufferedImage image = ImageIO.read(new FileInputStream(new File("./res/textures/" + fileName)));
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			
			ByteBuffer buffer = Util.createByteBuffer(image.getHeight() * image.getWidth() * 4);  
			
			boolean hasAlpha = image.getColorModel().hasAlpha();
			
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int pixel = pixels[y * image.getWidth() + x];

					//Apha is highest 8 bits than after 8 bits red then after 8 green and then after 8 blue.
					//Some tips between & and &&...
					/*& is bitwise. && is logical.
					 
					& evaluates both sides of the operation.
					&& evaluates the left side of the operation, if it's true, it continues and evaluates the right side.
					shifting pixel 16 to the right and cast it over 8 bits*/
					buffer.put((byte)(pixel >> 16 & 0xFF));// Red part of pixel.  
					buffer.put((byte)(pixel >> 8 & 0xFF));// Green part of pixel.  
					buffer.put((byte)(pixel & 0xFF));// Blue part of pixel.  
					
					if(hasAlpha)
						buffer.put((byte)(pixel >> 24 & 0xFF));// Aplha part of pixel.
					else
						buffer.put((byte)(0xFF)); //full opacity
				}
			} 
			
			buffer.flip();
						
			TextureResource resource = new TextureResource();
			glBindTexture(GL_TEXTURE_2D, resource.getId());
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);//If we are reading texture component beyond 1.0 or less than 0.0 we should repeat.
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);//This Linear texture gurantees we have linear texture on our object...
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			
			return resource;
		}  
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
}
