package com.bigbass.gitcordbot.github.updates;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;

import javax.json.JsonObject;

import com.bigbass.gitcordbot.discord.DiscordController;
import com.bigbass.gitcordbot.discord.DiscordUtil;
import com.jcabi.github.Comment;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

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
			JsonObject json = comment.json();
			
			EmbedObject embed = formatMessage(
					json.getString("body"),
					"Comment on Issue #" + issue.number(),
					comment.issue().repo().coordinates().toString(),
					json.getString("html_url"),
					json.getJsonObject("user").getString("login"),
					json.getJsonObject("user").getString("html_url"),
					json.getJsonObject("user").getString("avatar_url"),
					new Color(68, 204, 204)
				);
			
			DiscordController.getInstance().client.getChannelByID(364497546158276613L).sendMessage(embed);
			//DiscordController.getInstance().client.getChannelByID(365167595630100480L).sendMessage("New Comment from Issue #" + issue.number() + ", " + comment.number() + ", " + comment.json().getString("body"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendUpdatedCommentMessage(Comment comment){
		try {
			JsonObject json = comment.json();
			
			EmbedObject embed = formatMessage(
					json.getString("body"),
					"Comment Edited on Issue #" + issue.number(),
					comment.issue().repo().coordinates().toString(),
					json.getString("html_url"),
					json.getJsonObject("user").getString("login"),
					json.getJsonObject("user").getString("html_url"),
					json.getJsonObject("user").getString("avatar_url"),
					new Color(68, 180, 180)
				);
			
			DiscordController.getInstance().client.getChannelByID(364497546158276613L).sendMessage(embed);
			
			//DiscordController.getInstance().client.getChannelByID(364497546158276613L).sendMessage("Comment Updated from Issue #" + issue.number() + ", " + comment.number() + ", " + comment.json().getString("body"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private EmbedObject formatMessage(String body, String title, String repo, String commentURL, String authorLogin, String authorURL, String avatarURL, Color col){
		EmbedBuilder b = DiscordUtil.getBuilder();
		b.clearFields().setLenient(true).withColor(col);
		
		b.withTitle(title + " - " + repo);
		b.withUrl(commentURL);
		b.withAuthorName(authorLogin);
		b.withAuthorUrl(authorURL);
		b.withAuthorIcon(avatarURL);
		
		b.appendField("Content", body, false);
		
		return b.build();
	}
}
