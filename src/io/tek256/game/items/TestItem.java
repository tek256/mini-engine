package io.tek256.game.items;

import io.tek256.game.Inventory;
import io.tek256.game.Inventory.STATS;
import io.tek256.render.Texture;

public class TestItem extends Inventory.Item{

	public TestItem(){
		this.texture = Texture.getTexture("sword");
		this.stats = new STATS();
		stats.attack = 100;
		name = "sword";
		this.useCount = 0;
		id = 1;
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
