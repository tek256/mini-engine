package io.tek256.game;

import org.joml.Vector2f;

import io.tek256.Util;
import io.tek256.game.items.Potion;
import io.tek256.game.items.TestArmor;
import io.tek256.game.items.TestItem;
import io.tek256.input.Keyboard;
import io.tek256.net.Net;
import io.tek256.render.TextureSheet;
import io.tek256.render.gui.GUIElement.AnchorX;
import io.tek256.render.gui.GUIElement.AnchorY;
import io.tek256.runtime.Scene;
import io.tek256.render.gui.GUIText;
import io.tek256.render.Texture;

public class Console {
	private Game parent;
	private GUIText consoleLabel,consoleLine;
	private GUIText[] consoleLines;
	
	private boolean allowConsole = false;
	private boolean canBackspace = false;
	private boolean backspaceRepeat = false;
	private boolean backspace = false;
	private float backspaceEnterRepeat = 0.3f;
	private float consoleTimer;
	private float consoleLifeTime = 2f;
	private float backspaceRate = 0.1f;
	private float backspaceLife = 0;
	
	public TextureSheet target;
	private int curTex = 0;
	private int lines = 5;
	
	private StringBuilder cl;
	
	public Console(Game parent){
		this.parent = parent;
		consoleLabel = new GUIText(">");
		consoleLabel.setAnchor(AnchorX.LEFT,AnchorY.BOTTOM);
		consoleLabel.setPadding(new Vector2f(40f,-40f));
		consoleLine = new GUIText("");
		consoleLine.setAnchor(AnchorX.LEFT, AnchorY.BOTTOM);
		consoleLine.setPadding(new Vector2f(75f,-40f));
		cl = new StringBuilder();
		
		
		setLineCount(lines);
		
		Scene.getCurrent().add(consoleLine,consoleLabel);
		Scene.getCurrent().add(consoleLines);
	}
	
	public void handle(String line){
		if(line.startsWith("/")){
			String cmd = line.substring(1);
			String val = "";
			if(line.contains(" ")){
				cmd = cmd.split(" ")[0];
			if(line.substring(1).split(" ").length > 1)
				val = line.substring(line.indexOf(" ")+1).trim();
			}
			switch(cmd){
			case "out":
				if(target != null && !val.equals("")){
					target.saveMappings(val);
					addConsoleLine("Saving mappings to:"+val);
				}else{
					if(val.equals(""))
						addConsoleLine("No path to write target to");
					else if(target == null)
						addConsoleLine("No target to write");
				}
				break;
			case "n":
				if(target == null){
					addConsoleLine("No target sheet selected");
				}else{
					target.name(val, curTex);
					curTex++;
					parent.tset.setTexture(target.getSubTexture(curTex));
				}
				break;
			case "b":
				if(target != null){
					curTex = (curTex > 0) ? curTex-1 : 0;
					parent.c.getMesh().getMaterial().setTexture(target.getSubTexture(curTex));
					addConsoleLine("Current Texture:"+curTex);
				}
				break;
			case "gn":
				if(target != null){
					if(val.equals("")){
						addConsoleLine(curTex+": "+target.getName(curTex));
					}else if(val.toLowerCase().equals("all")){
						StringBuilder[] lines = new StringBuilder[consoleLines.length];
						if(target.getNamedLength() < lines.length){
							lines = new StringBuilder[target.getNamedLength()];
						}
						for(int i=0;i<lines.length;i++)
							lines[i] = new StringBuilder();
						
						String[] keyset = target.getNamedKeyset();
						for(int i=0;i<keyset.length;i++){
							int toline = (int)i % lines.length;
							int index = target.getNameIndex(keyset[i]);
							lines[toline].append(keyset[i]+":"+index+" ");
						}
						for(int i=0;i<lines.length;i++)
							addConsoleLine(lines[i].toString());
					}else if(val.toLowerCase().equals("count")){
						addConsoleLine("Listing count: "+target.getNamedLength());
					}
				}
				break;
			case "t":
				if(!val.equals("")){
					target = TextureSheet.getSheet(val);
					if(target != null)
						addConsoleLine("Target set to: "+val);
					else
						addConsoleLine("Target unable to be found");
				}
				break;
			case "s":
				if(target != null){
					if(!val.equals("")){
						curTex = Util.getInt(val);
						addConsoleLine("Current Texture set to: "+curTex);
						Texture t = target.getSubTexture(curTex);
						parent.c.getMesh().getMaterial().setTexture(t);
					}
				}else
					addConsoleLine("Cannot change texture without target sheet");
				break;
			case "cls":
				clearConsole();
				break;
			case "fs":
				if(!val.equals("")){
					parent.text.setFontSize(Util.getInt(val));
				}
				break;
			case "gg":
				addConsoleLine(target.getTexture().getPath());
				break;
			case "pos":
				addConsoleLine("Player position:"+Util.asString(Player.getPlayerPosition()));
				break;
			case "inv":
				if(val.equals("")){ //no params
					String[] lns = Player.getInstance().getInventory().getFormattedItemStrings();
					StringBuilder oln = new StringBuilder();
					//because I can't do math without food
					oln.append(lns[0]+" "+lns[1]);
					addConsoleLine(oln.toString());
					oln.setLength(0);
					oln.append(lns[2]+" "+lns[3]);
					addConsoleLine(oln.toString());
					oln.setLength(0);
					oln.append(lns[4]+" "+lns[5]);
					addConsoleLine(oln.toString());
					oln.setLength(0);
				}else if(!val.equals("")){ //params
					if(val.equals("w")){
						for(String ln : Player.getInstance().getInventory().getFormattedWearableStrings())
							addConsoleLine(ln);
					}else{
						int slot = Util.getInt(val);
						addConsoleLine(Player.getInstance().getInventory().getSlot(slot).getFormattedString());
					}
				}
				break;
			case "eq":
				if(!val.equals("")){
					if(val.equals("test")){
						Player.getInstance().getInventory().equip(new TestArmor());
					}else if(val.equals("sword")){
						Player.getInstance().getInventory().add(new TestItem());
					}else if(val.equals("potion")){
						Player.getInstance().getInventory().add(new Potion());
					}
				}else{
					addConsoleLine(Player.getInstance().getInventory().getArmorStats().toString());
				}
				break;
			case "rmw":
				if(!val.equals("")){
					int rm = Util.getInt(val);
					Player.getInstance().getInventory().removeWearable(rm);
					addConsoleLine("Removed: "+rm+".");
				}
				break;
			case "set_clf":
				if(!val.equals("")){
					consoleLifeTime = Util.getFloat(val);
					addConsoleLine("Console fade lifetime set to: "+consoleLifeTime+" seconds");
				}
				break;
			case "set_mcl":
				if(!val.equals("")){
					int nl = Util.getInt(val);
					setLineCount(nl);
					addConsoleLine("Console lines set to: "+nl);
				}
				break;
			case "gen":
				if(!val.equals("")){
					parent.dungeon.generate();
					parent.dungeon.output(val);
				}else{
					parent.dungeon.generate();
					parent.dungeon.output("test.png");
				}
				break;
			case "exit":
				parent.end();
			break;
			case "ping":
				if(!val.equals("")){
					addConsoleLine(val+" "+(Net.ping(val) ? "is" : "isn't") + " reachable.");
				}
				break;
			}
		}
	}
	
	public void setLineCount(int count){
		GUIText[] tempLines = new GUIText[count];
		
		if(consoleLines != null){
			if(count > lines){
				for(int i=0;i<count;i++){
					if(i < lines){
						tempLines[i] = consoleLines[i];
						consoleLines[i] = null;
					}else{
						tempLines[i] = new GUIText("");
						tempLines[i].setAnchor(AnchorX.LEFT, AnchorY.BOTTOM);
						tempLines[i].setPadding(new Vector2f(75f,-65f-(i*25)));
						tempLines[i].setZ(0.1f);
						Scene.getCurrent().add(tempLines[i]);
					}
				}
			}else if(count < lines){
				for(int i=0;i<count;i++){
					tempLines[i] = consoleLines[i];
					consoleLines[i] = null;
				}
			}
			Scene.getCurrent().remove(consoleLines);
		}else{
			for(int i=0;i<count;i++){
				tempLines[i] = new GUIText("");
				tempLines[i].setAnchor(AnchorX.LEFT, AnchorY.BOTTOM);
				tempLines[i].setPadding(new Vector2f(75f,-65f-(i*25)));
				tempLines[i].setZ(0.1f);
				Scene.getCurrent().add(tempLines[i]);
			}
		}
		
		consoleLines = tempLines;
		lines = count;
	}
	
	private void addConsoleLine(String line){
		for(int i=consoleLines.length-1;i>0;i--){
			consoleLines[i].setText(consoleLines[i-1].getText());
		}
		consoleLines[0].setText(line);
	}
	
	private void clearConsole(){
		for(int i=0;i<consoleLines.length;i++)
			consoleLines[i].setText("");
	}
	
	public void input(float delta){
		if(Keyboard.isClicked(Keyboard.KEY_ENTER) && allowConsole && cl.length() > 0){
			addConsoleLine(cl.toString());
			handle(cl.toString());
			cl.setLength(0);
			consoleLine.setText("");
		}
		
		if(Keyboard.isClicked(Keyboard.KEY_ENTER)){
			allowConsole = !allowConsole;
			setVisibility(allowConsole);
		}
		
		if(allowConsole && Keyboard.isClicked(Keyboard.KEY_BACKSPACE) && cl.length() > 0){
			cl.setLength(cl.length()-1);
			consoleLine.setText(cl.toString());
			backspaceLife = backspaceEnterRepeat;
			backspaceRepeat = true;
			canBackspace = false;
		}
		
		if(Keyboard.isPressed(Keyboard.KEY_BACKSPACE))
			backspace = true;
		else
			backspace = false;
		
		if(allowConsole && canBackspace && backspace && backspaceRepeat && cl.length() > 0){
			cl.setLength(cl.length()-1);
			consoleLine.setText(cl.toString());
			canBackspace = false;
			backspaceRepeat = true;
			backspaceLife = backspaceRate;
		}
		
		if(Keyboard.hasNext() && allowConsole){
			for(Character c : Keyboard.getNext()){
				if(Keyboard.isClicked(c))
					if(Keyboard.isPressed(Keyboard.KEY_LEFT_SHIFT) || Keyboard.isPressed(Keyboard.KEY_RIGHT_SHIFT))
						cl.append(Keyboard.getAlt(c));
					else
						cl.append(c);
			}
			consoleLine.setText(cl.toString());
		}
	}
	
	public void update(float delta){
		if(consoleTimer > 0)
			consoleTimer -= delta;
		if(consoleTimer <= 0 && !allowConsole){
			for(int i=0;i<consoleLines.length;i++)
				consoleLines[i].setVisible(false);
		}
		
		if(backspaceLife > 0)
			backspaceLife -= delta;
		if(backspaceLife <= 0){
			canBackspace = true;
			if(backspaceRepeat && !backspace)
				backspaceRepeat = false;
		}
	}
	
	private void setVisibility(boolean visibility){
		if(visibility){
			for(int i=0;i<consoleLines.length;i++)
				consoleLines[i].setVisible(true);
		}
		consoleLine.setVisible(visibility);
		consoleLabel.setVisible(visibility);
		if(!visibility)
			consoleTimer = consoleLifeTime;
	}
	
	public boolean isOpen(){
		return allowConsole;
	}
}
