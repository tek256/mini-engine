package io.tek256.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class FontTexture {
	public static FontTexture defaultFont;
	private final Font font;
	private final String charset;
	private final Map<Character, CharInfo> charMap;
	private Texture texture;
	private int width,height;
	
	public FontTexture(Font font, String charset){
		this.font = font;
		this.charset = charset;
		charMap = new HashMap<Character, CharInfo>();
		build();
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public Font getFont(){
		return font;
	}
	
	public CharInfo getCharInfo(char c){
		return charMap.get(c);
	}
	
	public String getAllChars(){
		CharsetEncoder ce = Charset.forName(charset).newEncoder();
		StringBuilder out = new StringBuilder();
		for(char c = 0; c < Character.MAX_VALUE; c++){
			if(ce.canEncode(c))
				out.append(c);
		}
		return out.toString();
	}
	
	private void build(){
		BufferedImage img = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		g2.setFont(font);
		FontMetrics metrics = g2.getFontMetrics();
		
		String chars = getAllChars();
		width = 0;
		height = 0;
		for(char c: chars.toCharArray()){
			CharInfo ci = new CharInfo(width, metrics.charWidth(c));
			charMap.put(c, ci);
			width += ci.getWidth();
			height = Math.max(height, metrics.getHeight());
		}
		g2.dispose();
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2 = img.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setFont(font);
		metrics = g2.getFontMetrics();
		g2.setColor(Color.WHITE);
		g2.drawString(chars, 0, metrics.getAscent());
		g2.dispose();
		
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(img, "png", out);
			ImageIO.write(img, "png", new File("res/teset.png"));
			out.flush();
			InputStream in = new ByteArrayInputStream(out.toByteArray());
			texture = new Texture(in);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public class CharInfo{
		private final int offset,width;
		
		public CharInfo(int offset, int width){
			this.offset = offset;
			this.width = width;
		}
		
		public int getOffset(){
			return offset;
		}
		
		public int getWidth(){
			return width;
		}
	}
}
