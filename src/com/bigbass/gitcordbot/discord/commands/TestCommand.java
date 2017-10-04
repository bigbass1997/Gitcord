package com.bigbass.gitcordbot.discord.commands;

import com.bigbass.gitcordbot.discord.DiscordUtil;
import com.bigbass.gitcordbot.internalperms.InternalPermsManager.Perm;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public class TestCommand extends Command {
	
	public TestCommand() {
		super("test");
	}

	@Override
	public void performAction(IMessage msg) {
		if(hasPermission(msg.getAuthor().getLongID(), Perm.TEST_COMMAND)){
			msg.getChannel().sendMessage(formatMessage());
		} else {
			msg.reply("You do not have permission for this command!");
		}
	}
	
	private EmbedObject formatMessage(){
		EmbedBuilder b = DiscordUtil.getBuilder();
		b.clearFields().setLenient(true).withColor(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
		
		String temp = "Hello World!\n\nMy name is GitcordBot,\n    and I will be your host this evening.";
		
		b.appendField("Test Command", temp, false);
		
		return b.build();
	}
}
