package com.linmalu.hideandseek.Data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Hint
{
	private Location location;

	public Hint(Location location)
	{
		this.location = location;
		double size = 0;
		for(double y = 2; y <= 7; y += 0.2)
		{
			for(double x = (size < 3 ? -size : -1); x <= (size < 3 ? size : 1); x += 0.2)
			{
				play(x, y);
			}
			size += 0.2;
		}
		location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_LAUNCH, 2, 1);
	}
	private void rotationY(Location loc, double angle)
	{
		double x = loc.getX();
		double z = loc.getZ();
		loc.setX(Math.cos(Math.toRadians(angle)) * x + Math.sin(Math.toRadians(angle)) * z);
		loc.setY(loc.getY());
		loc.setZ(-Math.sin(Math.toRadians(angle)) * x + Math.cos(Math.toRadians(angle)) * z);
	}
	private double yawAngle(Player player)
	{
		Location loc = player.getLocation();
		double x = loc.getX() - location.getX();
		double z = loc.getZ() - location.getZ();
		return Math.toDegrees(Math.atan2(x, z));
	}
	private void play(double y, double x)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			Location loc = location.clone();
			loc.setX(y);
			loc.setY(x);
			loc.setZ(0);
			rotationY(loc, yawAngle(player) + 180);
			loc.setX(location.getX() + loc.getX());
			loc.setZ(location.getZ() + loc.getZ());
			loc.setY(location.getY() + loc.getY());
			player.spawnParticle(Particle.FIREWORKS_SPARK, loc, 0);
		}
	}
}
