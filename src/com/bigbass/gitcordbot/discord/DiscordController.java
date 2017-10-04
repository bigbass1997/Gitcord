package com.bigbass.gitcordbot.discord;

import com.bigbass.gitcordbot.config.ConfigManager;
import com.bigbass.gitcordbot.discord.commands.AddPermCommand;
import com.bigbass.gitcordbot.discord.commands.CommandHandler;
import com.bigbass.gitcordbot.discord.commands.RegisterUpdateCommand;
import com.bigbass.gitcordbot.discord.commands.RemovePermCommand;
import com.bigbass.gitcordbot.discord.commands.TestCommand;
import com.bigbass.gitcordbot.discord.listeners.ReadyListener;
import com.bigbass.gitcordbot.discord.listeners.message.MessageReceivedListener;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class DiscordController {
	
	private static DiscordController instance;
	
	public IDiscordClient client;
	
	private DiscordController(){
		ClientBuilder builder = new ClientBuilder();
		builder.withToken(ConfigManager.getInstance().getConfig("discord").data.getString("token"));
		
		try {
			client = builder.build();
			
			client.getDispatcher().registerListener(new ReadyListener());
			client.getDispatcher().registerListener(new MessageReceivedListener());
			
			client.login();
		} catch (DiscordException | RateLimitException e) {
			e.printStackTrace();
		}
		
		CommandHandler ch = CommandHandler.getInstance();
		ch.registerCommand(new TestCommand());
		ch.registerCommand(new AddPermCommand());
		ch.registerCommand(new RemovePermCommand());
		ch.registerCommand(new RegisterUpdateCommand());
	}
	
	public static DiscordController getInstance(){
		if(instance == null){
			instance = new DiscordController();
		}
		
		return instance;
	}
	
	public void close(){
		try {
			client.logout();
			System.out.println("Discord Client connection closed!");
		} catch (DiscordException e) {
			e.printStackTrace();
			client = null;
		}
	}
}
