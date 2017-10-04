package com.bigbass.gitcordbot.discord.commands;

import com.bigbass.gitcordbot.internalperms.InternalPermsManager.Perm;

import sx.blah.discord.handle.obj.IMessage;

public class RemovePermCommand extends Command {
	
	public RemovePermCommand() {
		super("removeperm");
	}
	
	@Override
	public void performAction(IMessage msg) {
		if(hasPermission(msg.getAuthor().getLongID(), Perm.MANAGE_PERMS)){
			String text = msg.getFormattedContent().substring(1).trim();
			String[] parts;
			
			if(!text.contains(" ")){
				parts = new String[]{text};
			} else {
				parts = text.split(" ");
			}
			
			if(parts.length == 3){
				Perm perm = Perm.valueOf(parts[2]);
				
				if(perm != null){ // check if valid perm
					try {
						perm.removeUser(Long.valueOf(parts[1]));
					} catch(NumberFormatException e){
						// invalid id
					}
				}
			}
		}
	}
}
