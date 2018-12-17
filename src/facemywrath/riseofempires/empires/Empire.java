package facemywrath.riseofempires.empires;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import facemywrath.riseofempires.empires.buildings.BuildingManager;
import facemywrath.riseofempires.empires.claims.Claim;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.empires.members.Member;
import facemywrath.riseofempires.empires.members.MemberManager;
import net.md_5.bungee.api.ChatColor;

public class Empire {

	//Dependency Injection Variables
	private EmpireManager em;
	private BuildingManager bm;
	private MemberManager mm;
	private ClaimManager cm;
	//Personal Variables
	private Location loc;
	private Member emperor;
	private FileConfiguration config;
	private String name;
	private double gold = 0;
	private double exp = 0;
	private String tag;
	private EmpireType type;
	private int level = 1;
	private int builders = 2;
	private boolean open = true;

	public Empire(EmpireManager em, Location loc, Member emperor, String name)
	{
		this.emperor = emperor;
		this.name = name;
		this.em = em;
		this.loc = loc;
		this.type = EmpireType.EMPIRE;
		File empireFile = new File(em.getMain().getDataFolder() + File.separator + "empires", name + ".yml");
		if(!empireFile.exists())
		{
			createNewEmpire();
		}
		this.config = YamlConfiguration.loadConfiguration(empireFile);
		this.gold = config.getDouble("Gold");
		this.exp = config.getDouble("Experience");
		this.level = config.getInt("Buildings.0.Level");
		this.builders = 2*level;
		this.open = config.getBoolean("Open");
		this.setTag(config.getString("Tag"));
		if(this.mm == null)
			mm = new MemberManager(this);
		if(this.cm == null)
			cm = new ClaimManager(this);
		if(this.bm == null)
			bm = new BuildingManager(this);
	}
	
	//Sanctuary
	public Empire(EmpireManager em, Location loc, String name, String type)
	{
		this.name = name;
		this.loc = loc;
		this.type = EmpireType.valueOf(type);
		this.em = em;
		File empireFile = new File(em.getMain().getDataFolder() + File.separator + "empires", name + ".yml");
		if(!empireFile.exists())
		{
			createNewSpecialEmpire();
		}
		this.config = YamlConfiguration.loadConfiguration(empireFile);
		if(this.cm == null)
			cm = new ClaimManager(this);
	}

	public void broadcastToMembers(String s)
	{
		if(mm != null && mm.getMembers().size() > 0)
			for(Member member : mm.getMembers())
			{
				if(Bukkit.getOfflinePlayer(member.getUUID()).isOnline())
					if(tag != null)
						Bukkit.getPlayer(member.getUUID()).sendMessage(tag + ChatColor.translateAlternateColorCodes('&', s));
					else
						Bukkit.getPlayer(member.getUUID()).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
			}
	}

	public void createNewSpecialEmpire()
	{
		int chunkx = this.loc.getChunk().getX();
		int chunkz = this.loc.getChunk().getZ();
		File empireFile = new File(em.getMain().getDataFolder() + File.separator + "empires", name + ".yml");
		try{
			empireFile.createNewFile();
			FileConfiguration config = YamlConfiguration.loadConfiguration(empireFile);
			this.config = config;
			config.set("Type", this.type.toString());
			config.set("World", this.loc.getWorld().getName());
			config.set("Origin.X", this.loc.getX());
			config.set("Origin.Y", this.loc.getY());
			config.set("Origin.Z", this.loc.getZ());
			int count = 0;
			String claimsx = "";
			String claimsz = "";
			for(int i = -1; i <= 1; i++)
			{
				for(int j = -1; j <= 1; j++)
				{
					claimsx = claimsx + (chunkx + i) + ",";
					claimsz = claimsz + (chunkz + j) + ",";
					count++;
				}
			}
			config.set("Claims.X", claimsx);
			config.set("Claims.Z", claimsz);
			saveConfig();
			reloadConfig();
		}catch(Exception e){e.printStackTrace();}
	
	}
	
	public void setClaimSize(int size)
	{
		int chunkx = this.loc.getChunk().getX();
		int chunkz = this.loc.getChunk().getZ();
		int count = 0;
		String claimsx = "";
		String claimsz = "";
		cm.getClaims().clear();
		for(int i = -1*size; i <= size; i++)
		{
			for(int j = -1*size; j <= size; j++)
			{
				cm.addClaim(new Claim(cm, chunkx+i, chunkz+j));
				claimsx = claimsx + (chunkx + i) + ",";
				claimsz = claimsz + (chunkz + j) + ",";
				count++;
			}
		}
		config.set("Claims.X", claimsx);
		config.set("Claims.Z", claimsz);
		saveConfig();
		reloadConfig();
	}

	@SuppressWarnings("deprecation")
	public void createNewEmpire()
	{
		int chunkx = this.loc.getChunk().getX();
		int chunkz = this.loc.getChunk().getZ();
		File empireFile = new File(em.getMain().getDataFolder() + File.separator + "empires", name + ".yml");
		try{
			empireFile.createNewFile();
			FileConfiguration config = YamlConfiguration.loadConfiguration(empireFile);
			this.config = config;
			config.set("Creator", emperor.getUUID().toString());
			config.set("Tag", "");
			config.set("Open", true);
			config.set("Type", "EMPIRE");
			config.set("World", this.loc.getWorld().getName());
			config.set("Level", 1);
			config.set("Experience", 0.0);
			config.set("Gold", 0.0);
			config.set("Members.0.UUID", emperor.getUUID().toString());
			config.set("Members.0.Rank", emperor.getRank().toString());
			config.set("Buildings.0.Type", "Town_Hall");
			config.set("Buildings.0.PlacedBy", emperor.getUUID().toString());
			config.set("Buildings.0.Level", 1);
			config.set("Buildings.0.Direction", 0);
			config.set("Buildings.0.X", this.loc.getBlockX());
			config.set("Buildings.0.Y", this.loc.getBlockY());
			config.set("Buildings.0.Z", this.loc.getBlockZ());
			config.set("Buildings.0.Generating", true);
			int count = 0;
			String claimsx = "";
			String claimsz = "";
			for(int i = -1; i <= 1; i++)
			{
				for(int j = -1; j <= 1; j++)
				{
					claimsx = claimsx + (chunkx + i) + ",";
					claimsz = claimsz + (chunkz + j) + ",";
					count++;
				}
			}
			config.set("Claims.X", claimsx);
			config.set("Claims.Z", claimsz);
			saveConfig();
			reloadConfig();
		}catch(Exception e){e.printStackTrace();}
		mm = new MemberManager(this);
		bm = new BuildingManager(this);
		bm.getTownHall().create();
		BukkitScheduler sch = this.em.getMain().getServer().getScheduler();
		sch.scheduleSyncDelayedTask(this.em.getMain(), new BukkitRunnable()
		{
			public void run()
			{
				broadcastToMembers("Empire " + name + " created.");
			}
		}, 20L);
	}

	//Setters

	public void addExp(double i)
	{
		this.exp += i;
		config.set("Experience", this.exp);
		saveConfig();
		reloadConfig();
	}

	public void addMoney(double i)
	{
		this.gold += i;
		config.set("Gold", this.exp);
		saveConfig();
		reloadConfig();
	}

	public void saveConfig()
	{
		try{
			config.save(new File(this.getEmpireManager().getMain().getDataFolder() + File.separator + "empires", name + ".yml"));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isOpen()
	{
		return this.open;
	}

	public void toggleOpen(boolean flag)
	{
		this.open = false;
		getConfig().set("Open", flag);
		saveConfig();
		reloadConfig();
	}

	public void setTag(String s)
	{
		getConfig().set("Tag", s);
		this.tag = ChatColor.translateAlternateColorCodes('&', "&9[&b" + s.toUpperCase() + "&9] &r");
		broadcastToMembers("&2Empire tag changed.");
		saveConfig();
		reloadConfig();
	}

	public void subtractGold(int i)
	{
		this.gold = this.gold - i;
		getConfig().set("Gold", this.gold);
		saveConfig();
		reloadConfig();
	}

	//Getters

	public String getTag()
	{
		return tag;
	}

	public FileConfiguration getConfig()
	{
		return this.config;
	}

	public Location getLocation()
	{
		return this.loc;
	}

	public String getName()
	{
		return this.name;
	}

	public double getExperience()
	{
		return this.exp;
	}

	public ClaimManager getClaimManager()
	{
		return this.cm;
	}

	public MemberManager getMemberManager()
	{
		return this.mm;
	}
	
	public EmpireType getType()
	{
		return this.type;
	}

	public EmpireManager getEmpireManager()
	{
		return this.em;
	}

	public BuildingManager getBuildingManager()
	{
		return this.bm;
	}

	public double getGold() {
		return this.gold;
	}

	public void reloadConfig() {
		File empireFile = new File(em.getMain().getDataFolder() + File.separator + "empires", name + ".yml");
		if(empireFile.exists())
			this.config = YamlConfiguration.loadConfiguration(empireFile);

	}

}
