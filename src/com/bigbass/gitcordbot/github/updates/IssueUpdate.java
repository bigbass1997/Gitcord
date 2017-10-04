package com.bigbass.gitcordbot.github.updates;

import java.io.IOException;
import java.util.Date;

import javax.json.JsonObject;

import com.bigbass.gitcordbot.discord.DiscordController;
import com.jcabi.github.Comment;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;

public class IssueUpdate implements GithubUpdate {

	private final Issue issue;
	
	private Github.Time lastCheck;
	
	public IssueUpdate(Repo repo, int number) throws IOException{
		this(repo.issues().get(number));
	}
	
	public IssueUpdate(Issue issue) throws IOException{
		if(issue == null || !issue.exists()){
			throw new NullPointerException("Issue does not exist!");
		}
		
		this.issue = issue;
		
		lastCheck = new Github.Time(new Date());
	}

	@Override
	public void check(Github github) {
		System.out.println("Checking Issue #" + issue.number());
		for(Comment comment : issue.comments().iterate(lastCheck.date())){
			try {
				JsonObject json = comment.json();
				
				if(json.getString("created_at").equals(json.getString("updated_at"))){
					sendNewCommentMessage(comment);
				} else {
					sendUpdatedCommentMessage(comment);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		lastCheck = new Github.Time(new Date());
	}
	
	private void sendNewCommentMessage(Comment comment){
		try {
			DiscordController.getInstance().client.getChannelByID(365167595630100480L).sendMessage("New Comment from Issue #" + issue.number() + ", " + comment.number() + ", " + comment.json().getString("body"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendUpdatedCommentMessage(Comment comment){
		try {
			DiscordController.getInstance().client.getChannelByID(365167595630100480L).sendMessage("Comment Updated from Issue #" + issue.number() + ", " + comment.number() + ", " + comment.json().getString("body"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
