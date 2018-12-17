package facemywrath.riseofempires.events;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.sk89q.worldedit.data.DataException;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireType;
import facemywrath.riseofempires.empires.buildings.BuildingManager;
import facemywrath.riseofempires.empires.buildings.buildzone.BuildZone;
import facemywrath.riseofempires.empires.buildings.home.Home;
import facemywrath.riseofempires.empires.buildings.mine.Mine;
import facemywrath.riseofempires.empires.buildings.outpost.Outpost;
import facemywrath.riseofempires.empires.buildings.townhall.TownHall;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.empires.members.Member;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.enums.Rank;
import facemywrath.riseofempires.main.Main;
import net.md_5.bungee.api.ChatColor;

public class BlockPlace implements Listener{

	private Main pl;
	private HashMap<UUID,Location> naming = new HashMap<>();

	public BlockPlace(Main main)
	{
		pl = main;
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event)
	{ // Deal with the placing of structures.
		if(!ClaimManager.checkClaim(event.getBlock().getLocation(), event.getPlayer()))
		{ // Check if the block location belongs to the players empire
			if(ClaimManager.getEmpireAt(event.getBlock().getLocation()) != null)
			{
				Empire emp = ClaimManager.getEmpireAt(event.getBlock().getLocation());
				if(emp.getType() == EmpireType.EMPIRE)
				{
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', ClaimManager.getEmpireAt(event.getBlock().getLocation()).getTag() + "&4That doesn't belong to you!"));

				}
				else if(emp.getType() == EmpireType.SANCTUARY)
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bSanctuary&9] &4You can't do that in a sanctuary."));
				}
				else if(emp.getType() == EmpireType.BATTLEGROUND)
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&4BattleGround&9] &4You can't do that in a BattleGround."));
				}
				event.setCancelled(true);
			}
		}
		if(!event.isCancelled()) // If the player can build there, check to see if they're placing a structure
			checkStructurePlace(event); // Check if they're placing a structure and place it

	}

	public void checkStructurePlace(BlockPlaceEvent event)
	{ // Checks to see if a structure is being places
		if(event.getBlock().getWorld().getName().equals("pophasus") || event.getBlock().getWorld().getName().equalsIgnoreCase("world3"))
			return;
		if(event.getItemInHand() == null)
			return; // Return if they aren't holding an item
		if(!event.getItemInHand().hasItemMeta())
			return; // Return if it doesn't have a meta
		if(!event.getItemInHand().getItemMeta().hasDisplayName())
			return; // Return if it doesn't have a displayname
		if(BuildingManager.getBuildingType(event.getItemInHand()) == null)
			return; // Return if it isn't in the config file
		Player p = event.getPlayer(); // Get the player
		double yaw = p.getLocation().getYaw(); // Get their yaw
		int direction = (int) ((yaw+135)/90); // Simplify it to a 0-3 set
		direction = direction%4;
		String name = BuildingManager.getBuildingType(event.getItemInHand()); // Get the building they're trying to place
		int level = BuildingManager.getBuildingLevel(event.getItemInHand());
		if(MemberManager.getMember(event.getPlayer()) == null)
		{ // If they don't belong to an empire
			if(name.equals("Town_Hall"))
			{ // And the building is a townhall
				TownHall th = new TownHall(event.getBlock().getLocation(), direction, event.getPlayer().getUniqueId());
				if(th.createable())
				{
					event.getBlock().setType(Material.AIR);
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lWhat would you like to name your empire? (Type 'cancel' to cancel empire creation)"));
					naming.put(event.getPlayer().getUniqueId(), event.getBlock().getLocation());
				}
				else
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &4Not enough room to place that building."));
				}
			}
			return; // Return
		}

		Member member = MemberManager.getMember(event.getPlayer()); // Get the empire the player belongs to
		if(member.getRank() == Rank.OUTCAST || member.getRank() == Rank.CITIZEN)
		{
			if(!name.equals("Home") && !name.equals("BuildZone"))
			{
				event.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4That building requires a higher rank to build."));
			}
		}
		BuildingManager bm = member.getEmpire().getBuildingManager(); // Get the empire's building manager
		if((member.getRank() == Rank.BUILDER || member.getRank() == Rank.GENERAL || member.getRank() == Rank.EMPEROR && !name.equals("Home")) && !name.equals("BuildZone"))
		{ // If they're a builder, general, or emperor, and it's not a home
			event.getBlock().setType(Material.AIR);
			switch(name)
			{ // Check to see which kind of building it is and try to create it.
			case "Goldmine":
				Mine building;
				building = new Mine(bm, event.getPlayer().getUniqueId(), direction, event.getBlock().getLocation(), "Goldmine");
				if(building.createable())
				{
					building.create();
					if(level != -1)
						building.setLevel(level);
				}
				else
				{
					event.setCancelled(true);
				}
				break;
			case "Coalmine":
				Mine building3;
				building3 = new Mine(bm, event.getPlayer().getUniqueId(), direction, event.getBlock().getLocation(), "Coalmine");
				if(building3.createable())
				{
					building3.create();
					if(level != -1)
						building3.setLevel(level);
				}
				else
				{
					event.setCancelled(true);
				}
				break;
			case "Redstonemine":
				Mine building4;
				building4 = new Mine(bm, event.getPlayer().getUniqueId(), direction, event.getBlock().getLocation(), "Redstonemine");
				if(building4.createable())
				{
					building4.create();
					if(level != -1)
						building4.setLevel(level);
				}
				else
				{
					event.setCancelled(true);
				}
				break;
			case "Diamondmine":
				Mine building5;
				building5 = new Mine(bm, event.getPlayer().getUniqueId(), direction, event.getBlock().getLocation(), "Diamondmine");
				if(building5.createable())
				{
					building5.create();
					if(level != -1)
						building5.setLevel(level);
				}
				else
				{
					event.setCancelled(true);
				}
				break;
			case "Ironmine":
				Mine building6;
				building6 = new Mine(bm, event.getPlayer().getUniqueId(), direction, event.getBlock().getLocation(), "Ironmine");
				if(building6.createable())
				{
					building6.create();
					if(level != -1)
						building6.setLevel(level);
				}
				else
				{
					event.setCancelled(true);
				}
				break;
			case "Outpost":
				Outpost building2;
				try {
					building2 = new Outpost(bm, event.getPlayer().getUniqueId(), direction, event.getBlock().getLocation());
					if(building2.createable())
					{
						building2.create();
						if(level != -1)
							building2.setLevel(level);
					}
					else
					{
						event.setCancelled(true);
					}
				} catch (DataException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		else if(member.getRank() != Rank.OUTCAST)
		{
			if(name.equals("Home"))
			{ // Otherwise if they're not an outcast and creating a home, try to create it.
				event.getBlock().setType(Material.AIR);
				Home home;
				try {
					home = new Home(bm, event.getPlayer().getUniqueId(), direction, event.getBlock().getLocation());
					if(home.createable())
					{
						home.create();
						if(level != -1)
							home.setLevel(level);
					}
					else
					{
						event.setCancelled(true);
					}
				} catch (DataException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(name.equals("BuildZone"))
			{
				event.getBlock().setType(Material.AIR);
				BuildZone bz;
				try {
					bz = new BuildZone(bm, event.getPlayer().getUniqueId(), direction, event.getBlock().getLocation());
					if(bz.createable())
					{
						bz.create();
						if(level != -1)
							bz.setLevel(level);
					}
					else
					{
						event.setCancelled(true);
					}
				} catch (DataException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public boolean containsPlayer(UUID uuid)
	{ // Check if a player is trying to create an empire
		return this.naming.containsKey(uuid);
	}

	public HashMap<UUID, Location> getNamingPlayers()
	{ // Get the hashmap of players trying to create an empire
		return this.naming;
	}

	public Main getMain()
	{ // Get the main instance
		return pl;
	}

}
