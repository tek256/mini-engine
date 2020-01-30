package io.tek256.game.items;

import io.tek256.game.Inventory;
import io.tek256.game.Inventory.STATS;
import io.tek256.render.Texture;

public class Potion extends Inventory.Item{

	public Potion(){
		this.texture = Texture.getTexture("potion_red");
		this.stats = new STATS();
		name = "potion";
		id = 2;
		stackable = false;
	}
	
	@Override
	public void onUse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDrop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPickup() {
		// TODO Auto-generated method stub
		
	}
	
}
