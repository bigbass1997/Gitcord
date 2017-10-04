package com.bigbass.gitcordbot.github;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.bigbass.gitcordbot.config.ConfigManager;
import com.jcabi.github.Comment;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Event;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.IssueEvents;
import com.jcabi.github.Issues;
import com.jcabi.github.RtGithub;
import com.jcabi.http.request.ApacheRequest;

public class GithubController extends Thread {
	
	private static GithubController instance;

	private boolean running;
	
	public final String USER_AGENT;
	public final String TOKEN;
	
	public Github github;
	
	public static enum Status {
		
	}
	
	private GithubController(){
		USER_AGENT = ConfigManager.getInstance().getConfig("github").data.getString("useragent");
		TOKEN = ConfigManager.getInstance().getConfig("github").data.getString("token");
		
		github = new RtGithub(new ApacheRequest("https://api.github.com")
				.header(HttpHeaders.USER_AGENT, USER_AGENT)
				.header(HttpHeaders.AUTHORIZATION, String.format("token %s", TOKEN))
	            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
	            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
		
		
		running = true;
		
		start();
	}
	
	public static GithubController getInstance(){
		if(instance == null){
			instance = new GithubController();
		}
		
		return instance;
	}
	
	public String sinceLast = "";
	
	public void doAgain(){
		Issues issues = github.repos().get(new Coordinates.Simple("bigbass1997", "Gitcord")).issues();
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "all");
		map.put("since", "2017-10-04T05:15:37Z");
		
		System.out.println("\n=== Next Response ===");
		for(Issue issue : issues.iterate(map)){
			try {
				for(Comment comment : issue.comments().iterate(new Github.Time("2017-10-04T05:15:37Z").date())){
					System.out.println(comment.json());
				}
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run(){
		Issues issues = github.repos().get(new Coordinates.Simple("bigbass1997", "Gitcord")).issues();
		Map<String, String> map = new HashMap<String, String>();
		map.put("state", "all");
		map.put("since", "2017-10-04T04:35:37Z");
		
		System.out.println("=== Initial Response ===");
		for(Issue issue : issues.iterate(map)){
			try {
				for(Comment comment : issue.comments().iterate(new Github.Time("2017-10-04T04:35:37Z").date())){
					System.out.println(comment.json());
				}
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		/*EventsMonitor test = new EventsMonitor("repos/GTNewHorizons/NewHorizons/issues/events");
		
		System.out.println("=== Initial Response ===");
		System.out.println(test.getResponse().headers());
		
		test.serveAPI();

		System.out.println("\n=== Second Response ===");
		System.out.println(test.getResponse().headers());*/
		
		
		
		while(running){
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("GithubController Thread ended!");
	}
	
	public void close(){
		running = false;
	}
	
	
}
