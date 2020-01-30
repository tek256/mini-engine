package io.tek256.game;

public abstract class Item {
	private static int ITEM_IDS = 0;
	private int id = genId();
	private String name = "";
	
	public Item(){
	}
	
	public Item(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	public abstract void pickup();
	public abstract void use();
	public abstract void drop();
	
	private static int genId(){
		return ITEM_IDS++;
	}
}
