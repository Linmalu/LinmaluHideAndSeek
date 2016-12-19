package com.linmalu.hideandseek.Data;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.linmalu.hideandseek.Main;

public class ChangePlayer
{
	public ChangePlayer(Player player, Entity entity)
	{
		GameData data = Main.getMain().getGameData();
		if(player != null && entity != null && entity.getType() == EntityType.PLAYER)
		{
			PlayerData pd = data.getPlayerData(entity.getUniqueId());
			if(pd != null && pd.isLive() && !pd.isTagger())
			{
				PlayerData pd1 = data.getPlayerData(player.getUniqueId());
				if(pd1 != null)
				{
					pd.spawnBlock(pd1);
				}
			}
		}
	}
}
