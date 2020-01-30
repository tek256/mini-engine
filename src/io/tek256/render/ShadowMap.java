package io.tek256.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {
	private static ShadowMap current = null;
	public static final int SHADOW_MAP_WIDTH = 1024;
	public static final int SHADOW_MAP_HEIGHT = 1024;
	
	private final int fbo;
	private final Texture depthMap;
	
	public ShadowMap(){
		fbo = glGenFramebuffers();
		depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);
		
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
			throw new RuntimeException("Unable to create framebuffer");
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public Texture getDepthTexture(){
		return depthMap;
	}
	
	public int getDepthMapFBO(){
		return fbo;
	}
	
	public void bind(){
		if(current == this) return;
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		current = this;
	}
	
	public void delete(){
		glDeleteFramebuffers(fbo);
		depthMap.destroy();
	}
	
}
