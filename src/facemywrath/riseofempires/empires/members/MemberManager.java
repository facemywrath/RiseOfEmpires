package facemywrath.riseofempires.empires.members;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireManager;
import facemywrath.riseofempires.enums.Rank;
import facemywrath.riseofempires.main.Main;
import net.md_5.bungee.api.ChatColor;

public class MemberManager {

	private Empire emp;

	private List<Member> members = new ArrayList<>();

	public MemberManager(Empire empire)
	{
		this.emp = empire;
		loadMembers();
	}

	public void loadMembers()
	{
		emp.getEmpireManager().getMain().getFileManager().checkFiles();
		File empireFolder = new File(emp.getEmpireManager().getMain().getDataFolder(), "empires");
		File empireFile = new File(empireFolder, emp.getName() + ".yml");
		if(empireFile.exists())
		{
			FileConfiguration config = YamlConfiguration.loadConfiguration(empireFile);
			if(config.contains("Members"))
				for(String s : config.getConfigurationSection("Members").getKeys(false))
				{
					members.add(new Member(this, Integer.parseInt(s), UUID.fromString(config.getString("Members." + s + ".UUID")), Rank.valueOf(config.getString("Members." + s + ".Rank"))));
				}
		}
	}

	public void addMember(UUID uniqueId, Rank valueOf) {
		int id = this.getEmpire().getConfig().getConfigurationSection("Members").getKeys(false).size();
		members.add(new Member(this, id, uniqueId, valueOf));	
		Bukkit.getPlayer(uniqueId).sendMessage(ChatColor.DARK_GREEN + "Welcome to " + this.getEmpire().getName());
		this.getEmpire().getConfig().set("Members." + id + ".UUID", uniqueId.toString());
		this.getEmpire().getConfig().set("Members." + id + ".Rank", valueOf.toString());
		this.getEmpire().saveConfig();
		this.getEmpire().reloadConfig();
	}

	public void playerLeaveEmpire(Member member)
	{
		if(members.contains(member))
		{
			if(Bukkit.getOfflinePlayer(member.getUUID()).isOnline())
			{
				Bukkit.getPlayer(member.getUUID()).sendMessage(ChatColor.translateAlternateColorCodes('&', emp.getTag() + " &cYou have left &b" + emp.getName()));
			}
			this.getEmpire().getConfig().set("Members." + member.getId() + ".UUID", null);
			this.getEmpire().getConfig().set("Members." + member.getId() + ".Rank", null);
			this.getEmpire().getConfig().set("Members." + member.getId(), null);
			members.remove(member);
			List<UUID> pchats = this.emp.getEmpireManager().getMain().getCommandObj().getPChats();
			if(pchats.contains(member.getUUID()))
			{
				this.emp.getEmpireManager().getMain().getCommandObj().getPChats().remove(member.getUUID());
			}
			this.getEmpire().saveConfig();
			this.getEmpire().reloadConfig();
			this.emp.broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&b" + StringUtils.capitalise(member.getRank().toString().toLowerCase()) + " " +  Bukkit.getOfflinePlayer(member.getUUID()).getName() + " &chas left the empire."));

		}

	}
	
	public List<Member> getOnlineMembers()
	{
		List<Member> online = new ArrayList<>();
		for(Member member : members)
		{
			if(Bukkit.getOfflinePlayer(member.getUUID()).isOnline())
				online.add(member);
		}
		return online;
	}

	public void kickMember(Member member)
	{
		if(members.contains(member))
		{
			if(Bukkit.getOfflinePlayer(member.getUUID()).isOnline())
			{
				Bukkit.getPlayer(member.getUUID()).sendMessage(ChatColor.translateAlternateColorCodes('&', emp.getTag() + " &cYou have been kicked out of &b" + emp.getName()));
			}
			List<UUID> pchats = this.emp.getEmpireManager().getMain().getCommandObj().getPChats();
			if(pchats.contains(member.getUUID()))
			{
				this.emp.getEmpireManager().getMain().getCommandObj().getPChats().remove(member.getUUID());
			}
			this.getEmpire().getConfig().set("Members." + member.getId() + ".UUID", null);
			this.getEmpire().getConfig().set("Members." + member.getId() + ".Rank", null);
			this.getEmpire().getConfig().set("Members." + member.getId(), null);
			members.remove(member);
			this.getEmpire().saveConfig();
			this.getEmpire().reloadConfig();
			this.emp.broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&b" + StringUtils.capitalise(member.getRank().toString().toLowerCase()) + " " +  Bukkit.getOfflinePlayer(member.getUUID()).getName() + " &chas been kicked from the empire."));

		}
	}

	public void setRank(Member member, Rank rank)
	{
		if(members.contains(member))
		{
			member.setRank(rank);
		}
	}

	//Static Functions

	public static int toInt(Rank rank)
	{
		switch(rank)
		{
		case OUTCAST:
			return 0;
		case CITIZEN:
			return 1;
		case BUILDER:
			return 2;
		case GENERAL:
			return 3;
		case EMPEROR:
			return 4;
		}
		return 1;
	}

	public static Member getMember(Player player)
	{
		Main pl = Main.getPlugin(Main.class);
		EmpireManager manager = pl.getEmpireManager();
		if(manager != null && manager.getEmpires().size() > 0)
			for(Empire empire : manager.getEmpires())
			{
				if(empire.getMemberManager() != null && empire.getMemberManager().getMembers().size() > 0)
					for(Member member : empire.getMemberManager().getMembers())
					{
						if(member.getUUID().equals(player.getUniqueId()))
							return member;
					}
			}
		return null;
	}

	public static Member getMember(String string) {
		Main pl = Main.getPlugin(Main.class);
		EmpireManager manager = pl.getEmpireManager();
		if(manager != null && manager.getEmpires().size() > 0)
			for(Empire empire : manager.getEmpires())
			{
				for(Member member : empire.getMemberManager().getMembers())
				{
					if(Bukkit.getOfflinePlayer(string) != null && member.getUUID().equals(Bukkit.getOfflinePlayer(string).getUniqueId()))
						return member;
				}
			}
		return null;
	}

	public static Member getMember(UUID uniqueId) {
		Main pl = Main.getPlugin(Main.class);
		EmpireManager manager = pl.getEmpireManager();
		if(manager != null && manager.getEmpires().size() > 0)
			for(Empire empire : manager.getEmpires())
			{
				for(Member member : empire.getMemberManager().getMembers())
				{
					if(Bukkit.getOfflinePlayer(uniqueId) != null && member.getUUID().equals(uniqueId))
						return member;
				}
			}
		return null;
	}


	//Getters

	public Empire getEmpire()
	{
		return this.emp;
	}

	public List<Member> getMembers()
	{
		return this.members;
	}
}
