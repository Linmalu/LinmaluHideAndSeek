package com.linmalu.hideandseek;

import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.packetwrapper.WrapperPlayServerNamedSoundEffect;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.linmalu.hideandseek.Data.ChangeEntity;
import com.linmalu.hideandseek.Data.ChangePlayer;
import com.linmalu.hideandseek.Data.GameData;
import com.linmalu.library.api.LinmaluMain;

public class Main extends LinmaluMain
{
	public static Main getMain()
	{
		return (Main)LinmaluMain.getMain();
	}

	private GameData data;

	public void onEnable()
	{
		super.onEnable();
		data = new GameData();
		HideAndSeek();
		registerCommand(new Main_Command());
		registerEvents(new Main_Event());
	}
	public GameData getGameData()
	{
		return data;
	}
	private void HideAndSeek()
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Server.NAMED_SOUND_EFFECT, PacketType.Play.Client.USE_ENTITY)
		{
			public void onPacketSending(PacketEvent event)
			{
				if(!data.isGame())
				{
					return;
				}
				if(event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING)
				{
					new ChangeEntity(event.getPlayer(), event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0));
				}
				else if(event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN)
				{
					new ChangePlayer(event.getPlayer(), event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0));
				}
				else if(event.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT)
				{
					WrapperPlayServerNamedSoundEffect se = new WrapperPlayServerNamedSoundEffect(event.getPacket());
					if(se.getSoundEffect().toString().contains("SHEEP"))
					{
						event.setCancelled(true);
					}
				}
			}
			public void onPacketReceiving(PacketEvent event)
			{
				if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY)
				{
					WrapperPlayClientUseEntity ue = new WrapperPlayClientUseEntity(event.getPacket());
					Player player = data.getBlock(ue.getTargetID());
					if(player != null && !event.getPlayer().getUniqueId().equals(player.getUniqueId()))
					{
						ue.setTargetID(player.getEntityId());
					}
				}
			}
		});
	}
}
