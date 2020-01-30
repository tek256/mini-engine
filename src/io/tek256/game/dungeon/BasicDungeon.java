package io.tek256.game.dungeon;

import java.util.ArrayList;

import io.tek256.game.Dungeon;

public class BasicDungeon extends Dungeon{
	public static int MAX_TRIES = 10000;
	
	public static int maxWidth = 20;
	public static int minWidth = 3;
	public static int maxHeight = 20;
	public static int minHeight = 3;
	
	@Override
	public void generate() {
		this.width = 128;
		this.height = 128;
		ArrayList<Room> generated  = new ArrayList<Room>();
		for(int i=0;i<MAX_TRIES;i++){
			int w = random.nextInt(maxWidth - minWidth) + minWidth;
			int h = random.nextInt(maxHeight - minHeight) + minHeight;
			int x = random.nextInt(this.width);
			int y=  random.nextInt(this.height);
				Room r = new Room(x,y,w,h);
				if(!intersects(generated,r))
					generated.add(r);
		}
		
		rooms =new Room[generated.size()];
		for(int i=0;i<rooms.length;i++)
			rooms[i] = generated.get(i);
	}

}
