package com.bigbass.gitcordbot.mongo;

import javax.json.JsonObject;

import com.bigbass.gitcordbot.config.ConfigManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoIterable;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;

public class MongoController extends Thread implements ServerMonitorListener {

	private static MongoController instance;
	
	private MongoClient client;
	
	private boolean running;
	private boolean connected;
	
	private MongoController(){
		running = true;
		connected = false;
		
		setName("MongoController");
		//start();
	}
	
	public static MongoController getInstance(){
		if(instance == null){
			instance = new MongoController();
		}
		
		return instance;
	}
	
	@Override
	public void run(){
		System.out.println("MongoController Thread started!");
		JsonObject options = ConfigManager.getInstance().getConfig("mongo").data;
		
		try {
			MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder().addServerMonitorListener(this);
			
			MongoClientURI uri = new MongoClientURI("mongodb://" + options.getString("username") + ":" + options.getString("password") + "@" + options.getString("hostname") + "/?authSource=" + options.getString("authDatabase"), optionsBuilder);
			
			client = new MongoClient(uri);
		} catch(Exception e) {
			e.printStackTrace();
			running = false;
			System.out.println("WARNING! MongoClient failed to initialize. Program will not make any automated attempt to fix this!");
			return;
		}

		//MongoDatabase database = client.getDatabase(options.getString("database"));
		
		while(running){
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// do stuff
		}
		
		client.close();
		connected = false;
		System.out.println("MongoController Thread ended! MongoDB Client connection closed!");
	}
	
	@Override
	public void serverHearbeatStarted(ServerHeartbeatStartedEvent e) {
	}
	
	@Override
	public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent e) {
		connected = true;
	}
	
	@Override
	public void serverHeartbeatFailed(ServerHeartbeatFailedEvent e) {
		connected = false;
	}

	public boolean isConnected(){
		return connected;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public void close(){
		this.running = false;
	}
	
	public void closeClient(){
		client.close();
	}
	
	public MongoClient getCli(){
		return client;
	}
	
	public String expandStringIterator(MongoIterable<String> iterator){
		String s = "";
		for(String str : iterator){
			s += str + " ";
		}
		return s;
	}
}
