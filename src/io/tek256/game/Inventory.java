package io.tek256.game;

import java.util.Arrays;

import org.joml.Vector2f;

import io.tek256.Window;
import io.tek256.render.Texture;
import io.tek256.render.gui.GUIText;
import io.tek256.render.gui.GUITexture;
import io.tek256.runtime.Scene;

public class Inventory {
	public static int SLOT_COUNT = 6;
	
	//0 = boots 1 = legs 2 = gloves 3 = chest 4 = helm
	public static int WEARABLE_COUNT = 5;
	public static int MAX_SLOT_STACK = 64;
	
	private Player player;
	
	private Item[] slots; 
	private WearableItem[] wearables;
	
	private GUISlot[] guiSlots;
	
	protected int selection = 0;
	
	public static final EMPTY_WEARABLE EMPTY_WEARABLE = new EMPTY_WEARABLE();
	public static final EMPTY_ITEM EMPTY_ITEM = new EMPTY_ITEM();
	
	private static STATS tempStats = new STATS();
	
	public Inventory(Player player){
		this.player = player;
		slots = new Item[SLOT_COUNT];
		guiSlots = new GUISlot[SLOT_COUNT];
		wearables = new WearableItem[WEARABLE_COUNT];
		
		for(int i=0;i<SLOT_COUNT;i++)
			slots[i] = EMPTY_ITEM;
		
		GUISlot.BACKGROUND = new GUITexture(Texture.getTexture("res/textures/slot_background.png"));
		GUISlot.BACKGROUND.setSize(GUISlot.WIDTH, GUISlot.HEIGHT, 0);
		GUISlot.SELECTOR = new GUITexture(Texture.getTexture("res/textures/slot_select.png"));
		GUISlot.SELECTOR.setSize(GUISlot.WIDTH, GUISlot.HEIGHT, 0f);
		GUISlot.ITEM = new GUITexture();
		GUISlot.ITEM.setSize(GUISlot.WIDTH * GUISlot.ITEM_RATIO, GUISlot.HEIGHT * GUISlot.ITEM_RATIO, 0);
		GUISlot.TEXT = new GUIText("");
		
		for(int i=0;i<SLOT_COUNT;i++)
			guiSlots[i] = new GUISlot(i);
		
		for(int i=0;i<WEARABLE_COUNT;i++)
			wearables[i] = EMPTY_WEARABLE;
		
		select(selection);
	}
	
	public void createSceneDrop(Item item){
		
	}
	
	public void createSceneDrop(Item item, int count){
		
	}
	
	public void select(int selection){
		if(this.selection == selection)
			return;
		guiSlots[this.selection].selected = false;
		this.selection = selection;
		guiSlots[selection].selected = true;
		player.setItemStats(getItemStats());
	}
	
	public int getSelection(){
		return selection;
	}
	
	public void add(Item item){
		if(hasEmpty())
			add(item,firstOpenSlot());
	}
	
	public void add(Item item, int slot){
		if(slot < 0 || slot > SLOT_COUNT)
			return;
		slots[slot].onDrop();
		item.onPickup();
		slots[slot] = item;
		guiSlots[slot].setItem(item);
		player.setItemStats(getItemStats());
	}
	
	public STATS getItemStats(){
		tempStats.clear();
		tempStats.set(slots[selection].stats);
		return tempStats;
	}
	
	public STATS getArmorStats(){
		tempStats.clear();
		for(int i=0;i<WEARABLE_COUNT;i++)
			tempStats.add(wearables[i].stats);
		return tempStats;
	}
	
	public void use(int slot){
		slots[slot].onUse();
		slots[slot] = EMPTY_ITEM;
		guiSlots[slot].clearTexture();
	}
	
	public void dropAll(int slot){
		slots[slot].onDrop();
		createSceneDrop(slots[slot],slots[slot].count);
		slots[slot] = EMPTY_ITEM;
		guiSlots[slot].clearTexture();
	}
	
	public void drop(int slot){
		slots[slot].onDrop();
		createSceneDrop(slots[slot]);
		if(slots[slot].count > 1)
			slots[slot].count --;
		if(slots[slot].count == 0)
			slots[slot] = EMPTY_ITEM;
		guiSlots[slot].clearTexture();
	}
	
	public void removeWearable(int wearableSlot){
		wearables[wearableSlot].onDrop();
		wearables[wearableSlot] = EMPTY_WEARABLE;
		player.setArmorStats(getArmorStats());
	}
	
	public int nextSlot(){
		if(selection == SLOT_COUNT -1)
			return 0;
		return selection+1;
	}
	
	public int nextFullSlot(){
		for(int i=selection+1;i<SLOT_COUNT;i++)
			if(slots[i] != EMPTY_ITEM)
				return i;
		if(selection > 1)
			for(int i=selection-1;i>0;i--)
				if(slots[i] != EMPTY_ITEM)
					return i;
		return selection;
	}
	
	public int nextEmptySlot(){
		for(int i=selection+1;i<SLOT_COUNT;i++)
			if(slots[i] == EMPTY_ITEM)
				return i;
		if(selection > 1)
			for(int i =0;i>0;i--)
				if(slots[i] == EMPTY_ITEM)
					return i;
		return selection;
	}
	
	public int lastFullSlot(){
		for(int i=selection-1;i>0;i--)
			if(slots[i] != EMPTY_ITEM)
				return i;
		if(selection < SLOT_COUNT-2)
			for(int i=selection+1;i<SLOT_COUNT;i++)
				if(slots[i] != EMPTY_ITEM)
					return i;
		return selection;
	}
	
	public int lastSlot(){
		return (selection == 0) ? SLOT_COUNT-1: selection-1; 
	}
	
	public boolean hasEmpty(){
		for(int i=0;i<SLOT_COUNT;i++)
			if(slots[i] == EMPTY_ITEM)
				return true;
		return false;
	}
	
	public boolean isFull(){
		return !hasEmpty();
	}
	
	public Item getSelectedItem(){
		return slots[selection];
	}
	
	public Item getSlot(int index){
		return slots[index];
	}
	
	public void setWearable(WearableItem item){
		int affected = item.getType().slot;
		wearables[affected].onDrop();
		item.onWear();
		wearables[affected] = item;
		player.setArmorStats(getArmorStats());
	}
	
	public void equip(WearableItem item){
		int affected = item.getType().slot;
		wearables[affected].onDrop();
		item.onWear();
		wearables[affected] = item;
		player.setArmorStats(getArmorStats());
	}
	
	public WearableItem getWearable(int index){
		return wearables[index];
	}
	
	public WearableItem getWearable(WearableType type){
		return wearables[type.getSlot()];
	}
	
	public WearableItem[] getWearables(){
		return wearables.clone();
	}
	
	public boolean isWearing(WearableType type){
		return wearables[type.getSlot()] != EMPTY_WEARABLE;
	}
	
	public boolean isWearing(int slot){
		return wearables[slot] != EMPTY_WEARABLE;
	}
	
	public boolean isWearing(WearableItem item){
		for(WearableItem w : wearables)
			if(w == item)
				return true;
		return false;
	}
	
	public int[] getFreeItemSlots(){
		int[] count = new int[0];
		for(int i=0;i<SLOT_COUNT;i++)
			if(slots[i] == EMPTY_ITEM){
				count = Arrays.copyOf(count, count.length+1);
				count[count.length-1] = i;
			}
		return count;		
	}
	
	public int getFreeItemSlotCount(){
		int c = 0;
		for(int i=0;i<SLOT_COUNT;i++)
			if(slots[i] == EMPTY_ITEM)
				c++; //lol
		return c;
	}
	
	public int[] getFullItemSlots(){
		int[] count = new int[0];
		for(int i=0;i<SLOT_COUNT;i++)
			if(slots[i] != EMPTY_ITEM){
				count = Arrays.copyOf(count, count.length+1);
				count[count.length-1] = i; 
			}
		return count;
	}
	
	public int getFullItemSlotCount(){
		int c = 0;
		for(int i=0;i<SLOT_COUNT;i++)
			if(slots[i] != EMPTY_ITEM)
				c++;
		return c;
	}

	public String[] getItemNames(){
		String[] names = new String[SLOT_COUNT];
		for(int i=0;i<SLOT_COUNT;i++)
			names[i] = slots[i].getName();
		return names;
	}
	
	public int[] getItemIds(){
		int[] ids = new int[SLOT_COUNT];
		for(int i=0;i<SLOT_COUNT;i++)
			ids[i] = slots[i].getId();
		return ids;
	}
	
	public String[] getWearableNames(){
		String[] names = new String[WEARABLE_COUNT];
		for(int i=0;i<WEARABLE_COUNT;i++)
			names[i] = wearables[i].getName();
		return names;
	}
	
	public int[] getWearableIds(){
		int[] ids = new int[WEARABLE_COUNT];
		for(int i=0;i<WEARABLE_COUNT;i++)
			ids[i] = wearables[i].getId();
		return ids;
	}
	
	public String[] getFormattedItemStrings(){
		String[] strings = new String[SLOT_COUNT];
		for(int i=0;i<SLOT_COUNT;i++)
			strings[i] = slots[i].getFormattedString();
		return strings;
	}
	
	public String[] getFormattedWearableStrings(){
		String[] strings = new String[WEARABLE_COUNT];
		for(int i=0;i<WEARABLE_COUNT;i++)
			strings[i] = slots[i].getFormattedString();
		return strings;
	}
	
	public boolean contains(Item item){
		for(int i=0;i<SLOT_COUNT;i++)
			if(slots[i] == item)
				return true;
		return false;
	}

	public boolean containsMultiple(Item item){
		int x=0;
		for(int i=0;i<SLOT_COUNT;i++)
			if(slots[i] == item)
				x++;
		return x > 1;
	}
	
	public int getCountOf(Item item){
		int x=0;
		for(int i=0;i<SLOT_COUNT;i++)
			if(slots[i] == item)
				x++;
		return x;
	}
	
	public int[] getSlotsWith(Item item){
		int[] c = new int[0];
		for(int i=0;i<SLOT_COUNT;i++){
			if(slots[i] == item){
				c = Arrays.copyOf(c, c.length+1);
				c[c.length-1] = i;
			}
		}
		return c;
	}
	
	public int[] getSlotsWithout(Item item){
		int[] c = new int[0];
		for(int i=0;i<SLOT_COUNT;i++){
			if(slots[i] != item){
				c = Arrays.copyOf(c, c.length+1);
				c[c.length-1] = i;
			}
		}
		return c;
	}
	
	public int getCountWithin(Item item){
		int[] c = getSlotsWith(item);
		int s = 0;
		for(int i=0;i<c.length;i++){
			s += slots[c[i]].count;
		}
		return s;
	}
	
	public Item[] getSlots(){
		return slots.clone();
	}
	
	public int firstOpenSlot(){
		for(int i=0;i<SLOT_COUNT;i++){
			if(slots[i] == EMPTY_ITEM)
				return i;
		}
		return -1;
	}
	
	public int lastOpenSlot(){
		for(int i=SLOT_COUNT;i>0;i--)
			if(slots[i] == EMPTY_ITEM)
				return i;
		return -1;
	}
	
	public STATS getTotalStats(){
		STATS stats = new STATS();
		for(WearableItem wear : wearables){
			stats.add(wear.stats);
		}
		return stats;
	}
	
	public GUISlot[] getGUISlots(){
		return guiSlots;
	}
	
	public GUISlot getGUISlot(int index){
		return guiSlots[index];
	}
	
	public static enum WearableType{
		BOOTS(0),
		LEGS(1),
		GLOVES(2),
		CHEST(3),
		HELM(4);
		
		int slot;
		
		WearableType(int slot){
			this.slot = slot;
		}
		
		public int getSlot(){
			return slot;
		}
	}
	
	public static class EMPTY_WEARABLE extends WearableItem{

		public EMPTY_WEARABLE(){
			name = "EMPTY";
			stats = new STATS();
		}
		
		@Override
		public void onDrop() {
		}

		@Override
		public void onWear() {
			
		}

		@Override
		public void onDestroy() {
			
		}

		@Override
		public void onPickup() {
			System.out.println("empty");
		}
	}
	
	public abstract static class WearableItem{
		protected WearableType type;
		protected int id;
		protected String name;
		protected STATS stats;
		protected Texture texture;
		
		public WearableType getType(){
			return type;
		}
		
		public int getId(){
			return id;
		}
		
		public String getName(){
			return name;
		}
		
		public STATS getStats(){
			return stats;
		}
		
		public Texture getTexture(){
			return texture;
		}
		
		public abstract void onDrop();
		public abstract void onWear();
		public abstract void onDestroy();
		public abstract void onPickup();
	}
	
	public static class STATS{ 
		public int attack,armor,magic_resist,magic,dexterity,accuracy;
		
		public STATS(){
			attack = armor = magic_resist = magic = dexterity = accuracy = 0;
		}
		
		public STATS(int attack, int armor, int magic_resist, int magic, int dexterity, int accuracy){
			this.attack = attack;
			this.armor = armor;
			this.magic_resist = magic_resist;
			this.magic = magic;
			this.dexterity = dexterity;
			this.accuracy = accuracy;
		}
		
		public void set(int attack, int armor, int magic_resist, int magic, int dexterity, int accuracy){
			this.attack = attack;
			this.armor = armor;
			this.magic_resist = magic_resist;
			this.magic = magic;
			this.dexterity = dexterity;
			this.accuracy = accuracy;
		}
		
		public void set(STATS stats){
			this.attack = stats.attack;
			this.armor = stats.armor;
			this.magic_resist = stats.magic_resist;
			this.magic = stats.magic;
			this.dexterity = stats.dexterity;
			this.accuracy = stats.accuracy;
		}
		
		public void add(STATS stats){
			this.attack += stats.attack;
			this.armor += stats.armor;
			this.magic_resist += stats.magic_resist;
			this.magic += stats.magic;
			this.dexterity += stats.dexterity;
			this.accuracy += stats.accuracy;
		}
		
		public void sub(STATS stats){
			this.attack -= stats.attack;
			this.armor -= stats.armor;
			this.magic_resist -= stats.magic_resist;
			this.magic -= stats.magic;
			this.dexterity -= stats.dexterity;
			this.accuracy -= stats.accuracy;
		}
		
		public void scale(float scale){
			this.attack *= scale;
			this.armor *= scale;
			this.magic_resist *= scale;
			this.magic *= scale;
			this.dexterity *= scale;
			this.accuracy *= scale;
		}
		
		public void scale(float attack, float armor, float magic_resist, float magic, float dexterity, float accuracy){
			this.attack *= attack;
			this.armor *= armor;
			this.magic_resist *= magic_resist;
			this.magic *= magic;
			this.dexterity *= dexterity;
			this.accuracy *= accuracy;
		}
		
		public void scale(STATS stats){
			this.attack *= stats.attack;
			this.armor *= stats.armor;
			this.magic_resist *= stats.magic_resist;
			this.magic *= stats.magic;
			this.dexterity *= stats.dexterity;
			this.accuracy *= stats.accuracy;
		}
		
		public void clear(){
			attack = armor = magic_resist = magic = dexterity = accuracy = 0;
		}
		
		public STATS clone(){
			return new STATS(attack, armor, magic_resist, magic, dexterity, accuracy);
		}
		
		@Override
		public String toString(){
			return "STATS{ Attack: "+attack+", Armor: "+armor+
					", Magic Resist: "+magic_resist+", Magic: "+magic+
					", Dexterity: "+dexterity+", Accuracy: "+accuracy+"}";
		}
	}
	
	public static class EMPTY_ITEM extends Item{
		public EMPTY_ITEM(){
			name = "EMPTY";
			stats = new STATS();
		}
		
		@Override
		public void onUse() {
			System.out.println("uhh, nothing here");
		}

		@Override
		public void onDrop() {
			System.out.println("dropping nothing rip");
		}

		@Override
		public void onPickup() {
			System.out.println("opposite logic op");
		}
	}
	
	public abstract static class Item{
		protected int id;
		protected String name;
		protected int count = 0;
		protected int useCount = 1; //individual consumption
		protected boolean wearable = false,stackable = true;
		protected ITEM_TYPE type;
		protected STATS stats;
		protected Texture texture;
		
		public ITEM_TYPE getType(){
			return type;
		}
		
		public String getName(){
			return name;
		}
		
		public int getId(){
			return id;
		}
		
		public int getCount(){
			return count;
		}
		
		public boolean isWearable(){
			return wearable;
		}
		
		public int getUseCount(){
			return useCount;
		}
		
		public Texture getTexture(){
			return texture;
		}
		
		public boolean isStackable(){
			return stackable;
		}
		
		public String getFormattedString(){
			return ""+id+" "+name;
		}
		
		//wearable ? -> to wearableItem.java : to consume
		public abstract void onUse();
		public abstract void onDrop();
		public abstract void onPickup();
	}
	
	public static enum ITEM_TYPE{
		CONSUMABLE,
		WEAPON,
		DECORATIVE
	}
	
	public static class GUISlot{
		public static float WIDTH = 50f;
		public static float HEIGHT = 50f;
		public static float ITEM_RATIO = 0.8f;
		
		public static GUITexture SELECTOR;
		public static GUITexture BACKGROUND,ITEM;
		public static GUIText TEXT;
		
		private String text;
		private Texture itemTexture;
		
		public Vector2f textOffset,backgroundOffset,itemOffset;
		
		protected boolean hasItem = false;
		protected boolean hasText = false;
		protected boolean selected = false;
		
		public GUISlot(Item item){
			this.itemTexture = item.getTexture();
			init();
		}
		
		public GUISlot(int index){
			float neg_offset_w = (Inventory.SLOT_COUNT / 2) * WIDTH;
			backgroundOffset = new Vector2f((Window.getWidth()/2f)+(index*WIDTH)-neg_offset_w, 0);
			itemOffset = new Vector2f(backgroundOffset);
			textOffset = new Vector2f(backgroundOffset.x, HEIGHT); //text to bottom left of the slot
			itemOffset.x += WIDTH * (1 - ITEM_RATIO) / 2;
			itemOffset.y += HEIGHT * (1 - ITEM_RATIO) / 2;
			init();
		}
		
		public GUISlot(){
			init();
		}
		
		public void prep(){
			BACKGROUND.setPadding(backgroundOffset.x, backgroundOffset.y);
			
			ITEM.setTexture(itemTexture);
			ITEM.setPadding(itemOffset.x, itemOffset.y);
			
			TEXT.setText(text);
			TEXT.setPadding(textOffset.x,textOffset.y);
			TEXT.updatePosition();
			
			if(selected){
				SELECTOR.setPadding(backgroundOffset.x, backgroundOffset.y);
				SELECTOR.setZ(0.2f);
			}
		}
		
		public void setItem(Item item){
			setTexture(item.getTexture());
			if(item.isStackable())
				setText(""+item.count);
		}
		
		public void setText(String text){
			this.text = text;
			hasText = !text.equals("");
		}
		
		public String getText(){
			return text;
		}
		
		public void setTexture(Texture texture){
			itemTexture = texture;
			if(itemTexture != null)
				hasItem = true;
		}
		
		public void clearTexture(){
			itemTexture = null;
			hasItem = false;
		}
		
		public boolean hasItem(){
			return hasItem;
		}
		
		public boolean hasText(){
			return hasText;
		}
		
		public boolean isSelected(){
			return selected;
		}
		
		public Texture getTexture(){
			return itemTexture;
		}
		
		private void init(){
			if(ITEM == null){
				ITEM = new GUITexture();
			}
			if(BACKGROUND == null){
				BACKGROUND = new GUITexture();
			}
			if(TEXT == null){
				TEXT = new GUIText("");
			}
			text = "";
			if(itemTexture != null)
				hasItem = true;
			
		}
	}
}
