package com.jcrm1.minecraft.bacontweaks;

import com.jcrm1.minecraft.bacontweaks.markdown.ChatParser;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChatListener {
	private PlayerMemberLinks pmLinks;
	private TextChannel channel;
	public ChatListener(PlayerMemberLinks pmLinks, TextChannel channel) {
		this.pmLinks = pmLinks;
		this.channel = channel;
	}
	@SubscribeEvent
    public void onChat(ServerChatEvent event) {
		// replace with event.getRawText?
    	channel.sendMessage(pmLinks.getDiscordMention(event.getPlayer()) + ": " + event.getRawText()).queue();
    	event.setMessage(ChatParser.parse(event.getRawText()));
    }
}
