package com.jcrm1.minecraft.bacontweaks;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerEnterExitNotifier {
	private TextChannel loginsChannel;
	private PlayerMemberLinks pmLinks;
	public PlayerEnterExitNotifier(TextChannel loginsChannel, PlayerMemberLinks pmLinks) {
		this.loginsChannel = loginsChannel;
		this.pmLinks = pmLinks;
	}
    
    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
    	if (event.getEntity() instanceof Player) {
    		loginsChannel.sendMessage(pmLinks.getDiscordMention((Player) event.getEntity()) + " joined the game").queue();
    	}
    }
    @SubscribeEvent
    public void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
		if (event.getEntity() instanceof Player) {
    		loginsChannel.sendMessage(pmLinks.getDiscordMention((Player) event.getEntity()) + " left the game").queue();
    	}
    }
}
