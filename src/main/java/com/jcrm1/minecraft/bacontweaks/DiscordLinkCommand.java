package com.jcrm1.minecraft.bacontweaks;

import java.util.UUID;

import com.jcrm1.minecraft.bacontweaks.discord.LinkCommandListener;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class DiscordLinkCommand {
	private static final Component REQUIRE_PLAYER_MESSAGE = Component.literal("Must be a player")
			.withStyle(ChatFormatting.BOLD)
			.withStyle(ChatFormatting.RED);
	private static final Component CLICK_TO_COPY_MESSAGE = Component.literal("Click to copy")
			.withStyle(ChatFormatting.GREEN);
	public static final String COMMAND_NAME = "beginlink";
	
	private Bacon mod;
	private PlayerMemberLinks pmLinks;
	
	
	public DiscordLinkCommand(Bacon mod) {
		this.mod = mod;
	}
	
	public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal(COMMAND_NAME).executes((ctx) -> {
			return execute(ctx);
		}));
	}
	
	public int execute(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		if (source.isPlayer()) {
			if (pmLinks == null) pmLinks = mod.getPlayerMemberLinks();
			UUID reqUUID = pmLinks.beginLink(source.getPlayer());
			MutableComponent message = Component.literal("Type /" + LinkCommandListener.LINK_COMMAND_NAME + " on Discord then paste this: " + reqUUID.toString() + " (click to copy)");
			message.setStyle(message.getStyle()
					.withColor(ChatFormatting.GREEN)
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, CLICK_TO_COPY_MESSAGE))
					.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, reqUUID.toString()))
					);
			source.sendSystemMessage(message);
			return Command.SINGLE_SUCCESS;
		} else {
			source.sendFailure(REQUIRE_PLAYER_MESSAGE);
			return Command.SINGLE_SUCCESS;
		}
	}
}
