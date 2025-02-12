package com.jcrm1.minecraft.bacontweaks.voicechat;

import java.nio.ByteBuffer;

import com.jcrm1.minecraft.bacontweaks.Bacon;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioMinecraftToDiscord implements AudioSendHandler {
	
	private ByteBuffer bb = null;
	private Group normalGroup;
	private Group openGroup;
	
	public AudioMinecraftToDiscord() {
		normalGroup = Bacon.getVoicePlugin().getNormalGroup();
		openGroup = Bacon.getVoicePlugin().getOpenGroup();
	}
	
	public void onMicrophonePacket(MicrophonePacketEvent event) {
		if (event.getSenderConnection().getGroup() == normalGroup || event.getSenderConnection().getGroup() == openGroup)
		bb = ByteBuffer.wrap(event.getPacket().getOpusEncodedData());
	}

	@Override
	public boolean canProvide() {
		return bb != null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		return bb;
	}
	
	@Override
	public boolean isOpus() {
		return true;
	}
}
