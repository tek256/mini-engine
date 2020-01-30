package io.tek256.game.items;

import io.tek256.game.Inventory;
import io.tek256.game.Inventory.STATS;
import io.tek256.game.Inventory.WearableItem;
import io.tek256.game.Inventory.WearableType;

public class TestArmor extends WearableItem{
	
	public TestArmor(){
		stats = new STATS();
		stats.armor = 10;
		id = 1;
		type = WearableType.CHEST;
	}
	
	@Override
	public void onDrop() {
		System.out.println("Drop @ "+System.currentTimeMillis());
	}

	@Override
	public void onWear() {
		System.out.println("Wear @ "+System.currentTimeMillis());
	}

	@Override
	public void onDestroy() {
		System.out.println("Destroy @ "+System.currentTimeMillis());

	}

	@Override
	public void onPickup() {
		System.out.println("Pickup @ "+System.currentTimeMillis());
	}

}
