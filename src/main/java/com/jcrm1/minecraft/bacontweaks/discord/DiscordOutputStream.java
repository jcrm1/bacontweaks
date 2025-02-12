package com.jcrm1.minecraft.bacontweaks.discord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DiscordOutputStream extends OutputStream {
	private DiscordManager discordManager;
	
	public DiscordOutputStream(DiscordManager discordManager) {
		this.discordManager = discordManager;
	}
	
	private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {;
            discordManager.sendLogMessage(buffer.toString());
            buffer.reset();
        } else {
            buffer.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = off; i < off + len; i++) {
            write(b[i]);
        }
    }
}
