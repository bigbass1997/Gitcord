package com.bigbass.gitcordbot.internalperms;

import java.util.ArrayList;

public class InternalPermsManager {
	
	private static InternalPermsManager instance;
	
	public enum Perm {
		MANAGE_PERMS, TEST_COMMAND;
		
		private ArrayList<Long> users;
		
		private Perm(){
			users = new ArrayList<Long>();
		}
		
		public boolean hasUserID(long id){
			if(users.contains(id)){
				return true;
			} else {
				return false;
			}
		}
		
		public void addUser(long id){
			if(!users.contains(id)){
				users.add(id);
				System.out.println("User " + id + " is added to " + this);
			}
		}
		
		public void removeUser(long id){
			users.remove(id);
			System.out.println("User " + id + " is removed from " + this);
		}
	}
	
	private InternalPermsManager(){
		// Adds bigbass1997 to all Perms
		for(Perm perm : Perm.values()){
			perm.addUser(119460542699143172L);
		}
	}
	
	public static InternalPermsManager getInstance(){
		if(instance == null){
			instance = new InternalPermsManager();
		}
		
		return instance;
	}
	
	public boolean hasPermission(long id, Perm perm){
		return perm.hasUserID(id);
	}
}
