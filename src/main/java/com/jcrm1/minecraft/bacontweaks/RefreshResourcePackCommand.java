package com.jcrm1.minecraft.bacontweaks;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class RefreshResourcePackCommand {
	public static final String COMMAND_NAME = "refreshresourcepack";
	
	public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal(COMMAND_NAME)
				.requires((source) -> source.hasPermission(3))
				.executes((ctx) -> {
			return execute(ctx);
		}));
	}
	
	public int execute(CommandContext<CommandSourceStack> ctx) {
		Bacon.refreshResourcePack(ctx.getSource().getServer().getPlayerList());
		return Command.SINGLE_SUCCESS;
	}
}
