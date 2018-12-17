package facemywrath.riseofempires.empires;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import facemywrath.riseofempires.empires.members.Member;
import facemywrath.riseofempires.enums.Rank;
import facemywrath.riseofempires.main.Main;
import net.md_5.bungee.api.ChatColor;

public class EmpireManager {

	private Main pl;
	private List<Empire> empires = new ArrayList<>();

	public EmpireManager(Main main) {
		this.pl = main;
	}

	public void loadEmpires()
	{
		pl.getFileManager().checkFiles();
		File dataf = pl.getDataFolder();
		File empireFolder = new File(dataf, "empires");
		for(File empireFile : empireFolder.listFiles())
		{
			FileConfiguration config = YamlConfiguration.loadConfiguration(empireFile);
			if(config.contains("Type" ) && (config.getString("Type").equals("SANCTUARY") || config.getString("Type").equals("BATTLEGROUND")))
			{
				empires.add(new Empire(this, new Location(Bukkit.getWorld(config.getString("World")), config.getInt("Origin.X"), config.getInt("Origin.Y"), config.getInt("Origin.Z")), empireFile.getName().substring(0,empireFile.getName().length()-4), config.getString("Type")));
			}
			else
				empires.add(new Empire(this, new Location(Bukkit.getWorld(config.getString("World")), config.getInt("Buildings.0.X"), config.getInt("Buildings.0.Y"), config.getInt("Buildings.0.Z")), new Member(UUID.fromString(config.getString("Creator")), Rank.EMPEROR), empireFile.getName().substring(0,empireFile.getName().length()-4)));
		}
	}

	//Statics

	public static Empire getEmpireByFile(File f)
	{
		for(Empire emp : Main.getPlugin(Main.class).getEmpireManager().getEmpires())
		{
			if(emp.getName().equals(f.getName().substring(0, f.getName().length()-4)))
				return emp;
		}
		return null;	
	}

	public static boolean doesEmpireExist(String s)
	{
		Main main = Main.getPlugin(Main.class);
		for(Empire emp : main.getEmpireManager().getEmpires())
		{
			if(emp.getName().equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	public static boolean isTagUsed(String s)
	{
		Main main = Main.getPlugin(Main.class);
		String tag = ChatColor.translateAlternateColorCodes('&', "&9[&b" + s.toUpperCase() + "&9] &r");
		for(Empire emp : main.getEmpireManager().getEmpires())
		{
			if(emp.getType() == EmpireType.EMPIRE)
				if(emp.getTag().equalsIgnoreCase(tag))
					return true;
		}
		return false;
	}

	//Getters

	public List<Empire> getEmpires()
	{
		return this.empires;
	}

	public Main getMain()
	{
		return this.pl;
	}

}
