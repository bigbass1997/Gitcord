package com.bigbass.gitcordbot.discord;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.google.gson.Gson;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IMessage.Attachment;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class DiscordUtil {
	
	private DiscordUtil(){}
	
	private static Gson gson = null;
	
	public static Gson getGson(){
		if(gson == null){
			gson = new Gson();
		}
		
		return gson;
	}
	
	public static EmbedBuilder getBuilder(){
		return new EmbedBuilder();
	}
	
	public static String formatMessage(IMessage msg){
		String timestamp = formatTimestampUTC(msg.getTimestamp());
		String author = msg.getAuthor().getName();
		String channelName = msg.getChannel().getName();
		
		if(!msg.getChannel().isPrivate()){
			return String.format("[%s][%s][#%s]%s: %s", timestamp, msg.getGuild().getName(), channelName, author, msg.getFormattedContent());
		} else {
			return String.format("[%s][Private]%s: %s", timestamp, author, msg.getFormattedContent());
		}
	}
	
	public static String formatTimestampUTC(LocalDateTime timestamp){
		return timestamp.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"));
	}
	
	public static String replaceMentions(String content, List<IUser> mentions){
		for(IUser mention : mentions){
			content = content.replace("<@" + mention.getLongID() + ">", "@" + mention.getName());
		}
		return content;
	}
	
	public static String parseMessageForAuthor(IMessage msg){
		try {
			if(msg.getAuthor().isBot()){
				String content = msg.getContent();
				int seperatorIndex = 0;
				int stripOffset = 0;
				
				if(content.contains("\u00BB")){
					seperatorIndex = content.indexOf("\u00BB");
					stripOffset = 1;
				} else {
					seperatorIndex = content.indexOf(":");
					stripOffset = 0;
				}

				if(content.startsWith("**") || content.startsWith("```") || content.startsWith(":")){
					return msg.getAuthor().getName();
				}
				
				if(content.startsWith("[")){
					return content.substring(content.indexOf("]") + 1, seperatorIndex - stripOffset);
				}
				
				return content.substring(0, seperatorIndex - stripOffset);
				
			} else {
				return msg.getAuthor().getName();
			}
		} catch(Exception e) {
			e.printStackTrace();
			return msg.getAuthor().getName();
		}
	}
	
	public static Hashtable<String, Object> getMessageAuthor(IMessage msg){
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		
		IUser author = msg.getAuthor();
		boolean isBot = author.isBot();
		
		data.put("name", author.getName());
		data.put("displayName", author.getDisplayName(msg.getGuild()));
		data.put("id", author.getStringID());
		data.put("isBot", isBot);
		if(isBot){
			data.put("parsedName", parseMessageForAuthor(msg));
		}
		
		return data;
	}
	
	public static Hashtable<String, Object> getMessageGuild(IMessage msg){
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		if(!msg.getChannel().isPrivate()){
			IGuild guild = msg.getGuild();
			
			data.put("name", guild.getName());
			data.put("id", guild.getStringID());
		} else {
			data.put("name", "Private");
		}
		
		return data;
	}
	
	public static Hashtable<String, Object> getMessageText(IMessage msg){
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		
		data.put("raw", msg.getContent());
		data.put("formatted", msg.getFormattedContent());
		data.put("id", msg.getStringID());
		
		List<Attachment> attachments = msg.getAttachments();
		if(attachments != null && !attachments.isEmpty()){
			ArrayList<String> urls = new ArrayList<String>();
			for(Attachment attachment : attachments){
				urls.add(attachment.getUrl());
			}
			
			if(!urls.isEmpty()){
				data.put("attachmentUrls", urls);
			}
		}
		
		return data;
	}
}
