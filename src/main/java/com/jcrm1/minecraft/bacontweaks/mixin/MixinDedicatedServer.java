package com.jcrm1.minecraft.bacontweaks.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.jcrm1.minecraft.bacontweaks.Bacon;

import net.minecraft.server.MinecraftServer.ServerResourcePackInfo;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {
	@Shadow
	private DedicatedServerSettings settings;
	private ServerResourcePackInfo oldInfo = null;
	
	@Inject(at = @At("HEAD"), method = "getServerResourcePack", cancellable = true)
	private void getServerResourcePack(CallbackInfoReturnable<Optional<ServerResourcePackInfo>> cir) {
		if (Bacon.getResourcePackHash() != null && oldInfo != null && oldInfo.hash().equals(Bacon.getResourcePackHash())) {
			cir.setReturnValue(Optional.of(oldInfo));
		} else {
			settings.getProperties().serverResourcePackInfo.ifPresentOrElse(packInfo -> {
				oldInfo = packInfo;
			}, () -> {
				oldInfo = null;
			});
			if (oldInfo == null) cir.setReturnValue(Optional.empty());
			else {
				if (Bacon.getResourcePackHash() == null) cir.setReturnValue(Optional.of(oldInfo));
				else {
					// need to make new packInfo
					oldInfo = new ServerResourcePackInfo(oldInfo.id(), oldInfo.url(), Bacon.getResourcePackHash(), oldInfo.isRequired(), oldInfo.prompt());
					cir.setReturnValue(Optional.of(oldInfo));
				}
			}
		}
	}
}
