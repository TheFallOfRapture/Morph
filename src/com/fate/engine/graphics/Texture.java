package com.fate.engine.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class Texture {
	private TextureResource resource;
	private static HashMap<String, TextureResource> loadedTextures = new HashMap<String, TextureResource>();
	private String filename;
	
	public Texture(String filename) {
		this.filename = filename;
		TextureResource oldResource = loadedTextures.get(filename);
		if (oldResource != null) {
			this.resource = oldResource;
			resource.addReference();
		} else {
			resource = loadTexture(filename);
			loadedTextures.put(filename, resource);
		}
	}
	
	private TextureResource loadTexture(String filename) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getClassLoader().getResourceAsStream(filename));
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			
			ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
			
			boolean hasAlpha = image.getColorModel().hasAlpha();
			
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int color = pixels[x + y * image.getWidth()];
					buffer.put((byte)((color >> 16) & 0xff));
					buffer.put((byte)((color >> 8) & 0xff));
					buffer.put((byte)(color & 0xff));
					
					if (hasAlpha)
						buffer.put((byte)((color >> 24) & 0xff));
					else
						buffer.put((byte)(0xff));
				}
			}
			
			buffer.flip();
			
			int ID = glGenTextures();
			
			glBindTexture(GL_TEXTURE_2D, ID);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			
			return new TextureResource(ID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void destroy() {
		resource.removeReference();
	}
	
	public void bind() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, resource.getID());
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public String getFilename() {
		return filename;
	}
}
