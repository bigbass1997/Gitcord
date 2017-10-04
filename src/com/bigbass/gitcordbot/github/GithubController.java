package com.bigbass.gitcordbot.github;

import java.util.ArrayList;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.bigbass.gitcordbot.config.ConfigManager;
import com.bigbass.gitcordbot.github.updates.GithubUpdate;
import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.http.request.ApacheRequest;

public class GithubController extends Thread {
	
	private static GithubController instance;

	private boolean running;
	
	private ArrayList<GithubUpdate> updates;
	
	public final String USER_AGENT;
	public final String TOKEN;
	
	public Github github;
	
	private GithubController(){
		updates = new ArrayList<GithubUpdate>();
		
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
	
	@Override
	public void run(){/*
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
		}*/
		
		float startTime = System.currentTimeMillis();
		while(running){
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(System.currentTimeMillis() - startTime > 15000){
				checkUpdates();
				
				startTime = System.currentTimeMillis();
			}
		}
		
		System.out.println("GithubController Thread ended!");
	}
	
	public void addUpdate(GithubUpdate update){
		if(update == null){
			return;
		}
		
		updates.add(update);
	}
	
	private void checkUpdates(){
		for(GithubUpdate update : updates){
			update.check(github);
		}
	}
	
	public void close(){
		running = false;
	}
	
	
}
