package com.jcrm1.minecraft.bacontweaks.voicechat;

import java.util.HashMap;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.audiosender.AudioSender;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.OpusPacket;

public class AudioDiscordToMinecraft implements AudioReceiveHandler {
	private final HashMap<VoicechatConnection, AudioSender> senders;
	public AudioDiscordToMinecraft(HashMap<VoicechatConnection, AudioSender> senders) {
		this.senders = senders;
	}
	
	@Override
	public boolean canReceiveEncoded() {
		return true;
	}
	@Override
	public void handleEncodedAudio(OpusPacket packet) {
		final byte[] data = packet.getOpusAudio();
		for (AudioSender sender : senders.values()) {
			sender.send(data);
		}
	}
}
