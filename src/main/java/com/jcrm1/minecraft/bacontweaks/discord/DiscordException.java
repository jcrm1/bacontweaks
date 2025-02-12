package com.jcrm1.minecraft.bacontweaks.discord;

public class DiscordException extends Exception {

	private static final long serialVersionUID = 7161274827375355341L;
	
	public DiscordException() {
        super();
    }
    public DiscordException(String message) {
        super(message);
    }
    public DiscordException(String message, Throwable cause) {
        super(message, cause);
    }
    public DiscordException(Throwable cause) {
        super(cause);
    }
}
