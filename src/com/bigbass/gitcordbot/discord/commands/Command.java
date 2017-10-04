package com.bigbass.gitcordbot.discord.commands;

import java.util.Random;

import com.bigbass.gitcordbot.internalperms.InternalPermsManager;
import com.bigbass.gitcordbot.internalperms.InternalPermsManager.Perm;

import sx.blah.discord.handle.obj.IMessage;

public abstract class Command {
	
	private String type;
	
	protected Random rand;
	
	public Command(String type){
		this.type = type;
		
		rand = new Random();
	}
	
	public boolean hasPermission(long id, Perm perm){
		return InternalPermsManager.getInstance().hasPermission(id, perm);
	}
	
	public abstract void performAction(IMessage msg);
	
	public String getType(){
		return type;
	}
}
