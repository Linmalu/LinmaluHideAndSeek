package com.linmalu.hideandseek.Data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.linmalu.hideandseek.Main;

public class Timer implements Runnable
{
	private final GameData data = Main.getMain().getGameData();
	private final BossBar bossbar = Bukkit.createBossBar(Main.getMain().getTitle(), BarColor.WHITE, BarStyle.SOLID);
	private final int taskId;
	private int time;
	private int time1;
	private int maxTime;

	public Timer(GameType type, int time)
	{
		this.time = time;
		Bukkit.broadcastMessage(ChatColor.GREEN + " = = = = = [ 숨 바 꼭 질 ] = = = = =");
		Bukkit.broadcastMessage(Main.getMain().getTitle() + ChatColor.GREEN + "숨바꼭질버전 : " + ChatColor.YELLOW + Main.getMain().getDescription().getVersion());
		Bukkit.broadcastMessage(ChatColor.GOLD + "종류 : " + ChatColor.YELLOW + type.toString() + ChatColor.WHITE + " / " + ChatColor.GOLD + "시간 : " + ChatColor.YELLOW + time + "초" + ChatColor.WHITE + " / " + ChatColor.GOLD + "술래 : " + ChatColor.YELLOW + getTaggerNumber() + "명");
		Bukkit.broadcastMessage(ChatColor.YELLOW + "아이템이 초기화되며 60초후 술래에게 아이템이 지급됩니다.");
		Bukkit.broadcastMessage(Main.getMain().getTitle() + ChatColor.YELLOW + "제작자 : " + ChatColor.AQUA + "린마루(Linmalu)" + ChatColor.WHITE + " - http://blog.linmalu.com");
		maxTime = time1 = 60;
		String msg = ChatColor.GOLD + "술래 : " + ChatColor.YELLOW;
		for(PlayerData pd : data.getPlayers())
		{
			Player player = pd.getPlayer();
			player.setGameMode(GameMode.SURVIVAL);
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			player.getInventory().clear();
			if(pd.isTagger())
			{
				player.sendMessage(ChatColor.YELLOW + "당신은 술래입니다.");
				msg += player.getName() + " ";
			}
			else
			{
				player.sendMessage(ChatColor.YELLOW + "당신은 술래가 아니며 머리위에 하트는 술래에게 보이지 않습니다.");
				if(pd.getType() == GameType.진짜)
				{
					player.sendMessage(ChatColor.GREEN + "당신의 능력은 분신을 소환할 수 있습니다.");
					ItemStack item = new ItemStack(Material.MONSTER_EGG, data.getFugitiveItem() >= 0 ? data.getFugitiveItem() : 64 * getTaggerNumber());
					ItemMeta im = item.getItemMeta();
					im.setDisplayName(ChatColor.GREEN + "분신소환알");
					item.setItemMeta(im);
					player.getInventory().addItem(item);
				}
				else
				{
					player.sendMessage(ChatColor.YELLOW + "당신의 능력은 우클릭으로 대상블럭으로 변신할 수 있으며 쉬프트를 누르면 칸에맞게조절됩니다.");
					pd.spawnBlock();
				}
				player.teleport(data.getSpawn());
			}
		}
		Bukkit.broadcastMessage(msg);
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getMain(), this, 0L, 20L);
	}
	public void run()
	{
		if(!data.isGame())
		{
			Bukkit.getScheduler().cancelTask(taskId);
			bossbar.setVisible(false);
			return;
		}
		if(time1 == 0)
		{
			time1 = -1;
			maxTime = time;
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래에게 아이템이 지급되었습니다.");
			int count = data.getTaggerItem() >= 0 ? data.getTaggerItem() : getFugitiveNumber() / getTaggerNumber() + (getFugitiveNumber() % getTaggerNumber() == 0 ? 0 : 1);
			for(PlayerData pd : data.getPlayers())
			{
				Player player = pd.getPlayer();
				if(pd.isTagger())
				{
					player.teleport(data.getSpawn());
					ItemStack item = new ItemStack(Material.DIAMOND_AXE);
					ItemMeta im = item.getItemMeta();
					im.setDisplayName(ChatColor.GREEN + "술래의검");
					item.setItemMeta(im);
					player.getInventory().addItem(item);
					item = new ItemStack(Material.FIREWORK_CHARGE, count);
					im = item.getItemMeta();
					im.setDisplayName(ChatColor.GREEN + "힌트아이템");
					item.setItemMeta(im);
					player.getInventory().addItem(item);
				}
			}
		}
		if(time == 0)
		{
			Bukkit.broadcastMessage(ChatColor.GREEN + "게임시간이 끝났습니다.");
			data.diePlayer();
			return;
		}
		for(PlayerData pd : data.getPlayers())
		{
			Player player = pd.getPlayer();
			player.setFoodLevel(20);
			if(pd.isLive())
			{
				if(pd.isTagger())
				{
					playTaggerEffect(player);
				}
				else
				{
					playFugitiveEffect(player);
					if(pd.getType() == GameType.블럭)
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0), true);
						//pd.spawnBlock();
					}
				}
			}
		}
		if(time1 > 0)
		{
			String timer = String.format(ChatColor.YELLOW + "%02d" + ChatColor.GOLD + " : " + ChatColor.YELLOW + "%02d" + ChatColor.WHITE + " - " + ChatColor.GREEN + " 술래 : " + ChatColor.YELLOW + getTaggerNumber() + "명" + ChatColor.GREEN + " 남은인원 : " + ChatColor.YELLOW + getFugitiveNumber() + "명" + ChatColor.WHITE + " - " + ChatColor.GRAY + "린마루", time1 / 60, time1 % 60);
			bossbar.setTitle(timer);
			bossbar.setProgress(time1 / (double)maxTime);
			for(PlayerData pd : data.getPlayers())
			{
				Player player = pd.getPlayer();
				player.setHealth(player.getMaxHealth());
				bossbar.addPlayer(player);
			}
			time1--;
		}
		else
		{
			String timer = String.format(ChatColor.YELLOW + "%02d" + ChatColor.GOLD + " : " + ChatColor.YELLOW + "%02d" + ChatColor.WHITE + " - " + ChatColor.GREEN + " 술래 : " + ChatColor.YELLOW + getTaggerNumber() + "명" + ChatColor.GREEN + " 남은인원 : " + ChatColor.YELLOW + getFugitiveNumber() + "명" + ChatColor.WHITE + " - " + ChatColor.GRAY + "린마루", time / 60, time % 60);
			bossbar.setTitle(timer);
			bossbar.setProgress(time / (double)maxTime);
			for(PlayerData pd : data.getPlayers())
			{
				Player player = pd.getPlayer();
				bossbar.addPlayer(player);
				if(pd.isLive() && pd.isTagger())
				{
					if(time < 60)
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1), true);
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40, 1), true);
					}
					else if(time < 180)
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0), true);
					}
				}
				else if(!pd.isLive())
				{
					player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0), true);
				}
			}
			if(time == 180)
			{
				Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 이동속도가 빨라집니다.");
			}
			else if(time == 60)
			{
				Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 능력이 상승합니다.");
				Bukkit.broadcastMessage(ChatColor.GREEN + "가짜를 쳤을때의 패널티가 사라집니다.");
			}
			time--;
		}
	}
	private int getTaggerNumber()
	{
		int num = 0;
		for(PlayerData pd : data.getPlayerDatas())
		{
			if(pd.isLive() && pd.isTagger())
			{
				num++;
			}
		}
		return num;
	}
	private int getFugitiveNumber()
	{
		int num = 0;
		for(PlayerData pd : data.getPlayerDatas())
		{
			if(pd.isLive() && !pd.isTagger())
			{
				num++;
			}
		}
		return num;
	}
	private void playFugitiveEffect(Player player)
	{
		for(PlayerData pd : data.getPlayers())
		{
			if(!(pd.isTagger() && pd.isLive()))
			{
				pd.getPlayer().spawnParticle(Particle.HEART, player.getLocation().add(0, 2, 0), 0);
			}
		}
	}
	private void playTaggerEffect(Player player)
	{
		for(PlayerData pd : data.getPlayers())
		{
			pd.getPlayer().spawnParticle(Particle.VILLAGER_ANGRY, player.getLocation().add(0, 2, 0), 0);
		}
	}
}
