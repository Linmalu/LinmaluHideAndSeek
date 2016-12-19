package com.linmalu.hideandseek.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameData
{
	private boolean game = false;
	private Location spawn;
	private HashMap<UUID, PlayerData> players = new HashMap<>();
	private HashMap<Integer, EntityData> entitys = new HashMap<>();
	private HashMap<Integer, UUID> blocks = new HashMap<>();
	private int taggerItem = -1;
	private int fugitiveItem = -1;

	public GameData()
	{
		Team team;
		team = getTaggerTeam();
		team.setAllowFriendlyFire(false);
		team.setCanSeeFriendlyInvisibles(true);
		team.setPrefix(ChatColor.GREEN + "[술래]" + ChatColor.WHITE);
		team = getFugitiveTeam();
		team.setAllowFriendlyFire(false);
		team.setCanSeeFriendlyInvisibles(true);
		clearTeam();
	}
	public void GameStart(GameType type, ArrayList<UUID> taggers, int number, int time)
	{
		game = true;
		List<Player> player = new ArrayList<>(Bukkit.getOnlinePlayers());
		if(taggers.size() == 0)
		{
			int size = number;
			ArrayList<Integer> list = new ArrayList<>();
			Random r = new Random();
			while(size > 0)
			{
				int i = r.nextInt(player.size());
				if(!list.contains(i))
				{
					list.add(i);
					size--;
				}
			}
			for(int i = 0; i < player.size(); i++)
			{
				boolean tagger = false;
				if(list.contains(i))
				{
					tagger = true;
				}
				players.put(player.get(i).getUniqueId(), new PlayerData(player.get(i), tagger, type));
			}
		}
		else
		{
			for(int i = 0; i < player.size(); i++)
			{
				boolean tagger = false;
				if(taggers.contains(player.get(i).getUniqueId()))
				{
					tagger = true;
				}
				players.put(player.get(i).getUniqueId(), new PlayerData(player.get(i), tagger, type));
			}
		}
		new Timer(type, time);
	}
	public void GameStop()
	{
		game = false;
		for(PlayerData pd : getPlayers())
		{
			pd.getPlayer().teleport(spawn);
			if(!pd.isTagger() && pd.getType() == GameType.블럭)
			{
				pd.removeBlock();
			}
		}
		players.clear();
		entitys.clear();
		blocks.clear();
		Location loc = spawn.clone();
		loc.setY(-100);
		for(Entity e : Bukkit.getWorld("world").getEntities())
		{
			if(e.getType() != EntityType.PLAYER && !e.isDead())
			{
				e.teleport(loc);
				e.remove();
			}
		}
		clearTeam();
		Bukkit.broadcastMessage(ChatColor.GREEN + "게임이 종료되었습니다.");
	}
	public void diePlayer()
	{
		for(UUID id : players.keySet())
		{
			PlayerData pd = players.get(id);
			if(pd.isTagger() && pd.isLive())
			{
				pd.setDead();
			}
		}
		gameState();
	}
	public void diePlayer(Player player)
	{
		PlayerData pd = players.get(player.getUniqueId());
		pd.setDead();
		if(!pd.isTagger() && pd.getType() == GameType.블럭)
		{
			pd.removeBlock();
		}
		Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + "님이 탈락했습니다.");
		Location loc = spawn.clone();
		loc.setY(-100);
		for(Entity e : player.getWorld().getEntities())
		{
			EntityData ed = entitys.get(e.getEntityId());
			if(e.getType() != EntityType.PLAYER && !e.isDead() && ed != null && player.getUniqueId().equals(ed.getPlayer()))
			{
				e.teleport(loc);
				e.remove();
			}
		}
		getFugitiveTeam().addEntry(player.getName());
		gameState();
	}
	private void gameState()
	{
		int p1 = 0;
		int p2 = 0;
		for(UUID id : players.keySet())
		{
			PlayerData pd = players.get(id);
			if(pd.isLive())
			{
				if(pd.isTagger())
				{
					p1++;
				}
				else
				{
					p2++;
				}
			}
		}
		if(p1 == 0)
		{
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 패배입니다.");
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 패배입니다.");
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 패배입니다.");
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 패배입니다.");
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 패배입니다.");
			GameStop();
		}
		else if(p2 == 0)
		{
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 승리입니다.");
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 승리입니다.");
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 승리입니다.");
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 승리입니다.");
			Bukkit.broadcastMessage(ChatColor.GREEN + "술래의 승리입니다.");
			GameStop();
		}
	}
	public Team getFugitiveTeam()
	{
		Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team;
		if((team = sc.getTeam("HASF")) == null)
		{
			team = sc.registerNewTeam("HASF");
		}
		return team;
	}
	public Team getTaggerTeam()
	{
		Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team;
		if((team = sc.getTeam("HAST")) == null)
		{
			team = sc.registerNewTeam("HAST");
		}
		return team;
	}
	public void clearTeam()
	{
		Team team;
		team = getFugitiveTeam();
		for(String player : team.getEntries())
		{
			team.removeEntry(player);
		}
		team = getTaggerTeam();
		for(String player : team.getEntries())
		{
			team.removeEntry(player);
		}
	}
	public boolean isGame()
	{
		return game;
	}
	public Location getSpawn()
	{
		return spawn;
	}
	public void setSpawn(Location spawn)
	{
		this.spawn = spawn;
	}
	public PlayerData getPlayerData(UUID id)
	{
		return players.get(id);
	}
	public ArrayList<PlayerData> getPlayers()
	{
		ArrayList<PlayerData> list = new ArrayList<>();
		for(UUID id : players.keySet())
		{
			PlayerData pd = players.get(id);
			if(pd.getPlayer() != null)
			{
				list.add(pd);
			}
		}
		return list;
	}
	public ArrayList<PlayerData> getPlayerDatas()
	{
		ArrayList<PlayerData> list = new ArrayList<>();
		for(UUID id : players.keySet())
		{
			list.add(players.get(id));
		}
		return list;
	}
	public EntityData getEntityData(int id)
	{
		return entitys.get(id);
	}
	public void addEntityData(int id, EntityData ed)
	{
		entitys.put(id, ed);
	}
	public void addBlock(int block, UUID player)
	{
		blocks.put(block, player);
	}
	public Player getBlock(int block)
	{
		Player player = null;
		UUID id = blocks.get(block);
		if(id != null)
		{
			player = Bukkit.getPlayer(id);
		}
		return player;
	}
	public int getTaggerItem()
	{
		return taggerItem;
	}
	public void setTaggerItem(int taggerItem)
	{
		this.taggerItem = taggerItem;
	}
	public int getFugitiveItem()
	{
		return fugitiveItem;
	}
	public void setFugitiveItem(int fugitiveItem)
	{
		this.fugitiveItem = fugitiveItem;
	}
}
