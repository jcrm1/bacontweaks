package com.jcrm1.minecraft.bacontweaks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerMemberLinks implements AutoCloseable {
	private TreeBidiMap<UUID, Long> map = new TreeBidiMap<UUID, Long>();
	private MinecraftServer server;
	private Guild guild;
	private final File linksFile;
	
	// This is not persisted
	// First UUID is the request UUID, second is the player UUID
	private HashMap<UUID, UUID> linkRequestUUIDs = new HashMap<UUID, UUID>();
	
	@SuppressWarnings("unchecked")
	public PlayerMemberLinks(MinecraftServer server, Guild guild) {
		this.server = server;
		this.guild = guild;
		File baconConfigDir = server.getServerDirectory().resolve("config").resolve(Bacon.MODID).toFile();
		baconConfigDir.mkdirs();
		linksFile = baconConfigDir.toPath().resolve("player_member_links.bin").toFile();
		if (linksFile.exists()) {
			try (
					FileInputStream fis = new FileInputStream(linksFile);
					ObjectInputStream ois = new ObjectInputStream(fis);
			) {
				Object obj = ois.readObject();
				if (obj instanceof TreeBidiMap) {
					map = (TreeBidiMap<UUID, Long>) obj;
					Bacon.getLogger().info("Loaded " + linksFile.getName());
				}
			} catch (IOException | ClassNotFoundException e) {
				Bacon.getLogger().error("Failed to load " + linksFile.getName() + ". Creating new link database");
				e.printStackTrace();
			}
		}
		Bacon.getLogger().debug(this.toString());
	}
	
	// returns link request UUID
	public UUID beginLink(UUID playerUUID) {
		UUID requestUUID = UUID.randomUUID();
		linkRequestUUIDs.put(requestUUID, playerUUID);
		return requestUUID;
	}
	// returns link request UUID
	public UUID beginLink(Player player) {
		return beginLink(player.getUUID());
	}
	
	// returns true on success;
	public boolean endLink(UUID requestUUID, long memberID) {
		UUID playerUUID = linkRequestUUIDs.get(requestUUID);
		if (playerUUID == null) return false;
		map.put(playerUUID, memberID);
		return true;
	}
	 
	public ServerPlayer getPlayer(long memberID) {
		UUID uuid = map.getKey(memberID);
		if (uuid == null) return null;
		return server.getPlayerList().getPlayer(uuid);
	}
	public ServerPlayer getPlayer(Member member) {
		return getPlayer(member.getIdLong());
	}
	
	public Member getMember(UUID playerUUID) {
		Long id = map.get(playerUUID);
		if (id == null) return null;
		return guild.getMemberById(id);
	}
	public Member getMember(Player player) {
		return getMember(player.getUUID());
	}
	
	public String getFileName() {
		return linksFile.getName();
	}

	@Override
	public void close() throws Exception {
		try (
				FileOutputStream fos = new FileOutputStream(linksFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
		) {
			oos.writeObject(map);
		}
	}
	
	@Override
	public String toString() {
		String str = "{\n";
		MapIterator<UUID, Long> it = map.mapIterator();
		while (it.hasNext()) {
			str += "\t" + it.next() + ": " + it.getValue() + ",\n";
		}
		str += "}";
		return str;
	}
	
	public String getDiscordMention(Player player) {
		Member member = getMember(player.getUUID());
		return ((member == null) ? "**" + player.getName().getString() + "**" : "<@" + member.getId() + ">");
	}
	
	public String getMinecraftMention(Member member) {
		Player player = getPlayer(member.getIdLong());
		return ((player == null) ? member.getEffectiveName() : player.getName().getString());
	}
	public String getMinecraftMention(User user) {
		Player player = getPlayer(user.getIdLong());
//		Bacon.getLogger().debug("Found user " + user.getIdLong() + " but " + player.toString());
		return ((player == null) ? user.getEffectiveName() : player.getName().getString());
	}
}
