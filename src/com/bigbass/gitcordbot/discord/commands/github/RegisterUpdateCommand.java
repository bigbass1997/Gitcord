package com.bigbass.gitcordbot.discord.commands.github;

import java.io.IOException;

import com.bigbass.gitcordbot.discord.commands.Command;
import com.bigbass.gitcordbot.github.GithubController;
import com.bigbass.gitcordbot.github.updates.IssueUpdate;
import com.bigbass.gitcordbot.github.updates.IssuesUpdate;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;

import sx.blah.discord.handle.obj.IMessage;

public class RegisterUpdateCommand extends Command {

	public RegisterUpdateCommand() {
		super("register");
	}
	
	@Override
	public void performAction(IMessage msg) {
		String text = msg.getFormattedContent().substring(1).trim();
		String[] parts;
		
		if(!text.contains(" ")){
			parts = new String[]{text};
		} else {
			parts = text.split(" ");
		}
		
		if(parts.length == 5 && parts[1].equals("issue")){
			Repo repo = GithubController.getInstance().github.repos().get(new Coordinates.Simple(parts[2], parts[3]));
			
			if(repo != null){
				try {
					int issueNumber = Integer.parseInt(parts[4]);
					GithubController.getInstance().addUpdate(new IssueUpdate(repo, issueNumber));
					
					msg.getChannel().sendMessage("Update has been added!");
					return;
				} catch(NumberFormatException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(parts.length == 4 && parts[1].equals("issues")){
			Repo repo = GithubController.getInstance().github.repos().get(new Coordinates.Simple(parts[2], parts[3]));
			
			if(repo != null){
				try {
					GithubController.getInstance().addUpdate(new IssuesUpdate(repo));
					
					msg.getChannel().sendMessage("Update has been added!");
					return;
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		
		msg.getChannel().sendMessage("You entered an invalid value.");
	}
}
