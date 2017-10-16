package com.bigbass.gitcordbot.github.updates;

import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import com.bigbass.gitcordbot.discord.DiscordController;
import com.bigbass.gitcordbot.discord.DiscordUtil;
import com.bigbass.gitcordbot.github.GithubController;
import com.jcabi.github.Event;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Issues;
import com.jcabi.github.Repo;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class IssuesUpdate implements GithubUpdate {
	
	private final Repo repo;

	private Github.Time lastCheck;
	
	public IssuesUpdate(Repo repo){
		this.repo = repo;
		
		lastCheck = new Github.Time(new Date());
	}

	@Override
	public void check(Github github) {
		System.out.println("Checking Issues for " + repo.coordinates());
		
		Issues issues = repo.issues();
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "all");
		map.put("since", lastCheck.toString());
		for(Issue issue : issues.iterate(map)){
			try {
				String jsonDate = issue.json().getString("created_at");
				Date createdAt = new Github.Time(jsonDate).date();
				System.out.println("Issue #" + issue.number() + " createdAt: " + createdAt.getTime() + " | lastCheck: " + lastCheck.date().getTime());
				if(lastCheck.date().before(createdAt)){
					if(issue.events().iterator().hasNext()){
						continue;
					}
					
					JsonObject json = issue.json();
					
					EmbedObject embed = formatMessage(
							json.getString("body"),
							json.getString("title"),
							"Issue #" + issue.number() + " Created",
							issue.repo().coordinates().toString(),
							json.getString("html_url"),
							json.getJsonObject("user").getString("login"),
							json.getJsonObject("user").getString("html_url"),
							json.getJsonObject("user").getString("avatar_url"),
							new Color(68, 204, 204)
						);
					
					DiscordController.getInstance().client.getChannelByID(364497546158276613L).sendMessage(embed);
					
					GithubController.getInstance().addUpdate(new IssueUpdate(repo, issue.number()));
				}
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
			
			try {
				for(Event event : issue.events()){
					Event.Smart smart = new Event.Smart(event);
					System.out.println(smart.type() + " for issue #" + issue.number());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		lastCheck = new Github.Time(new Date());
	}
	
	private EmbedObject formatMessage(String body, String issueTitle, String title, String repo, String commentURL, String authorLogin, String authorURL, String avatarURL, Color col){
		EmbedBuilder b = DiscordUtil.getBuilder();
		b.clearFields().setLenient(true).withColor(col);
		
		b.withTitle(title + " - " + repo);
		b.withUrl(commentURL);
		b.withAuthorName(authorLogin);
		b.withAuthorUrl(authorURL);
		b.withAuthorIcon(avatarURL);
		
		b.appendField(issueTitle, body, false);
		
		return b.build();
	}
}
