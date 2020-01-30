package io.tek256.game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.joml.Vector2f;

public abstract class Dungeon{
	protected long seed;
	protected int width = 1024,height = 1024;
	protected Room[] rooms;
	
	protected Random random;
	
	public abstract void generate();
	
	public Dungeon(){
		random = new Random(new Date().getTime());
	}
	
	public Dungeon(long seed){
		this.seed = seed;
		random = new Random(seed);
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public long getSeed(){
		return seed;
	}
	
	protected boolean intersects(ArrayList<Room> rooms, Room r){
		for(Room room : rooms)
			if(room.intersects(r))
				return true;
		return false;
	}
	
	public void output(String path){
		BufferedImage i = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		for(Room room : rooms){
			int color = new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)).getRGB();
			int xmin = (int)(room.position.x - (room.size.x / 2));
			int xmax = (int)(room.position.x + (room.size.x / 2));
			int ymin = (int)(room.position.y - (room.size.y / 2));
			int ymax = (int)(room.position.y + (room.size.y / 2));
			for(int x=xmin;x<xmax;x++){
				for(int y=ymin;y<ymax;y++){
					if(x < width && y < height && x > 0 && y > 0)
					i.setRGB(x, y, color);
				}
			}
		}
		try{
			FileOutputStream out = new FileOutputStream(path);
			ImageIO.write(i, "png", out);
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
				
	}
	
	public static class Room{
		protected Vector2f position,size,min,max;
		private boolean finalized = false;
		
		public Room(){
			this(new Vector2f(),new Vector2f());
		}
		
		public Room(int x, int y, int width, int height){
			this(new Vector2f(x,y),new Vector2f(width,height));
		}
		
		public Room(Vector2f position, Vector2f size){
			this.position = position;
			this.size = size;
			updateBounds();
		}
		
		public void move(int x, int y){
			if(finalized)
				return;
			
			position.x += x;
			position.y += y;
			updateBounds();
		}
		
		public boolean isFinalized(){
			return finalized;
		}
		
		public void finalize(){
			finalized = true;
		}
		
		private void updateBounds(){
			if(min == null)
				min = new Vector2f();
			if(max == null)
				max = new Vector2f();
			
			float hw = size.x / 2f,
					hh = size.y / 2f;
			min.x = position.x - hw;
			min.y = position.y - hh;
			
			max.x = position.x + hw;
			max.y = position.y + hh;
		}
		
		public Vector2f getPosition(){
			return position;
		}
		
		public void setPosition(Vector2f position){
			if(finalized)
				return;
			this.position.set(position);
			updateBounds();
		}
		
		public void setX(int x){
			if(finalized)
				return;
			this.position.x = x;
			updateBounds();
		}
		
		public void setY(int y){
			if(finalized)
				return;
			this.position.y = y;
			updateBounds();
		}
		
		public void setSize(Vector2f size){
			if(finalized)
				return;
			this.size.set(size);
			updateBounds();
		}
		
		public Vector2f getSize(){
			return size;
		}
		
		public Vector2f getMin(){
			return min;
		}
		
		public Vector2f getMax(){
			return max;
		}
		
		public boolean intersects(Room o){
			return Math.abs(position.x - o.position.x) < (size.x / 2f) + (o.size.x / 2f) &&
					Math.abs(position.y - o.position.y) < (size.y / 2f) + (o.size.y / 2f);
		}
		
		public float overlapX(Room o){
			return Math.abs(position.x - o.position.x) - ((o.size.x / 2f) + (size.x / 2f));
		}
		
		public float overlapY(Room o){
			return Math.abs(position.y - o.position.y) - ((o.size.y / 2f) + (size.y / 2f));
		}
	}
}
