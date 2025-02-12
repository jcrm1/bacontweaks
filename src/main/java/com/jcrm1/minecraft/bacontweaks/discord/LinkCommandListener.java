package com.jcrm1.minecraft.bacontweaks.discord;

import java.util.UUID;

import com.jcrm1.minecraft.bacontweaks.Bacon;
import com.jcrm1.minecraft.bacontweaks.PlayerMemberLinks;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.entity.player.Player;

public class LinkCommandListener extends ListenerAdapter {
	/*
	 * Make Discord command name public
	 * Make all other Discord command data protected
	 * Make all other data private
	 */
	
	private Bacon mod;
	private DiscordManager discordManager;
	
	// LINK COMMAND //
	public static final String LINK_COMMAND_NAME = "endlink";
	protected static final String LINK_ID_OPTION = "id";
	protected static final String LINK_INVALID_ID_MESSAGE = "Invalid ID. Use /beginlink in Minecraft";
	protected static final String LINK_VALID_ID_MESSAGE = "Successfully linked to user ";
	protected static final SlashCommandData LINK_COMMAND = Commands.slash(LINK_COMMAND_NAME, "Link your Discord account to your Minecraft account (use /beginlink in Minecraft first)")
			.addOption(OptionType.STRING, LINK_ID_OPTION, "The ID you were provided with from /beginlink in Minecraft");
	private PlayerMemberLinks pmLinks = null;
	
	// STOP COMMAND //
	public static final String STOP_COMMAND_NAME = "stop";
	protected static final String STOP_MSG = "Stopping server";
	protected static final String WRONG_SERVER_MSG = "Not a dedicated server. Refusing to stop";
	protected static final SlashCommandData STOP_COMMAND = Commands.slash(STOP_COMMAND_NAME, "Stop the connected Minecraft server (this will also terminate the monitor bot)");
	private static final String STOP_MINECRAFT_COMMAND = "stop";
	private MinecraftServer minecraftServer;
	
	public LinkCommandListener(Bacon mod, MinecraftServer minecraftServer, DiscordManager discordManager) {
		this.mod = mod;
		this.minecraftServer = minecraftServer;
		this.discordManager = discordManager;
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getName().equals(LINK_COMMAND_NAME)) {
			event.deferReply(true).queue();
			if (pmLinks == null) pmLinks = mod.getPlayerMemberLinks();
			String stringUUID = event.getOption(LINK_ID_OPTION).getAsString();
			UUID uuid = null;
			try {
				uuid = UUID.fromString(stringUUID);
			} catch (IllegalArgumentException e) {
				event.getHook().sendMessage(LINK_INVALID_ID_MESSAGE).queue();
				return;
			}
			// uuid will not be null here
			if (pmLinks.endLink(uuid, event.getMember().getIdLong())) {
				// pmLinks.getPlayer will not be null here
				Player player = pmLinks.getPlayer(event.getMember());
				event.getHook().sendMessage(LINK_VALID_ID_MESSAGE + player.getName().getString()).queue();
				player.sendSystemMessage(Component.literal(LINK_VALID_ID_MESSAGE + event.getMember().getEffectiveName()));
			} else event.getHook().sendMessage(LINK_INVALID_ID_MESSAGE).queue();
		} else if (event.getName().equals(STOP_COMMAND_NAME)) {
			if (minecraftServer instanceof DedicatedServer) {
				event.reply(STOP_MSG).queue();
				discordManager.disconnect();
				((DedicatedServer) minecraftServer).handleConsoleInput(STOP_MINECRAFT_COMMAND, minecraftServer.createCommandSourceStack());
			} else {
				event.reply(WRONG_SERVER_MSG).queue();
			}
		} else {
			event.reply("No such command").setEphemeral(true).queue();;
		}
	}
}
