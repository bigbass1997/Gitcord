package com.bigbass.gitcordbot.discord.commands.github;

import java.io.IOException;

import com.bigbass.gitcordbot.discord.DiscordUtil;
import com.bigbass.gitcordbot.discord.commands.Command;
import com.bigbass.gitcordbot.github.GithubController;
import com.jcabi.github.Branch;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Fork;
import com.jcabi.github.Repo;
import com.jcabi.github.User;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class InfoCommand extends Command {

	public InfoCommand() {
		super("info");
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
		
		if(parts[1].equals("repo")){
			if(parts.length == 3 && parts[2].contains("/")){
				String[] coords = parts[2].split("/");
				if(coords.length == 2){
					printRepoInfo(msg, GithubController.getInstance().github.repos().get(new Coordinates.Simple(coords[0], coords[1])));
				}
			} else if(parts.length == 4){
				printRepoInfo(msg, GithubController.getInstance().github.repos().get(new Coordinates.Simple(parts[2], parts[3])));
			}
		}
	}
	
	private void printRepoInfo(IMessage msg, Repo repo){
		msg.getChannel().sendMessage(formatRepoMessage(repo));
	}
	
	private EmbedObject formatRepoMessage(Repo r){
		Repo.Smart repo = new Repo.Smart(r);
		
		EmbedBuilder b = DiscordUtil.getBuilder();
		b.clearFields().setLenient(true).withColor(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
		
		Coordinates coord = repo.coordinates();
		b.withTitle("Repo Info - " + coord.user() + "/" + coord.repo());
		b.withUrl("https://github.com/" + coord.user() + "/" + coord.repo());
		
		String nl = "\n";
		
		// Collaborators
		String collaborators = "";
		for(User user : repo.collaborators().iterate()){
			try {
				collaborators += user.login() + nl;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!collaborators.isEmpty()){
			b.appendField("Collaborators", collaborators.trim(), true);
		}
		
		// Branches
		String branches = "";
		for(Branch branch : repo.branches().iterate()){
			branches += branch.name() + nl;
		}
		if(!branches.isEmpty()){
			b.appendField("Branches", branches.trim(), true);
		}
		
		// Contributors
		// https://api.github.com/repos/GTNewHorizons/NewHorizons/contributors
		String contributors = "WIP";
		
		if(!contributors.isEmpty()){
			b.appendField("Contributors", contributors.trim(), true);
		}
		
		// Forks
		/*String forks = "";
		for(Fork f : repo.forks().iterate("newest")){
			Fork.Smart fork = new Fork.Smart(f);
			try {
				forks += fork.fullName() + nl;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AssertionError e){
				e.printStackTrace();
			}
		}
		if(!forks.isEmpty()){
			b.appendField("Forks", forks.trim(), true);
		}*/
		try {
			b.appendField("json", repo.json().toString(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return b.build();
	}
}
