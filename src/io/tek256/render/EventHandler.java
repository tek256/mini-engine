package io.tek256.render;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import io.tek256.Util;

public class EventHandler {
	private Action[] actions;
	
	public EventHandler(){
		
	}
	
	public void handle(Integer event){
		for(Action action: actions){
			if(action.triggersFrom(event)){
				action.call();
			}
		}
	}
	
	public Action getAction(int index){
		return actions[index];
	}
	
	public Action[] getActions(){
		return actions;
	}
	
	public void setActions(Action[] actions){
		this.actions = actions;
	}
	
	public void setActions(List<Action> actions){
		this.actions = new Action[actions.size()];
		for(int i=0;i<this.actions.length;i++){
			this.actions[i] = actions.get(i);
		}
	}
	
	public void addAction(Action action){
		if(actions == null){
			actions = new Action[]{action};
			return;
		}
		Action[] tmp = Arrays.copyOf(actions, actions.length);
		actions = new Action[tmp.length+1];
		for(int i=0;i<tmp.length;i++){
			actions[i] = tmp[i];
		}
		actions[actions.length-1] = action;
	}
	
	public void removeAction(Action action){
		Action[] tmp = Arrays.copyOf(actions, actions.length);
		actions = new Action[actions.length-1];
		boolean a = false;
		for(int i=0;i<tmp.length;i++){
			if(tmp[i] == action){
				a = true;
			}else{
				if(a)
					actions[i] = tmp[i];
				else
					actions[i-1] = tmp[i];
			}
		}
	}
	
	public Action createAction(String trigger, Object target, String method, Object[] params){
		Method usable = null;
		for(Method m : target.getClass().getMethods()){
			if(m.getName().equals(method)){
				if(Action.paramsMatch(m.getParameterTypes(),params))
					usable = m;
			}
		}
		return new Action(trigger, target, usable, params);
	}
	
	public Action createAction(String trigger, Object target, String method){
		Method usable = null;
		for(Method m : target.getClass().getMethods())
			if(m.getName().equals(method))
				if(m.getParameterTypes().length == 0)
					usable = m;
		return new Action(trigger, target, usable);	
	}
	
	public void removeAction(int index){
		Action[] tmp = Arrays.copyOf(actions, actions.length);
		actions = new Action[actions.length-1];
		boolean a = false;
		for(int i=0;i<tmp.length;i++){
			if(i == index){
				a = true;
			}else{
				if(a)
					actions[i] = tmp[i];
				else
					actions[i-1] = tmp[i];
			}
		}
	}
	
	public void removeActions(List<Action> actions){
		for(Action a : actions)
			removeAction(a);
	}
	
	public void addActions(List<Action> actions){
		for(Action a : actions)
			addAction(a);
	}
	
	public static class Action{
		final String[] triggers;
		final Method method;
		final Object target;
		final Object[] params;
		
		public Action(){
			triggers = null;
			method = null;
			target = null;
			params = null;
		}
		
		public Action(String trigger, Object target, Method method, Object[] params){
			triggers = new String[]{trigger};
			this.target = target;
			this.method = method;
			this.params = params;
		}
		
		public Action(String trigger, Object target, Method method){
			this.triggers = new String[]{trigger};
			this.target = target;
			this.method = method;
			params = new Object[0];
		}
		
		public void call(int val){
			if(triggersFrom(val))
				call();
		}
		
		public void call(String val){
			if(triggersFrom(val))
				call();
		}
		
		public void call(){
			try{
				method.invoke(target, params);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public boolean triggersFrom(int val){
			for(String intString : triggers){
				if(Util.getInt(intString) == val)
					return true;
			}
			return false;
		}
		
		public boolean triggersFrom(String val){
			for(String str : triggers){
				if(str.equals(val))
					return true;
			}
			return false;
		}
		
		public static <T> boolean paramsMatch(T[] args, Object[] params){
			if(args.length != params.length) return false;
			for(int i=0;i<args.length;i++)
				if(!args[i].getClass().getName().equals(params[i].getClass().getName()))
					return false;
			return true;
		}
	}
}
