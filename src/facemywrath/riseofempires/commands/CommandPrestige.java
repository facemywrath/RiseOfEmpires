package facemywrath.riseofempires.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.herocraftonline.heroes.Heroes;

import facemywrath.riseofempires.managers.HeroesManager;
import facemywrath.riseofempires.storage.User;

public class CommandPrestige implements CommandExecutor {

	private HeroesManager hm;

	public CommandPrestige(HeroesManager heroesManager) {
		this.hm = heroesManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if(hm.canPrestige(p))
		{
			hm.getMain().getServer().dispatchCommand(p, "hero reset");
			hm.getMain().getServer().dispatchCommand(p, "hero confirm");
			User user = hm.getMain().getUserManager().getUser(p);
			user.setPrestige(user.getPrestige()+1);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &6You have reached &bPrestige " + user.getPrestige()));
			if(this.hm.getNewClasses(user.getPrestige()) != null)
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2You have now gained access to &b" + this.hm.getNewClasses(user.getPrestige())));
			if(user.getPrestige() >= 2)
			{
				hm.getMain().getServer().getScheduler().scheduleSyncDelayedTask(hm.getMain(), new BukkitRunnable()
				{
					public void run()
					{
						user.addClassPoints(1);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &6Because of your prestige you instantly master Warrior and Mage."));
						Heroes.getInstance().getCharacterManager().getHero(p).setExperience(Heroes.getInstance().getClassManager().getClass("Warrior"), 200000);
						Heroes.getInstance().getCharacterManager().getHero(p).setExperience(Heroes.getInstance().getClassManager().getClass("Mage"), 200000);
					}
				}, 5L);
			}
			else
			{
				user.addClassPoints(3);
			}
		}
		else
		{
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &4You can't prestige until you master: &b" + hm.getRemainingClasses(p)));
		}
		return true;
	}

}
