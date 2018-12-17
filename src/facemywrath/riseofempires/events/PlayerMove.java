package facemywrath.riseofempires.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.herocraftonline.heroes.Heroes;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireType;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.util.GetDirection;
import facemywrath.riseofempires.util.PerlinNoise;

public class PlayerMove implements Listener {

	private HashMap<UUID, Empire> region = new HashMap<>();
	private HashMap<UUID, String> buildzoneRegion = new HashMap<>();
	private HashMap<UUID, Integer> playersInHigherLevel = new HashMap<UUID, Integer>();
	private Main main;

	public PlayerMove(Main plugin){main=plugin;}

	@EventHandler
	public void playerMove(PlayerMoveEvent event)
	{
		if(!event.getTo().getChunk().equals(event.getFrom().getChunk()))
		{
			if(PerlinNoise.getLevel(event.getTo().getChunk()) -40 > Heroes.getInstance().getCharacterManager().getHero(event.getPlayer()).getLevel())
			{
				if(!playersInHigherLevel.containsKey(event.getPlayer().getUniqueId()) || playersInHigherLevel.get(event.getPlayer().getUniqueId()) != 2)
				{
					playersInHigherLevel.put(event.getPlayer().getUniqueId(), 2);
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &4Warning: Area you've entered is 40+ levels higher than you. Caution is advised."));
				}
			}
			else if(PerlinNoise.getLevel(event.getTo().getChunk()) -25 > Heroes.getInstance().getCharacterManager().getHero(event.getPlayer()).getLevel())
			{
				if(!playersInHigherLevel.containsKey(event.getPlayer().getUniqueId()) || playersInHigherLevel.get(event.getPlayer().getUniqueId()) != 1)
				{
					playersInHigherLevel.put(event.getPlayer().getUniqueId(), 1);
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &cWarning: Area you've entered is 25+ levels higher than you. Caution is advised."));
				}
			}
			else if(!playersInHigherLevel.containsKey(event.getPlayer().getUniqueId()) || playersInHigherLevel.get(event.getPlayer().getUniqueId()) != 0)
			{
				playersInHigherLevel.put(event.getPlayer().getUniqueId(), 0);
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2Danger has passed. Entering a safe area."));
			}
		}


		if(ClaimManager.getEmpireAt(event.getTo()) != null)
		{
			Empire emp = ClaimManager.getEmpireAt(event.getTo());
			if(emp.getType() == EmpireType.EMPIRE)
			{
				if(ClaimManager.getEmpireAt(event.getTo()).getBuildingManager().getBuildZoneChunks().contains(event.getTo().getChunk()))
				{
					if(!buildzoneRegion.containsKey(event.getPlayer().getUniqueId()) || !Bukkit.getOfflinePlayer(ClaimManager.getEmpireAt(event.getTo()).getBuildingManager().getBuildZoneByChunk(event.getTo().getChunk()).getWhoPlaced()).getName().equals(buildzoneRegion.get(event.getPlayer().getUniqueId())))
					{
						buildzoneRegion.put(event.getPlayer().getUniqueId(), Bukkit.getOfflinePlayer(ClaimManager.getEmpireAt(event.getTo()).getBuildingManager().getBuildZoneByChunk(event.getTo().getChunk()).getWhoPlaced()).getName());
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', emp.getTag() + "&2Entering &6" + buildzoneRegion.get(event.getPlayer().getUniqueId()) + "'s &2BuildZone."));
					}
					else if(!Bukkit.getOfflinePlayer(ClaimManager.getEmpireAt(event.getTo()).getBuildingManager().getBuildZoneByChunk(event.getTo().getChunk()).getWhoPlaced()).getName().equals(buildzoneRegion.get(event.getPlayer().getUniqueId())))
					{
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', emp.getTag() + "&cExiting &6" + buildzoneRegion.get(event.getPlayer().getUniqueId()) + "'s &cBuildZone."));

						buildzoneRegion.remove(event.getPlayer().getUniqueId());
					}
				}
				else if(buildzoneRegion.containsKey(event.getPlayer().getUniqueId()))
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', emp.getTag() + "&cExiting &6" + buildzoneRegion.get(event.getPlayer().getUniqueId()) + "'s &cBuildZone."));

					buildzoneRegion.remove(event.getPlayer().getUniqueId());
				}
			}
			if(!region.containsKey(event.getPlayer().getUniqueId()) || !ClaimManager.getEmpireAt(event.getTo()).equals(region.get(event.getPlayer().getUniqueId())))
			{
				region.put(event.getPlayer().getUniqueId(), ClaimManager.getEmpireAt(event.getTo()));
				if(emp.getType() == EmpireType.EMPIRE)
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', emp.getTag() + "&2Entering &b" + ClaimManager.getEmpireAt(event.getTo()).getName() + " &2territory."));
				} else if(emp.getType() == EmpireType.SANCTUARY)
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bSanctuary&9] &2Entering &b" + ClaimManager.getEmpireAt(event.getTo()).getName() + " &2territory."));
				} else if(emp.getType() == EmpireType.BATTLEGROUND)
				{
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&4BattleGround&9] &2Entering &4" + ClaimManager.getEmpireAt(event.getTo()).getName() + " &2territory."));
				}
			}
		}
		else if(region.containsKey(event.getPlayer().getUniqueId()))
		{
			region.remove(event.getPlayer().getUniqueId());
			event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2Entering &7unclaimed &2territory."));
		} else if(buildzoneRegion.containsKey(event.getPlayer().getUniqueId()))
		{
			event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &cExiting &6" + buildzoneRegion.get(event.getPlayer().getUniqueId()) + "'s &cterritory."));

			buildzoneRegion.remove(event.getPlayer().getUniqueId());
		}
	}

}
