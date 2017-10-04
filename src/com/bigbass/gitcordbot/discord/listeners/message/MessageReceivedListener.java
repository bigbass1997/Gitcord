package com.bigbass.gitcordbot.discord.listeners.message;

import com.bigbass.gitcordbot.discord.commands.CommandHandler;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class MessageReceivedListener implements IListener<MessageReceivedEvent> {
	
	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage msg = event.getMessage();
		if(msg.getChannel().getName().contains("console")){
			return;
		}
		
		CommandHandler.getInstance().processCommand(msg);
	}
}
