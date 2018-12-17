package facemywrath.riseofempires.empires.claims;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.main.Main;

public class ClaimManager {

	private Empire emp;

	private List<Claim> claims = new ArrayList<>();

	public ClaimManager(Empire emp)
	{
		this.emp = emp;
		loadClaims();
	}

	public void loadClaims()
	{
		emp.getEmpireManager().getMain().getFileManager().checkFiles();
		if(emp.getConfig() != null)
			if(emp.getConfig().contains("Claims") && emp.getConfig().getConfigurationSection("Claims").getKeys(false).size() > 0)
			{
				String strx = emp.getConfig().getString("Claims.X");
				String strz = emp.getConfig().getString("Claims.Z");
				String[] xlist = strx.split(",");
				String[] zlist = strz.split(",");
				if(strx.split(",").length == strz.split(",").length)
					for(int i = 0; i < strx.split(",").length; i++)
					{
						try{
							claims.add(new Claim(this, Integer.parseInt(xlist[i]), Integer.parseInt(zlist[i])));
						}catch(Exception e){Bukkit.broadcastMessage("Error on claim load");}
					}
			}
	}

	public void addClaim(Claim cl)
	{
		this.claims.add(cl);
		emp.getEmpireManager().getMain().getFileManager().checkFiles();
		FileConfiguration config = emp.getConfig();
		if(config.contains("Claims"))
		{
			String x = config.getString("Claims.X");
			String z = config.getString("Claims.Z");
			x = x + cl.getX() + ",";
			z = z + cl.getZ() + ",";
			config.set("Claims.X", x);
			config.set("Claims.Z", z);
			emp.saveConfig();
			emp.reloadConfig();
		}
	}

	//Statics

	public static boolean checkClaim(Location loc, Player player)
	{ // Checks to see if the location belongs to the players empire
		Main main = Main.getPlugin(Main.class);
		if(main.getCommandObj().getOverrides().contains(player.getUniqueId()))
			return true;
		if(ClaimManager.getEmpireAt(loc) != null)	
		{
			if(MemberManager.getMember(player) != null)
			{
				if(ClaimManager.getEmpireAt(loc).equals(MemberManager.getMember(player).getEmpire()))
				{
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public static Empire getEmpireAt(Location loc)
	{
		Main pl = Main.getPlugin(Main.class);
		if(pl != null && pl.getEmpireManager() != null && pl.getEmpireManager().getEmpires() != null && !pl.getEmpireManager().getEmpires().isEmpty())
			for(Empire emp : Main.getPlugin(Main.class).getEmpireManager().getEmpires())
			{
				if(loc.getWorld().equals(emp.getLocation().getWorld()))
					for(Claim clm : emp.getClaimManager().getClaims())
					{
						if(clm.getX() == loc.getChunk().getX() && clm.getZ() == loc.getChunk().getZ())
							return emp;
					}
			}
		return null;
	}

	public static Empire getEmpireAt(Chunk chunk)
	{
		if(!Main.getPlugin(Main.class).getEmpireManager().getEmpires().isEmpty())
			for(Empire emp : Main.getPlugin(Main.class).getEmpireManager().getEmpires())
			{
				if(chunk.getWorld().equals(emp.getLocation().getWorld()))
					for(Claim clm : emp.getClaimManager().getClaims())
					{
						if(clm.getX() == chunk.getX() && clm.getZ() == chunk.getZ())
							return emp;
					}
			}
		return null;
	}

	//Getters

	public Empire getEmpire()
	{
		return emp;
	}

	public List<Claim> getClaims()
	{
		return this.claims;
	}

}
