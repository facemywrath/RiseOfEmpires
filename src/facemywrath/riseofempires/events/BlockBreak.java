package facemywrath.riseofempires.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireType;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.main.Main;
import net.md_5.bungee.api.ChatColor;

public class BlockBreak implements Listener {

	private Main pl; // Instance of main

	public BlockBreak(Main main)
	{ // Store the instance of main
		pl = main;
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event)
	{ // Check if a player can break at that location
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
	}

}
