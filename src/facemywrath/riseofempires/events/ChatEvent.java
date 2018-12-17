package facemywrath.riseofempires.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireManager;
import facemywrath.riseofempires.empires.buildings.BuildingManager;
import facemywrath.riseofempires.empires.members.Member;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.enums.Rank;
import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.util.AlphaNumStr;

public class ChatEvent implements Listener {

	private Main pl;

	public ChatEvent(Main pl)
	{
		this.pl = pl;	
	}

	@EventHandler
	public void playerChat(PlayerChatEvent event)
	{
		Player p = event.getPlayer();
		Hero h = Heroes.getInstance().getCharacterManager().getHero(p);
		if(MemberManager.getMember(p) != null)
		{ // If the player belongs to an empire
			Member member = MemberManager.getMember(p); // Get their member object
			Empire emp = member.getEmpire(); // Get their empire
			String classname = h.getHeroClass().getName();
			classname = classname.substring(0, 3);
			classname = classname.toUpperCase();
			String profname = h.getSecondClass().getName();
			profname = profname.substring(0, 3);
			profname = profname.toUpperCase();
			
			if(emp.getTag() != null)
			{ // If the empire has a set tag
				event.setFormat(ChatColor.translateAlternateColorCodes('&', emp.getTag() + "<%1$s &9[&7" + classname + "/" + profname + "&9]&r> %2$s")); // Change the chat format
			}
			if(pl.getCommandObj().containsPlayer(event.getPlayer()))
			{
				member.getEmpire().broadcastToMembers("&r<&2" + p.getName() + "&r> &7" + event.getMessage());
				event.setCancelled(true);
				event.setMessage("");
				event.setFormat("");
			}
			return;
		}
		
		if(pl.getBlockPlaceListener().containsPlayer(event.getPlayer().getUniqueId()))
		{ // If they're trying to create an empire
			if(event.getMessage().equals("cancel"))
			{ // And the message is cancel, then stop them from placing an empire and return their item
				p.getInventory().addItem(BuildingManager.getBuildingItem("Town_Hall")); 
				pl.getBlockPlaceListener().getNamingPlayers().remove(event.getPlayer().getUniqueId());
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &4Cancelling empire creation"));
				return;
			}
			if(AlphaNumStr.isAlphaNumeric(event.getMessage()))
			{ // If the empire name is alphanumeric and the name isn't taken, create and remove them from the list.
				if(!EmpireManager.doesEmpireExist(event.getMessage()))
				{
				pl.getEmpireManager().getEmpires().add(new Empire(pl.getEmpireManager(), pl.getBlockPlaceListener().getNamingPlayers().get(p.getUniqueId()), new Member(p.getUniqueId(), Rank.EMPEROR), event.getMessage()));
				pl.getBlockPlaceListener().getNamingPlayers().remove(event.getPlayer().getUniqueId());
				event.setCancelled(true);
				event.setMessage("");
				event.setFormat("");
				}
				else
				{ // Else, cancel empire creation.	
					event.setMessage("");
					event.setFormat("");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &4An empire by that name already exists."));
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lWhat would you like to name your empire? (Type 'cancel' to cancel empire creation)"));
				}
			}
			else
			{ // Else, cancel empire creation.	
				event.setMessage("");
				event.setFormat("");
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &4Name must be Alpha-Numeric."));
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lWhat would you like to name your empire? (Type 'cancel' to cancel empire creation)"));
			}
		}
		else
		{
			String classname = h.getHeroClass().getName();
			classname = classname.substring(0, 3);
			classname = classname.toUpperCase();
			String profname = h.getSecondClass().getName();
			profname = profname.substring(0, 3);
			profname = profname.toUpperCase();
			event.setFormat(ChatColor.translateAlternateColorCodes('&', "&9[&7SOLO&9] &r<%1$s &9[&7" + classname + "/" + profname + "&9]&r> %2$s")); // Change the chat format
		}
	}

	//Getters

	public Main getMain()
	{
		return this.pl;
	}

}
