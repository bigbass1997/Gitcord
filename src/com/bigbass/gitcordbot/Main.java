package com.bigbass.gitcordbot;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;

public class Main {
	
	public static void main(String[] args) {
		StatusLogger.getLogger().setLevel(Level.FATAL);
		
		new ProgramController();
	}
}
