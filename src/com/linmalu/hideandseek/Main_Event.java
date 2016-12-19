package com.linmalu.hideandseek;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.linmalu.hideandseek.Data.EntityData;
import com.linmalu.hideandseek.Data.GameData;
import com.linmalu.hideandseek.Data.GameType;
import com.linmalu.hideandseek.Data.Hint;
import com.linmalu.hideandseek.Data.PlayerData;
import com.linmalu.library.api.LinmaluAutoRespawn;

public class Main_Event implements Listener
{
	private final GameData data = Main.getMain().getGameData();

	@EventHandler
	public void Event(ChunkUnloadEvent event)
	{
		if(data.isGame())
		{
			for(Entity e : event.getChunk().getEntities())
			{
				if(data.getEntityData(e.getEntityId()) != null)
				{
					event.setCancelled(true);
					break;
				}
			}
		}
	}
	@EventHandler
	public void Event(PlayerBedEnterEvent event)
	{
		if(data.isGame())
		{
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void Event(PlayerDeathEvent event)
	{
		if(data.isGame())
		{
			Player player = event.getEntity();
			if(data.getPlayerData(player.getUniqueId()) != null)
			{
				Player killer = player.getKiller();
				if(killer != null)
				{
					killer.setHealth(killer.getMaxHealth());
				}
				player.getInventory().clear();
				LinmaluAutoRespawn.respawn(player);
				data.diePlayer(player);
			}
		}
	}
	@EventHandler
	public void Event(PlayerRespawnEvent event)
	{
		if(data.isGame())
		{
			event.setRespawnLocation(data.getSpawn());
		}
	}
	@EventHandler
	public void Event(EntityDamageByEntityEvent event)
	{
		if(data.isGame())
		{
			Entity e1 = event.getEntity();
			Entity e2 = event.getDamager();
			PlayerData pd1 = data.getPlayerData(e1.getUniqueId());
			PlayerData pd2 = data.getPlayerData(e2.getUniqueId());
			if(e1.getType() != EntityType.PLAYER && e2.getType() == EntityType.PLAYER)
			{
				if(pd2 != null && !pd2.isLive())
				{
					event.setCancelled(true);
				}
				else if(pd2 != null && pd2.isLive() && pd2.isTagger())
				{
					event.setDamage(200);
					if(!((Player)e2).hasPotionEffect(PotionEffectType.JUMP))
					{
						((Player)e2).damage(2);
					}
				}
			}
			else if(e1.getType() == EntityType.PLAYER && e2.getType() == EntityType.PLAYER)
			{
				if(pd1 != null && pd2 != null && (!pd1.isLive() || !pd2.isLive()))
				{
					event.setCancelled(true);
				}
				else if(pd1 != null && pd2 != null && pd1.isTagger())
				{
					event.setDamage(0);
				}
			}
		}
	}
	@EventHandler
	public void Event(EntityTargetEvent event)
	{
		if(data.isGame())
		{
			if(data.getEntityData(event.getEntity().getEntityId()) != null)
			{
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void Event(EntityCombustEvent event)
	{
		if(data.isGame())
		{
			if(data.getEntityData(event.getEntity().getEntityId()) != null && !(event instanceof EntityCombustByBlockEvent || event instanceof EntityCombustByEntityEvent))
			{
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void Event(PlayerInteractEvent event)
	{
		if(data.isGame() && event.getHand() == EquipmentSlot.HAND)
		{
			Player player = event.getPlayer();
			PlayerData pd = data.getPlayerData(player.getUniqueId());
			if(pd == null)
			{
				return;
			}
			if(pd.isLive() && !pd.isTagger() && pd.getType() == GameType.블럭 && event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				pd.setBlockData(event.getClickedBlock());
			}
			else if(event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
			{
				ItemStack item = event.getItem();
				if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
				{
					return;
				}
				if(pd.isLive() && !pd.isTagger() && item.getType() == Material.MONSTER_EGG && item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "분신소환알"))
				{
					if(item.getAmount() > 1)
					{
						item.setAmount(item.getAmount() - 1);
					}
					else
					{
						player.getInventory().setItemInMainHand(null);
					}
					LivingEntity entity = player.getWorld().spawn(player.getLocation(), Sheep.class);
//					int i = new Random().nextInt(10);
//					switch(i)
//					{
//						case 0:
//							entity = player.getWorld().spawn(player.getLocation(), Bat.class);
//							break;
//						case 1:
//							entity = player.getWorld().spawn(player.getLocation(), Chicken.class);
//							break;
//						case 2:
//							entity = player.getWorld().spawn(player.getLocation(), Cow.class);
//							break;
//						case 3:
////							entity = player.getWorld().spawn(player.getLocation(), Pig.class);
//							break;
//						case 4:
////							entity = player.getWorld().spawn(player.getLocation(), Enderman.class);
//							break;
//						case 5:
////							entity = player.getWorld().spawn(player.getLocation(), Ocelot.class);
//							break;
//						case 6:
////							entity = player.getWorld().spawn(player.getLocation(), Zombie.class);
//							break;
//						case 7:
//							entity = player.getWorld().spawn(player.getLocation(), Spider.class);
//							break;
//						case 8:
////							entity = player.getWorld().spawn(player.getLocation(), Wolf.class);
//							break;
//						case 9:
//							entity = player.getWorld().spawn(player.getLocation(), Skeleton.class);
//							break;
//					}
					if(entity != null)
					{
						EntityEquipment ee = entity.getEquipment();
						ee.setBoots(null);
						ee.setBootsDropChance(0F);
						ee.setChestplate(null);
						ee.setChestplateDropChance(0F);
						ee.setHelmet(null);
						ee.setHelmetDropChance(0F);
						ee.setItemInMainHand(null);
						ee.setItemInMainHandDropChance(0F);
						ee.setItemInOffHand(null);
						ee.setItemInOffHandDropChance(0F);
						ee.setLeggings(null);
						ee.setLeggingsDropChance(0F);
						data.addEntityData(entity.getEntityId(), new EntityData(player, entity));
					}
				}
				else if(pd.isLive() && pd.isTagger() && item.getType() == Material.FIREWORK_CHARGE && item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "힌트아이템"))
				{
					if(item.getAmount() > 1)
					{
						item.setAmount(item.getAmount() - 1);
					}
					else
					{
						player.getInventory().setItemInMainHand(null);
					}
					for(PlayerData data : data.getPlayers())
					{
						if(data.isLive() && !data.isTagger())
						{
							new Hint(data.getPlayer().getLocation());
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void Event(PlayerMoveEvent event)
	{
		if(data.isGame())
		{
			if(event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ())
			{
				PlayerData pd = data.getPlayerData(event.getPlayer().getUniqueId());
				if(pd != null && !pd.isTagger() && pd.getType() == GameType.블럭)
				{
					pd.moveBlock(event.getTo());
				}
			}
		}
	}
	@EventHandler
	public void Event(PlayerToggleSneakEvent event)
	{
		if(data.isGame())
		{
			if(event.isSneaking())
			{
				PlayerData pd = data.getPlayerData(event.getPlayer().getUniqueId());
				if(pd != null && !pd.isTagger() && pd.getType() == GameType.블럭)
				{
					pd.sneakBlock();
				}
			}
		}
	}
	@EventHandler
	public void Event(PlayerQuitEvent event)
	{
		if(data.isGame())
		{
			PlayerData pd = data.getPlayerData(event.getPlayer().getUniqueId());
			if(pd != null && !pd.isTagger() && pd.getType() == GameType.블럭)
			{
				pd.removeBlock();
			}
		}
	}
}
