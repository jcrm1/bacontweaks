package com.jcrm1.minecraft.bacontweaks.discord;

import com.jcrm1.minecraft.bacontweaks.Bacon;
import com.jcrm1.minecraft.bacontweaks.PlayerMemberLinks;
import com.jcrm1.minecraft.bacontweaks.markdown.ChatParser;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class MessageListener extends ListenerAdapter {
	private PlayerMemberLinks pmLinks;
	private Bacon mod;
	private MinecraftServer server;
	private DiscordManager discordManager;
	public MessageListener(Bacon mod, MinecraftServer server, DiscordManager discordManager) {
		this.mod = mod;
		this.server = server;
		this.discordManager = discordManager;
	}
	public void onMessageReceived(MessageReceivedEvent event) {
		if (pmLinks == null) pmLinks = mod.getPlayerMemberLinks();
		User author = event.getAuthor();
		if (author.isBot() || author.isSystem()) {
			// do not check for pmlink
			if (author.getIdLong() != discordManager.selfId) {
				server.getPlayerList().broadcastSystemMessage(Component.literal(author.getEffectiveName() + ": ").append(ChatParser.parse(event.getMessage().getContentDisplay())), false);
			}
		} else {
			// check for pmlink
			server.getPlayerList().broadcastSystemMessage(Component.literal(pmLinks.getMinecraftMention(author) + ": ").append(ChatParser.parse(event.getMessage().getContentDisplay())), false);
		}
	}
}
