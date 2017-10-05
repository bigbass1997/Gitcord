package com.bigbass.gitcordbot.github.updates;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jcabi.github.Event;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Issues;
import com.jcabi.github.Repo;

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
				if(lastCheck.date().before(createdAt)){
					System.out.println("Issue #" + issue.number() + " has been created.");
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
}
