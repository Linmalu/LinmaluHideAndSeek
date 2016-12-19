package com.linmalu.hideandseek.Data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.linmalu.hideandseek.Main;

public class ChangeEntity implements Runnable
{
	private Player player;
	private Entity entity;

	public ChangeEntity(Player player, Entity entity)
	{
		this.player = player;
		this.entity = entity;
		if(player != null && entity != null && !entity.isDead())
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getMain(), this);
		}
	}
	public void run()
	{
		EntityData ed = Main.getMain().getGameData().getEntityData(entity.getEntityId());
		if(ed != null)
		{
			ed.sendPacket(player, entity);
		}
	}
}
