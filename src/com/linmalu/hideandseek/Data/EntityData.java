package com.linmalu.hideandseek.Data;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class EntityData
{
	private Player player;

	public EntityData(Player player, Entity entity)
	{
		this.player = player;
	}
	public UUID getPlayer()
	{
		return player.getUniqueId();
	}
	public void sendPacket(Player player, Entity entity)
	{
		WrapperPlayServerNamedEntitySpawn nes = new WrapperPlayServerNamedEntitySpawn();
		nes.setEntityID(entity.getEntityId());
		nes.setPosition(entity.getLocation().toVector());
		nes.setYaw(entity.getLocation().getYaw());
		nes.setPitch(entity.getLocation().getPitch());
		nes.setPlayerUUID(this.player.getUniqueId());
		nes.setMetadata(WrappedDataWatcher.getEntityWatcher(this.player));
		nes.sendPacket(player);
	}
}
