package com.jcrm1.minecraft.bacontweaks;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class JoinOpenGroupCommand {
	private static final Component REQUIRE_PLAYER_MESSAGE = Component.literal("Must be a player");
	private static final Component NO_VC_MESSAGE = Component.literal("Must be connected to voice chat first");
	private static final Component JOINED_GROUP_MESSAGE = Component.literal("Joined group")
			.withStyle(ChatFormatting.GREEN);
	public static final String COMMAND_NAME = "joinopenvc";
	
	public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal(COMMAND_NAME).executes((ctx) -> {
			return execute(ctx);
		}));
	}
	
	public int execute(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		if (source.isPlayer()) {
			VoicechatConnection conn = Bacon.getVoicePlugin().getApi().getConnectionOf(source.getPlayer().getUUID());
			if (conn == null) {
				source.sendFailure(NO_VC_MESSAGE);
				return 0;
			}
			conn.setGroup(Bacon.getVoicePlugin().getOpenGroup());
			source.sendSystemMessage(JOINED_GROUP_MESSAGE);
			return Command.SINGLE_SUCCESS;
		} else {
			source.sendFailure(REQUIRE_PLAYER_MESSAGE);
			return 0;
		}
	}
}
