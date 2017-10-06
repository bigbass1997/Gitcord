package com.bigbass.gitcordbot.discord.commands.github;

import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import com.bigbass.gitcordbot.discord.DiscordUtil;
import com.bigbass.gitcordbot.discord.commands.Command;
import com.bigbass.gitcordbot.github.GithubController;
import com.jcabi.github.Branch;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.User;
import com.jcabi.http.response.JsonResponse;

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
		String com = ", ";
		
		// Collaborators
		String collaborators = "";
		for(User user : repo.collaborators().iterate()){
			try {
				collaborators += user.login() + com;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!collaborators.isEmpty()){
			b.appendField("Collaborators", collaborators.substring(0, collaborators.length() - 2), true);
		}
		
		// Contributors
		try {
			JsonArray contributorsJson = repo.github().entry().uri()
		            .path("/repos")
		            .path(repo.coordinates().user())
		            .path(repo.coordinates().repo())
					.path("/contributors")
					.back().fetch().as(JsonResponse.class).json().readArray();
			
			String contributors = "";
			for(int i = 0; i < contributorsJson.size(); i++){
				JsonObject contribJson = contributorsJson.getJsonObject(i);
				
				contributors += contribJson.getString("login") + com;
			}
			if(!contributors.isEmpty()){
				b.appendField("Contributors", contributors.substring(0, contributors.length() - 2), true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Branches
		String branches = "";
		for(Branch branch : repo.branches().iterate()){
			branches += branch.name() + nl;
		}
		if(!branches.isEmpty()){
			b.appendField("Branches", branches.trim(), true);
		}
		
		// Forks
		/*String forks = "";
		for(Fork f : repo.forks().iterate("newest")){
			try {
				forks += f.json().getString("full_name") + nl;
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
			JsonArray forksJson = repo.github().entry().uri()
		            .path("/repos")
		            .path(repo.coordinates().user())
		            .path(repo.coordinates().repo())
					.path("/forks")
					.back().fetch().as(JsonResponse.class).json().readArray();
			
			String forks = "";
			for(int i = 0; i < forksJson.size(); i++){
				JsonObject forkJson = forksJson.getJsonObject(i);
				
				forks += forkJson.getString("full_name") + nl;
			}
			if(!forks.isEmpty()){
				b.appendField("Forks", forks.trim(), true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return b.build();
	}
}
