package facemywrath.riseofempires.empires.members;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.enums.Rank;

public class Member {
	
	private MemberManager mm;
	
	private UUID uuid;
	private int id;
	private Rank rank;
	
	private boolean claiming;
	
	public Member(MemberManager manager, int id, UUID uuid, Rank rank)
	{
		this.rank = rank;
		this.mm = manager;
		this.uuid = uuid;
		this.id = id;
	}
	
	public Member(UUID uuid, Rank rank)
	{
		this.rank = rank;
		this.uuid = uuid;
	}

	public void setRank(Rank rank2) {
		this.rank = rank2;
		this.getEmpire().getConfig().set("Members." + this.id + ".Rank", rank2.toString());
		this.getEmpire().saveConfig();
		this.getEmpire().reloadConfig();
		this.getEmpire().broadcastToMembers("&6" + Bukkit.getOfflinePlayer(uuid).getName() + " &2 has been set to the rank of &b " + StringUtils.capitalise(rank2.toString().toLowerCase()));
	}
	//Getters
	
	public MemberManager getMemberManager()
	{
		return this.mm;
	}
	
	public Empire getEmpire()
	{
		return this.mm.getEmpire();
	}
	
	public int getId()
	{
		return id;
	}
	
	public boolean getClaiming()
	{
		return this.claiming;
	}
	
	public Rank getRank()
	{
		return this.rank;
	}
	
	public UUID getUUID()
	{
		return this.uuid;
	}

}
