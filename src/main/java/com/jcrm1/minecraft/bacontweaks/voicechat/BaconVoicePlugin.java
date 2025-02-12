package com.jcrm1.minecraft.bacontweaks.voicechat;

import java.util.HashMap;

import com.jcrm1.minecraft.bacontweaks.Bacon;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiosender.AudioSender;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.JoinGroupEvent;
import de.maxhenkel.voicechat.api.events.LeaveGroupEvent;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartingEvent;

@ForgeVoicechatPlugin
public class BaconVoicePlugin implements VoicechatPlugin {
	private static final String VC_ID = "baconvc";
	private static final String VC_NAME = "Discord Bridge";
	private static final String VC_NAME_OPEN = "Discord Bridge - Open";
	private static final String VC_DESC = "Discord voice chat bridge provided by Bacon";
	private Group normalGroup = null;
	private Group openGroup = null;
	private VoicechatServerApi api = null;
	private final HashMap<VoicechatConnection, AudioSender> senders = new HashMap<VoicechatConnection, AudioSender>();
	private final AudioMinecraftToDiscord mcToDiscord = new AudioMinecraftToDiscord();
	private final AudioDiscordToMinecraft discordToMc = new AudioDiscordToMinecraft(senders); 
	
	@Override
	public String getPluginId() {
		return Bacon.MODID;
	}
	
	@Override
	public void initialize(VoicechatApi api) {
		Bacon.setVoicePlugin(this);
		Bacon.callVPReadyUsers(this);
		Bacon.getLogger().info("Bacon voice chat plugin initialized");
	}
	
	@Override
	public void registerEvents(EventRegistration registration) {
	    registration.registerEvent(VoicechatServerStartingEvent.class, this::onServerStarting);
	    registration.registerEvent(MicrophonePacketEvent.class, mcToDiscord::onMicrophonePacket);
	}
	
	public void onServerStarting(VoicechatServerStartingEvent event) {
		api = event.getVoicechat();
		api.registerVolumeCategory(api.volumeCategoryBuilder().setId(VC_ID).setName(VC_NAME).setDescription(VC_DESC).build());
		normalGroup = api.groupBuilder().setHidden(false).setName(VC_NAME).setPersistent(true).setType(Group.Type.NORMAL).build();
		openGroup = api.groupBuilder().setHidden(false).setName(VC_NAME_OPEN).setPersistent(true).setType(Group.Type.OPEN).build();
		Bacon.getLogger().info("Bacon voice server starting");
	}
	
	public void onJoinGroup(JoinGroupEvent event) {
		if (event.getGroup() == normalGroup || event.getGroup() == openGroup) {
			senders.put(event.getConnection(), event.getVoicechat().createAudioSender(event.getConnection()));
		}
	}
	
	public void onLeaveGroup(LeaveGroupEvent event) {
		AudioSender sender = senders.get(event.getConnection());
		if (sender != null) event.getVoicechat().unregisterAudioSender(sender);
	}
	
	public VoicechatServerApi getApi() {
		return api;
	}
	
	public Group getNormalGroup() {
		return normalGroup;
	}
	
	public Group getOpenGroup() {
		return openGroup;
	}
	
	public AudioMinecraftToDiscord getMcToDiscordBridge() {
		return this.mcToDiscord;
	}
	
	public AudioDiscordToMinecraft getDiscordToMcBridge() {
		return discordToMc;
	}

}
