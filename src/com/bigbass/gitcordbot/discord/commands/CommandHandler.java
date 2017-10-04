package com.bigbass.gitcordbot.discord.commands;

import java.util.Hashtable;

import sx.blah.discord.handle.obj.IMessage;

public class CommandHandler {
	
	private static CommandHandler instance;
	
	private Hashtable<String, Command> commands;
	
	private CommandHandler(){
		commands = new Hashtable<String, Command>();
	}
	
	public void registerCommand(Command cla){
		commands.put(cla.getType(), cla);
	}
	
	public static CommandHandler getInstance(){
		if(instance == null){
			instance = new CommandHandler();
		}
		
		return instance;
	}
	
	public void processCommand(IMessage msg){
		if(!msg.getFormattedContent().startsWith("!") || msg.getFormattedContent().length() < 2){ // No chance of this being a command if it does not start with '!'.
			return;
		}
		
		if(/*msg.getChannel().getName().startsWith("chat_") || */msg.getChannel().getName().startsWith("console_")){
			return;
		}
		
		if(msg.getAuthor().isBot()){ // Don't let other bots boss you around.
			return;
		}
		
		///// Parse Command Type /////
		
		String text = msg.getFormattedContent().substring(1).trim();
		String[] parts;
		
		if(!text.contains(" ")){
			parts = new String[]{text};
		} else {
			parts = text.split(" ");
		}
		
		String type = parts[0];
		Command tempCommand = commands.get(type);
		
		if(tempCommand != null){
			tempCommand.performAction(msg);
		}
	}
}
