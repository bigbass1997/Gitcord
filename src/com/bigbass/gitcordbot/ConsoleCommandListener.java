package com.bigbass.gitcordbot;

import java.util.Scanner;

import com.bigbass.gitcordbot.config.ConfigManager;
import com.bigbass.gitcordbot.discord.DiscordController;
import com.bigbass.gitcordbot.github.GithubController;

import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class ConsoleCommandListener extends Thread {
	
	private static ConsoleCommandListener instance;

	private boolean running;
	
	private Scanner scan;
	
	private ConsoleCommandListener(){
		running = true;
		
		scan = new Scanner(System.in);
		
		setName("ConsoleCommandListener");
		
		start();
	}
	
	public static ConsoleCommandListener getInstance(){
		if(instance == null){
			instance = new ConsoleCommandListener();
		}
		
		return instance;
	}

	@Override
	public void run(){
		System.out.println("ConsoleCommandListener Thread started!");
		
		while(running){
			String responce = scan.nextLine();
			
			if(responce.equalsIgnoreCase("stop") || responce.equalsIgnoreCase("exit")){
				break;
			}
			
			if(responce.equalsIgnoreCase("login")){
				try {
					DiscordController.getInstance().client.login(); //TODO doesn't really fix anything
				} catch (RateLimitException e) {
					e.printStackTrace();
				} catch (DiscordException e) {
					e.printStackTrace();
				}
			}
			
			if(responce.equalsIgnoreCase("saveall")){
				ConfigManager.getInstance().saveAll();
			}
			
			if(responce.startsWith("set status")){
				DiscordController.getInstance().client.changePlayingText(responce.substring("set status".length()));
			}
			
			if(responce.equalsIgnoreCase("doagain")){
				GithubController.getInstance().doAgain();
			}
		}
		
		scan.close();
		System.out.println("ConsoleCommandListener Thread ended!");
	}
}
