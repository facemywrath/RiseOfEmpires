package facemywrath.riseofempires.storage;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import facemywrath.riseofempires.managers.UserManager;
import net.md_5.bungee.api.ChatColor;

public class User {

	private UserManager um;
	private OfflinePlayer p;
	private int prestige;
	private int ClassPoints;

	public User(UserManager um, OfflinePlayer p, int prestige, int cp)
	{
		this.um = um;
		this.p = p;
		this.ClassPoints = cp;
		this.prestige = prestige;
	}

	public File getFile()
	{
		return this.um.getFile(this);
	}

	public void setClassPoints(int i)
	{
		this.ClassPoints = i;
		FileConfiguration config = YamlConfiguration.loadConfiguration(this.um.getFile(this));
		config.set("ClassPoints", i);
		try{
			config.save(this.um.getFile(this));
		}catch(Exception e){}
	}

	public void addClassPoints(int i)
	{
		this.ClassPoints += i;
		FileConfiguration config = YamlConfiguration.loadConfiguration(this.um.getFile(this));
		config.set("ClassPoints", ClassPoints);
		if(p.isOnline())
		{
			Bukkit.getPlayer(p.getUniqueId()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2Gained &6" + i + " &2Class Points! You can use these at to unlock classes at &5Class Trainers&2. You must use them before you can prestige again."));
		}
		try{
			config.save(this.um.getFile(this));
		}catch(Exception e){}
	}

	public boolean takeClassPoints(int i)
	{
		if(this.ClassPoints >= i)
		{
			this.ClassPoints -= i;
			FileConfiguration config = YamlConfiguration.loadConfiguration(this.um.getFile(this));
			config.set("ClassPoints", ClassPoints);
			try{
				config.save(this.um.getFile(this));
			}catch(Exception e){}
			return true;
		}
		return false;
	}

	public void setPrestige(int i)
	{
		this.prestige = i;
		FileConfiguration config = YamlConfiguration.loadConfiguration(this.um.getFile(this));
		config.set("Prestige", i);
		try{
			config.save(this.um.getFile(this));
		}catch(Exception e){}
	}

	public void finishTutorial()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(getFile());
		if(!config.contains("TutorialDone") || !config.getBoolean("TutorialDone"))
		{
			config.set("TutorialDone", true);
			try {
				config.save(getFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getPrestige()
	{
		return this.prestige;
	}

	public int getClassPoints()
	{
		return this.ClassPoints;
	}

	public OfflinePlayer getOfflinePlayer() {
		return p;
	}

	public Player getPlayer()
	{
		if(p.isOnline())
			return Bukkit.getPlayer(p.getName());
		return null;
	}

	public boolean duringTutorial() {

		FileConfiguration config = YamlConfiguration.loadConfiguration(getFile());
		if(!config.contains("TutorialDone"))
		{
			config.set("TutorialDone", false);
			try {
				config.save(getFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			return !config.getBoolean("TutorialDone");

	}

}
