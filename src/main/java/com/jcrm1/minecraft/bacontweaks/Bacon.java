package com.jcrm1.minecraft.bacontweaks;

import com.jcrm1.minecraft.bacontweaks.discord.DiscordManager;
import com.jcrm1.minecraft.bacontweaks.discord.DiscordOutputStream;
import com.jcrm1.minecraft.bacontweaks.voicechat.BaconVoicePlugin;
import com.mojang.logging.LogUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Bacon.MODID)
public class Bacon {
    public static final String MODID = "bacontweaks";
    
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private DiscordManager discordManager;
    
    private static BaconVoicePlugin voicePlugin;
    
    private PlayerMemberLinks pmLinks;
    
    private static List<Consumer<BaconVoicePlugin>> vpReadyUsers = new ArrayList<Consumer<BaconVoicePlugin>>();
    
    private static String resourcePackHash = null;
	private static final Component NEW_PACK_MESSAGE = Component.literal("New resource pack available!")
			.withStyle(ChatFormatting.BOLD)
			.withStyle(ChatFormatting.GOLD)
			.append(Component.literal(" Relog to download.").setStyle(Style.EMPTY.withBold(false)).withStyle(ChatFormatting.YELLOW));
	// TODO: Add your own pack hash endpoint
	private static final String PACK_HASH_ENDPOINT = "";
	private static final HttpClient CLIENT = HttpClient.newHttpClient();
	private static final HttpRequest PACK_HASH_REQUEST = HttpRequest.newBuilder()
            .uri(URI.create(PACK_HASH_ENDPOINT))
            .GET()
            .build();
    
    public Bacon() {
//        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
//        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

//    private void commonSetup(final FMLCommonSetupEvent event) {
//        // Some common setup code
//        LOGGER.info("Bacon common setup");
//    }
    
    public static Logger getLogger() {
    	return LOGGER;
    }
    
    public PlayerMemberLinks getPlayerMemberLinks() {
    	return pmLinks;
    }
    
    private void close() {
    	discordManager.disconnect();
    	try {
			pmLinks.close();
		} catch (Exception e) {
			Bacon.getLogger().error("Failed to save " + pmLinks.getFileName() + ".");
			e.printStackTrace();
		}
    }
    
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
    	close();
    }
    
    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
    	close();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) throws Exception {
        // Do something when the server starts
        LOGGER.info("Bacon main setup starting");
        discordManager = new DiscordManager(this, event.getServer());
        LOGGER.info("Discord setup complete");
        pmLinks = new PlayerMemberLinks(event.getServer(), discordManager.getMonitorServer());
        MinecraftForge.EVENT_BUS.register(new PlayerEnterExitNotifier(discordManager.getLoginsChannel(), pmLinks));
        MinecraftForge.EVENT_BUS.register(new ChatListener(pmLinks, discordManager.getChatChannel()));
        MinecraftForge.EVENT_BUS.register(new SmiteItemInteractHandler());
        OutputStream discordStreamOut = new DiscordOutputStream(discordManager);
        OutputStream discordStreamErr = new DiscordOutputStream(discordManager);
        System.setOut(new PrintStream(new TeeOutputStream(System.out, discordStreamOut)));
        System.setErr(new PrintStream(new TeeOutputStream(System.err, discordStreamErr)));
        
        LOGGER.info("Bacon main setup complete");
        
        refreshResourcePack(null);
    }
    
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
    	new DiscordLinkCommand(this).register(event.getDispatcher());
    	new JoinNormalGroupCommand().register(event.getDispatcher());
    	new JoinOpenGroupCommand().register(event.getDispatcher());
    	new RefreshResourcePackCommand().register(event.getDispatcher());
    }
    
    public static void setVoicePlugin(BaconVoicePlugin bVP) {
    	voicePlugin = bVP;
    }
    
    public static void addVPReadyUser(Consumer<BaconVoicePlugin> user) {
    	vpReadyUsers.add(user);
    }
    
    public static BaconVoicePlugin getVoicePlugin() {
    	return voicePlugin;
    }
    
    public static void callVPReadyUsers(BaconVoicePlugin voicePlugin) {
    	vpReadyUsers.forEach((user) -> {
    		user.accept(voicePlugin);
    	});
    }

	public static String getResourcePackHash() {
		return resourcePackHash;
	}

	public static void setResourcePackHash(String resourcePackHash) {
		Bacon.resourcePackHash = resourcePackHash;
	}
	
	public static void refreshResourcePack(@Nullable PlayerList playerList) {
		new Thread(() -> {
			try {
				Bacon.setResourcePackHash(CLIENT.send(PACK_HASH_REQUEST, HttpResponse.BodyHandlers.ofString()).body().stripTrailing());
				if (playerList != null) playerList.broadcastSystemMessage(NEW_PACK_MESSAGE, false);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
