package com.bigbass.gitcordbot;

import com.bigbass.gitcordbot.config.ConfigManager;
import com.bigbass.gitcordbot.discord.DiscordController;
import com.bigbass.gitcordbot.github.GithubController;
import com.bigbass.gitcordbot.internalperms.InternalPermsManager;
import com.bigbass.gitcordbot.mongo.MongoController;

public class ProgramController extends Thread {

	private ConsoleCommandListener console;
	private MongoController mongoCon;
	private DiscordController discordCon;
	private GithubController githubCon;

	public ProgramController(){
		ConfigManager.getInstance();
		console = ConsoleCommandListener.getInstance();
		mongoCon = MongoController.getInstance();
		discordCon = DiscordController.getInstance();
		githubCon = GithubController.getInstance();
		InternalPermsManager.getInstance();
		
		start();
	}
	
	@Override
	public void run(){
		while(console.isAlive()){
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//wait for console to close itself, then end program, pausing every loop for 5 seconds
		}
		
		stopProgram();
		
		System.gc();
		System.out.println("Program should terminate now. If not, kill the process.");
	}
	
	private void stopProgram(){
		System.out.println("Shutting down controllers...");
		mongoCon.close();
		discordCon.close();
		githubCon.close();
		
		System.out.println("Saving all configs...");
		ConfigManager.getInstance().saveAll();
		
		System.out.println("Program should self-terminate shortly.");
		
		while(console.isAlive() || mongoCon.isAlive() || mongoCon.isConnected() || discordCon.client.isReady() || githubCon.isAlive()){
			//System.out.println(console.isAlive() + " | " + mongoCon.isAlive() + " | " + mongoCon.isConnected() + " | " + discordCon.client.isReady());
		}
	}
}
