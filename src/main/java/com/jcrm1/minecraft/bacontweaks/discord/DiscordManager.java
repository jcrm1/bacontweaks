package com.jcrm1.minecraft.bacontweaks.discord;

import java.util.HashSet;

import com.jcrm1.minecraft.bacontweaks.Bacon;
import com.jcrm1.minecraft.bacontweaks.voicechat.BaconVoicePlugin;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionInvalidateEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.messages.MessageRequest;
import net.minecraft.server.MinecraftServer;

//public class DiscordManager {
public class DiscordManager implements EventListener {
	// TODO: Set these before compiling
	private static final String BOT_TOKEN = "";
	private static final long MONITOR_SERVER_ID = 0L;
	private static final long LOGINS_CHANNEL_ID = 0L;
	private static final long CHAT_CHANNEL_ID = 0L;
	private static final long RCON_CHANNEL_ID = 0L;
	private static final long LOGS_CHANNEL_ID = 0L;
	private static final long VOICE_CHANNEL_ID = 0L;
	private boolean isVoiceReady = false;
	private JDA jda = null;
	private Guild guild;
	private TextChannel loginsChannel;
	private TextChannel chatChannel;
	private TextChannel rconChannel;
	private TextChannel logsChannel;
	private VoiceChannel voiceChannel;
	public final long selfId;
	public DiscordManager(Bacon mod, MinecraftServer minecraftServer) throws DiscordException {
		jda = JDABuilder.createDefault(BOT_TOKEN)
//				.addEventListeners(new DiscordManager())
				.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES)
				.setChunkingFilter(ChunkingFilter.ALL)
	            .setMemberCachePolicy(MemberCachePolicy.ALL)
				.addEventListeners(new LinkCommandListener(mod, minecraftServer, this), new MessageListener(mod, minecraftServer, this))
				.build();
		try {
			jda.awaitReady();
		} catch (InterruptedException e) {
			throw new DiscordException("Interrupted while waiting for API to ready", e.getCause());
		}
		selfId = jda.getSelfUser().getIdLong();
		
		guild = jda.getGuildById(MONITOR_SERVER_ID);
		if (guild == null) throw new DiscordException("Unable to get monitor server");
		loginsChannel = guild.getTextChannelById(LOGINS_CHANNEL_ID);
		if (loginsChannel == null) throw new DiscordException("Unable to get logins channel");
		chatChannel = guild.getTextChannelById(CHAT_CHANNEL_ID);
		if (chatChannel == null) throw new DiscordException("Unable to get chat channel");
		rconChannel = guild.getTextChannelById(RCON_CHANNEL_ID);
		if (rconChannel == null) throw new DiscordException("Unable to get rcon channel");
		logsChannel = guild.getTextChannelById(LOGS_CHANNEL_ID);
		if (logsChannel == null) throw new DiscordException("Unable to get logs channel");
		voiceChannel = guild.getVoiceChannelById(VOICE_CHANNEL_ID);
		if (voiceChannel == null) throw new DiscordException("Unable to get voice bridge channel");
		
		MessageRequest.setDefaultMentions(new HashSet<MentionType>());
		
		guild.updateCommands().addCommands(LinkCommandListener.LINK_COMMAND, LinkCommandListener.STOP_COMMAND).queue();
		guild.getAudioManager().openAudioConnection(voiceChannel);
		Bacon.addVPReadyUser(this::onVPReady);
		
		logsChannel.sendMessage("# Bot connected!").queue();
	}
	
	private void onVPReady(BaconVoicePlugin voicePlugin) {
		guild.getAudioManager().setSendingHandler(Bacon.getVoicePlugin().getMcToDiscordBridge());
		guild.getAudioManager().setReceivingHandler(Bacon.getVoicePlugin().getDiscordToMcBridge());
		this.isVoiceReady = true;
	}
	
	public void disconnect() {
		logsChannel.sendMessage("# Bot disconnected!").queue();
		isVoiceReady = false;
		jda.shutdown();
	}
	
	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof SessionDisconnectEvent || event instanceof SessionInvalidateEvent || event instanceof ShutdownEvent) {
			disconnect();
		}
	}
	
	public void sendLogMessage(String msg) {
		logsChannel.sendMessage(msg).queue();
	}
	
	public boolean isVoiceReady() {
		return isVoiceReady;
	}
	
	public Guild getMonitorServer() {
		return guild;
	}
	public TextChannel getChatChannel() {
		return chatChannel;
	}
	public TextChannel getLogsChannel() {
		return logsChannel;
	}
	public TextChannel getRconChannel() {
		return rconChannel;
	}
	public TextChannel getLoginsChannel() {
		return loginsChannel;
	}
}
