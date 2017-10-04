package com.bigbass.gitcordbot.discord.listeners;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class ReadyListener implements IListener<ReadyEvent> {

	@Override
	public void handle(ReadyEvent event) {
		event.getClient().changePlayingText("I'm Listening");
		/*try {
			event.getClient().changeAvatar(Image.forFile(new File(this.getClass().getResource("/images/lemongrab.png").toURI())));
		} catch (DiscordException | RateLimitException | URISyntaxException e) {
			e.printStackTrace();
		}*/
		System.out.println("Bot is ready!");
	}
}
