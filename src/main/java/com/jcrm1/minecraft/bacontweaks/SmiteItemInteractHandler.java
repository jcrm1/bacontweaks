package com.jcrm1.minecraft.bacontweaks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SmiteItemInteractHandler {
	private static final Item WOODEN_HOE = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "wooden_hoe"));
	@SuppressWarnings("unchecked")
	private static final EntityType<LightningBolt> LIGHTNING_BOLT = (EntityType<LightningBolt>) ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "lightning_bolt"));
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
		if (event.getItemStack().is(WOODEN_HOE)) {
			HitResult res = ProjectileUtil.getHitResultOnViewVector(event.getEntity(), (e) -> true, 100);
			if (res.getType() != HitResult.Type.MISS) {
				smite(event.getLevel(), res.getLocation());
			}
		}
	}
//	@SubscribeEvent
//	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
//		
//	}
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getItemStack().is(WOODEN_HOE) && event.getTarget().isAlive()) smite(event.getLevel(), event.getTarget().position());
	}
	
	private static boolean smite(Level level, Vec3 loc) {
		LightningBolt smiteEntity = new LightningBolt(LIGHTNING_BOLT, level);
		smiteEntity.setPos(loc);
		smiteEntity.setDamage(0f);
		smiteEntity.setVisualOnly(true);
		return level.addFreshEntity(smiteEntity);
	}
}
