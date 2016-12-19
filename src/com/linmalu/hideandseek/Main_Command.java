package com.linmalu.hideandseek;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.linmalu.hideandseek.Data.GameData;
import com.linmalu.hideandseek.Data.GameType;
import com.linmalu.library.api.LinmaluTellraw;
import com.linmalu.library.api.LinmaluVersion;

public class Main_Command implements CommandExecutor
{
	private GameType type = GameType.진짜;
	private ArrayList<UUID> taggers = new ArrayList<>();
	private int number = 1;
	private int time = 300;

	public Main_Command()
	{
		Main.getMain().getCommand(Main.getMain().getDescription().getName()).setTabCompleter(new TabCompleter()
		{
			@Override
			public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
			{
				ArrayList<String> list = new ArrayList<>();
				if(args.length == 1)
				{
					list.add("시작");
					list.add("종료");
					list.add("종류");
					list.add("술래");
					list.add("술래인원");
					list.add("시간");
					list.add("스폰");
					list.add("술래아이템");
					list.add("도망자아이템");
				}
				else if(args.length == 2 && args[0].equals("종류"))
				{
					for(GameType type : GameType.values())
					{
						list.add(type.toString());
					}
				}
				return list.stream().filter(msg -> msg.startsWith(args[args.length - 1])).count() == 0 ? list : list.stream().filter(msg -> msg.startsWith(args[args.length - 1])).collect(Collectors.toList());
			}
		});
	}
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[])
	{
		if(sender instanceof Player && sender.isOp())
		{
			GameData data = Main.getMain().getGameData();
			Player player = (Player)sender;
			if(args.length == 1 && args[0].equals("시작"))
			{
				if(data.isGame())
				{
					sender.sendMessage(ChatColor.YELLOW + "이미 게임이 시작중입니다.");
					return true;
				}
				int size = Bukkit.getOnlinePlayers().size();
				if(size <= 1)
				{
					sender.sendMessage(ChatColor.YELLOW + "최소인원 2명이 되지 않습니다.");
					return true;
				}
				int tsize = taggers.size();
				if(size <= tsize || (tsize == 0 && size <= number))
				{
					sender.sendMessage(ChatColor.YELLOW + "숨는인원이 부족합니다.");
					return true;
				}
				if(data.getSpawn() == null)
				{
					data.setSpawn(player.getLocation());
				}
				data.GameStart(type, taggers, number, time);
				return true;
			}
			else if(args.length == 1 && args[0].equals("종료"))
			{
				if(data.isGame())
				{
					data.GameStop();
				}
				else
				{
					sender.sendMessage(ChatColor.YELLOW + "게임이 시작되지 않았습니다.");
				}
				return true;
			}
			else if(args.length == 2 && args[0].equals("종류"))
			{
				GameType type = GameType.getGameType(args[1]);
				if(type != null)
				{
					this.type = type;
					sender.sendMessage(ChatColor.GREEN + "게임종류가 설정되었습니다. - " + ChatColor.YELLOW + args[1]);
					return true;
				}
			}
			else if(args.length >= 1 && args[0].equals("술래"))
			{
				taggers.clear();
				for(int i = 1; i < args.length; i++)
				{
					taggers.add(Bukkit.getOfflinePlayer(args[i]).getUniqueId());
				}
				sender.sendMessage(ChatColor.GREEN + "술래가 설정되었습니다. - " + ChatColor.YELLOW + taggers.size() + "명");
				return true;
			}
			else if(args.length == 2 && args[0].equals("술래인원"))
			{
				try
				{
					int number = Integer.parseInt(args[1]);
					if(number < 1)
					{
						sender.sendMessage(ChatColor.YELLOW + "0 이하는 입력할 수 없습니다.");
					}
					else
					{
						this.number = number;
						sender.sendMessage(ChatColor.GREEN + "술래인원이 설정되었습니다. - " + ChatColor.YELLOW + number + "명");
					}
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "숫자가 입력되지 않았습니다.");
				}
				return true;
			}
			else if(args.length == 2 && args[0].equals("시간"))
			{
				try
				{
					int time = Integer.parseInt(args[1]);
					if(time < 60)
					{
						sender.sendMessage(ChatColor.YELLOW + "1분이하는 입력할 수 없습니다.");
					}
					else
					{
						this.time = time;
						sender.sendMessage(ChatColor.GREEN + "시간이 " + time + "초로 설정되었습니다.");
					}
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "숫자가 입력되지 않았습니다.");
				}
				return true;
			}
			else if(args.length >= 1 && args[0].equals("스폰"))
			{
				try
				{
					Location loc = player.getLocation();
					if(args.length == 4)
					{
						loc.setX(Double.parseDouble(args[1]));
						loc.setY(Double.parseDouble(args[2]));
						loc.setZ(Double.parseDouble(args[3]));
					}
					data.setSpawn(loc);
					sender.sendMessage(ChatColor.GREEN + "스폰이 설정되었습니다.");
					sender.sendMessage(ChatColor.GOLD + " X : " + ChatColor.YELLOW + loc.getBlockX() + ChatColor.GOLD + " / Y : " + ChatColor.YELLOW + loc.getBlockY() + ChatColor.GOLD + " / Z : " + ChatColor.YELLOW + loc.getBlockZ());
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "숫자가 입력되지 않았습니다.");
				}
				return true;
			}
			else if(args.length == 2 && args[0].equals("술래아이템"))
			{
				try
				{
					data.setTaggerItem(Integer.parseInt(args[1]));
					sender.sendMessage(ChatColor.GREEN + "술래아이템이 " + args[1] + "개로 설정되었습니다.");
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "숫자가 입력되지 않았습니다.");
				}
				return true;
			}
			else if(args.length == 2 && args[0].equals("도망자아이템"))
			{
				try
				{
					data.setFugitiveItem(Integer.parseInt(args[1]));
					sender.sendMessage(ChatColor.GREEN + "도망자아이템이 " + args[1] + "개로 설정되었습니다.");
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "숫자가 입력되지 않았습니다.");
				}
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + " = = = = = [ 숨 바 꼭 질 ] = = = = =");
			LinmaluTellraw.sendChat(sender, "/" + label + " 시작 ", ChatColor.GOLD + "/" + label + " 시작" + ChatColor.GRAY + " : 게임시작");
			LinmaluTellraw.sendChat(sender, "/" + label + " 종료 ", ChatColor.GOLD + "/" + label + " 종료" + ChatColor.GRAY + " : 게임종료");
			LinmaluTellraw.sendChat(sender, "/" + label + " 종류 ", ChatColor.GOLD + "/" + label + " 종류 <게임종류>" + ChatColor.GRAY + " : 게임종류설정(진짜, 블럭, 랜덤)");
			LinmaluTellraw.sendChat(sender, "/" + label + " 술래 ", ChatColor.GOLD + "/" + label + " 술래 (플레이어) (플레이어)..." + ChatColor.GRAY + " : 술래설정(플레이어가 없을경우 술래설정취소)");
			LinmaluTellraw.sendChat(sender, "/" + label + " 술래인원 ", ChatColor.GOLD + "/" + label + " 술래인원 <인원수>" + ChatColor.GRAY + " : 술래인원설정");
			LinmaluTellraw.sendChat(sender, "/" + label + " 시간 ", ChatColor.GOLD + "/" + label + " 시간 <시간(초)>" + ChatColor.GRAY + " : 시간설정");
			LinmaluTellraw.sendChat(sender, "/" + label + " 스폰 ", ChatColor.GOLD + "/" + label + " 스폰 (X Y Z)" + ChatColor.GRAY + " : 스폰설정(좌표가 없을경우 자신의 위치로 설정)");
			LinmaluTellraw.sendChat(sender, "/" + label + " 술래아이템 ", ChatColor.GOLD + "/" + label + " 술래아이템 <갯수>" + ChatColor.GRAY + " : 술래아이템갯수설정(기본:도망자인원 / 술래인원)");
			LinmaluTellraw.sendChat(sender, "/" + label + " 도망자아이템 ", ChatColor.GOLD + "/" + label + " 도망자아이템 <갯수>" + ChatColor.GRAY + " : 도망자아이템갯수설정(기본:술래인원 X 64)");
			sender.sendMessage(ChatColor.YELLOW + "제작자 : " + ChatColor.AQUA + "린마루(Linmalu)" + ChatColor.WHITE + " - http://blog.linmalu.com");
			if(sender.isOp())
			{
				LinmaluVersion.check(Main.getMain(), sender);
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "권한이 없습니다.");
		}
		return true;
	}
}
