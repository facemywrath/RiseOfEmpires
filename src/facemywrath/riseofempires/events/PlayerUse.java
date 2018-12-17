package facemywrath.riseofempires.events;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireType;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.main.Main;
import net.md_5.bungee.api.ChatColor;
import think.rpgitems.item.ItemManager;

public class PlayerUse implements Listener {

	private Main pl;

	public PlayerUse(Main main)
	{
		pl = main;
	}

	@EventHandler
	public void playerUse(PlayerInteractEvent event)
	{
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			ItemStack item = event.getItem();
			if(ItemManager.toRPGItem(item) != null)
			{
				if(ItemManager.toRPGItem(item).getDisplay().contains("XP Bomb"))
				{
					int amount = item.getAmount();
					item.setAmount(amount-1);
					if(this.pl.getExperienceModifier() < 30)
					{
						doSong(event.getPlayer());
						this.pl.addExperienceModifier();
					}
					else
					{
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &4Exp Modifier is maxed out."));
						this.pl.getServer().dispatchCommand(this.pl.getServer().getConsoleSender(), "rpgitem xpbomb give " + event.getPlayer().getName());
					}
				}
			}
		}
		if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(!ClaimManager.checkClaim(event.getClickedBlock().getLocation(), event.getPlayer()))
			{ // Check if the block location belongs to the players empire
				if(ClaimManager.getEmpireAt(event.getClickedBlock().getLocation()) != null)
				{
					Empire emp = ClaimManager.getEmpireAt(event.getClickedBlock().getLocation());
					if(emp.getType() == EmpireType.EMPIRE)
					{
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', ClaimManager.getEmpireAt(event.getClickedBlock().getLocation()).getTag() + "&4That doesn't belong to you!"));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public void doSong(Player p)
	{
		for(int i = 1; i < 40; i++)
		{
			final int x = i;
			pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new BukkitRunnable()
			{
				public void run()
				{
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, (float) (0.6f + (x*0.025)));
				}
			}, i*1L);
		}
	}

}
