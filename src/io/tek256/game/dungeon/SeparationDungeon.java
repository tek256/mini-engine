package io.tek256.game.dungeon;

import java.util.ArrayList;

import io.tek256.game.Dungeon;

public class SeparationDungeon extends Dungeon {

	public static int minWidth = 5, maxWidth = 10;
	public static int minHeight = 5, maxHeight = 10;

	public static int ITERATIONS = 50;
	
	@Override
	public void generate() {
		int rooms = 60; //min 10 rooms, max 100
		ArrayList<Room> gen = new ArrayList<Room>();
		for(int i=0;i<rooms;i++){
			int w = random.nextInt(maxWidth - minWidth) + minWidth;
			int h = random.nextInt(maxHeight - minHeight) + minHeight;
			gen.add(new Room(0,0,w,h));
 		}
		ArrayList<Room> hasIntersection = new ArrayList<Room>();
		for(int i=1;i<gen.size();i++){ //ignore room 0 as center
			Room room = gen.get(i);
			if(intersects(gen, room))
				hasIntersection.add(room);
		}
		for(int i=0;i<ITERATIONS;i++){
			for(Room room : hasIntersection){
				for(Room o : gen){
					if(!room.equals(o)){
						if(room.intersects(o)){
							solve(room,o);
						}
					}
				}
			}		
		}
		
		int minx = 1000000000;
		int maxx = -1000000000;
		int miny = 100000000;
		int maxy = -100000000;
		
		for(Room r : gen){
			float hw = r.getSize().x / 2;
			float hh = r.getSize().y / 2;
			minx = (int) Math.min(r.getPosition().x - hh, minx);
			miny = (int) Math.min(r.getPosition().y - hw, miny);
			
			maxx = (int) Math.max(r.getPosition().x + hw, minx);
			maxy = (int) Math.max(r.getPosition().y + hh, miny);
		}
		
		for(Room r : gen){
			r.move(Math.abs(minx), Math.abs(miny));
		}
		
		this.width = maxx - minx;
		this.height = maxy - miny;
		
		this.rooms = new Room[gen.size()];
		for(int i=0;i<rooms;i++){
			this.rooms[i] = gen.get(i);
		}
	}
	
	public void solve(Room a, Room b){
		float xo = a.overlapX(b);
		float yo = a.overlapY(b);
		
		boolean l = a.getPosition().x < b.getPosition().x; //left
		boolean d = a.getPosition().y < b.getPosition().y; //down
		
		float min = Math.min(xo, yo);
		
		if(min == xo){
			if(l){
				a.move((int)-xo, 0);
			}else{
				a.move((int)xo, 0);
			}
		}else if(min == yo){
			if(d){
				a.move(0, (int)-yo);
			}else{
				a.move(0, (int)yo);
			}
		}
		
	}

}
