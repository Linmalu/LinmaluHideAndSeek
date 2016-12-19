package com.linmalu.hideandseek.Data;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity.ObjectTypes;
import com.linmalu.hideandseek.Main;

public class PlayerData
{
	private final GameData data = Main.getMain().getGameData();
	private UUID player;
	private boolean tagger;
	private GameType type;
	private boolean live = true;
	private WrapperPlayServerSpawnEntity changeBlock;

	public PlayerData(Player player, boolean tagger, GameType type)
	{
		this.player = player.getUniqueId();
		this.tagger = tagger;
		if(type == GameType.랜덤)
		{
			if(new Random().nextInt(2) == 0)
			{
				type = GameType.진짜;
			}
			else
			{
				type = GameType.블럭;
			}
		}
		this.type = type;
		if(!tagger && type == GameType.블럭)
		{
			Entity e = player.getWorld().spawnEntity(player.getLocation(), EntityType.COW);
			e.remove();
			changeBlock = new WrapperPlayServerSpawnEntity(e, ObjectTypes.FALLING_BLOCK, 1);
			data.addBlock(e.getEntityId(), player.getUniqueId());
		}
		if(tagger)
		{
			data.getTaggerTeam().addEntry(player.getName());
		}
		else
		{
			data.getFugitiveTeam().addEntry(player.getName());
		}
	}
	public Player getPlayer()
	{
		return Bukkit.getPlayer(player);
	}
	public boolean isTagger()
	{
		return tagger;
	}
	public GameType getType()
	{
		return type;
	}
	public boolean isLive()
	{
		return live;
	}
	public void setDead()
	{
		live = false;
	}
	public int getBlockData()
	{
		return changeBlock.getObjectData();
	}
	@SuppressWarnings("deprecation")
	public void setBlockData(Block block)
	{
		int value = block.getTypeId() | (block.getData() << 12);
		changeBlock.setObjectData(value);
		spawnBlock();
	}
	public void spawnBlock()
	{
		for(PlayerData pd : data.getPlayers())
		{
			spawnBlock(pd);
		}
	}
	public void spawnBlock(PlayerData pd)
	{
		changeBlock.sendPacket(pd.getPlayer());
	}
	public void removeBlock()
	{
		for(PlayerData pd : data.getPlayers())
		{
			removeBlock(pd.getPlayer());
		}
	}
	public void removeBlock(Player player)
	{
		WrapperPlayServerEntityDestroy ed = new WrapperPlayServerEntityDestroy();
		ed.setEntityIds(new int[]{changeBlock.getEntityID()});
		ed.sendPacket(player);
	}
	public void moveBlock(Location loc)
	{
		if(getPlayer().isSneaking())
		{
			sneakBlock();
		}
		else
		{
			changeBlock.setX(loc.getX());
			changeBlock.setY(loc.getY());
			changeBlock.setZ(loc.getZ());
			spawnBlock();
		}
	}
	public void sneakBlock()
	{
		Location loc = getPlayer().getLocation();
		changeBlock.setX(loc.getBlockX() + 0.5D);
		changeBlock.setY(loc.getY());
		changeBlock.setZ(loc.getBlockZ() + 0.5D);
		spawnBlock();
	}
}
