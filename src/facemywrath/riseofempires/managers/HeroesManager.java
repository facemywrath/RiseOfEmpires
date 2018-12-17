package facemywrath.riseofempires.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;

import facemywrath.riseofempires.commands.CommandPrestige;
import facemywrath.riseofempires.commands.CommandXpGive;
import facemywrath.riseofempires.events.HeroExp;
import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.storage.User;

public class HeroesManager {

	private Main main;
	private HeroExp he = new HeroExp(this); // Hero experience gain listener 
	private CommandPrestige cp = new CommandPrestige(this);
	private CommandXpGive cxp = new CommandXpGive(this);

	public HeroesManager(Main main)
	{
		this.main= main;
	}

	public Main getMain()
	{
		return this.main;
	}

	public boolean canPrestige(Player p)
	{
		User user = main.getUserManager().getUser(p);
		if(user.getClassPoints() > 0)
			return false;
		Hero h = Heroes.getInstance().getCharacterManager().getHero(p);
		for(HeroClass hc : Heroes.getInstance().getClassManager().getClasses())
		{
			if(hc.isWildcardClass())
			{
				if(user.getPrestige() >= hc.getTier())
					if(h.getLevel(hc) != 50)
						return false;
			}
		}
		return true;
	}

	public String getRemainingClasses(Player p)
	{
		String classes = "";
		Hero h = Heroes.getInstance().getCharacterManager().getHero(p);
		for(HeroClass hc : Heroes.getInstance().getClassManager().getClasses())
		{
			if(hc.isWildcardClass())
			{
				User user = this.getMain().getUserManager().getUser(p);
				if(user.getPrestige() >= hc.getTier())
					if(h.getLevel(hc) != 50)
						classes += hc.getName() + ", ";
			}
		}
		return classes.substring(0, classes.length()-2);
	}

	public String getNewClasses(Player p, HeroClass hc)
	{
		String classes = "";
		Hero h = Heroes.getInstance().getCharacterManager().getHero(p);
		for(HeroClass hclass : Heroes.getInstance().getClassManager().getClasses())
		{
			if(this.getMain().getUserManager().getUser(p).getPrestige() < hclass.getTier())
				continue;
			if(hclass.getWeakParents().isEmpty() && hclass.getStrongParents().isEmpty())
				continue;
			if(!hclass.getWeakParents().isEmpty() && !hclass.getStrongParents().isEmpty())
			{
				boolean strongp = true;
				boolean weakp = false;
				if(hclass.getWeakParents().contains(hc))
					weakp = true;
				for(HeroClass hc2 : hclass.getStrongParents())
				{
					if(h.getLevel(hc2) != 50)
						strongp = false;
				}
				if(strongp && weakp)
				{
					classes += hclass.getName() + ", ";
				}
				continue;
			}
			if(!hclass.getWeakParents().isEmpty() && hclass.getWeakParents().contains(hc))
			{
				classes += hclass.getName() + ", ";
				continue;
			}
			if(!hclass.getStrongParents().isEmpty())
			{
				boolean flag = true;
				for(HeroClass hc2 : hclass.getStrongParents())
				{
					if(h.getLevel(hc2) != 50)
						flag = false;
				}
				if(flag)
				{
					classes += hclass.getName() + ", ";
					continue;
				}
			}

		}
		return classes.substring(0, classes.length()-2);
	}

	public String getNewClasses(int i)
	{
		String classes = "";
		for(HeroClass hc : Heroes.getInstance().getClassManager().getClasses())
		{
			if(hc.getTier() == i)
				classes += hc.getName() + ", ";
		}
		if(classes == null || classes.equals(""))
			return null;
		return classes.substring(0, classes.length()-2);
	}

	public HeroExp getExpListener()
	{
		return this.he;
	}

	public CommandPrestige getPrestigeListener()
	{
		return this.cp;
	}

	public CommandXpGive getXpCommand()
	{
		return this.cxp;
	}

}
