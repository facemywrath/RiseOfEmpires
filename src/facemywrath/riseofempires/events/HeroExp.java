package facemywrath.riseofempires.events;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.herocraftonline.heroes.api.events.ExperienceChangeEvent;

import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.managers.HeroesManager;
import facemywrath.riseofempires.storage.User;

public class HeroExp implements Listener {

	private HeroesManager hm;

	public HeroExp(HeroesManager main)
	{
		hm = main;
	}

	@EventHandler
	public void HeroGainExp(ExperienceChangeEvent event)
	{
		Player p = event.getHero().getPlayer();
		if(event.getExpChange() > 0 && event.getHero().getExperience(event.getHeroClass()) <= 500000)
		{
			event.setExpGain(event.getExpChange() * getMultiplier(p));
			if(MemberManager.getMember(p) != null)
				MemberManager.getMember(p).getEmpire().addExp(event.getExpChange());
		}
		else if(event.getHero().getExperience(event.getHeroClass()) >= 500000)
		{
			Plugin pl = hm.getMain();
			pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new BukkitRunnable()
			{
				public void run()
				{
					if(hm.canPrestige(p))
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2Congratulations! You can now prestige to the next rank!"));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2Type &7/prestige &2to prestige. &8Note: Ranking up will reset all of your levels back to 1."));
					}
					else if(hm.getNewClasses(p, event.getHeroClass()).length() > 1)
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2You've now gained access to &b" + hm.getNewClasses(p, event.getHeroClass()) ));
					}
				}
			}, 4L);
		}
	}

	private double getMultiplier(Player p)
	{
		double multiplier = Math.pow(1.2, hm.getMain().getExperienceModifier());
		User user = this.hm.getMain().getUserManager().getUser(p);
		Date date = new Date();
		if(date.getDay() == 0 || date.getDay() == 5 || date.getDay() == 6)
			multiplier+=1;
		multiplier += (0.1 * user.getPrestige());
		return multiplier;
	}

}
