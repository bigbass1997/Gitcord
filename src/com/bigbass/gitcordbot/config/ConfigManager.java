package com.bigbass.gitcordbot.config;

import java.util.Hashtable;

import javax.json.Json;
import javax.json.JsonObject;

public class ConfigManager {
	
	public static final String CONFIG_DIR = "./bot-config/";
	
	private static ConfigManager instance;
	
	private Hashtable<String, Config> configs;
	
	private ConfigManager(){
		configs = new Hashtable<String, Config>();
		
		// Register core configs
		registerConfig("mongo", "mongo.json");
		registerConfig("discord", "discord.json");
		registerConfig("github", "github.json");
		
		// Check if core configs exist, if not, create them with default settings
		Config mongo = getConfig("mongo");
		if(mongo.retrieveData() == null){
			JsonObject data = Json.createObjectBuilder()
					.add("hostname", "hostname")
					.add("port", 27017)
					.add("username", "username")
					.add("password", "password")
					.add("database", "database_name")
					.add("authDatabase", "authentication_database_name")
					.build();
			mongo.data = data;
			mongo.saveData();
		}
		
		Config discord = getConfig("discord");
		if(discord.retrieveData() == null){
			JsonObject data = Json.createObjectBuilder()
					.add("token", "")
					.add("clientid", "")
					.add("guildid", "")
					.build();
			discord.data = data;
			discord.saveData();
		}
		
		Config github = getConfig("github");
		if(github.retrieveData() == null){
			JsonObject data = Json.createObjectBuilder()
					.add("useragent", "useragent")
					.add("token", "token")
					.build();
			github.data = data;
			github.saveData();
		}
	}
	
	public boolean registerConfig(String id, String filename){
		if(id == null || filename == null || configs.containsKey(id)){ // Prevents accidentally rewriting already loaded configs
			return false;
		}
		
		configs.put(id, new Config(filename));
		return true;
	}
	
	public Config getConfig(String id){
		if(id == null){
			return null;
		}
		
		return configs.get(id);
	}
	
	public void saveAll(){
		for(Config config : configs.values()){
			config.saveData();
		}
	}
	
	public static ConfigManager getInstance(){
		if(instance != null){
			return instance;
		}
		
		return instance = new ConfigManager();
	}
}
