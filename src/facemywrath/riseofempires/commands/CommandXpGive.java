package facemywrath.riseofempires.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass.ExperienceType;

import facemywrath.riseofempires.managers.HeroesManager;

public class CommandXpGive implements CommandExecutor {
	
	private HeroesManager hm;
	
	public CommandXpGive(HeroesManager hm)
	{
		this.hm = hm;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender.hasPermission("roe.xpgive"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
				Hero h = Heroes.getInstance().getCharacterManager().getHero(p);
				h.gainExp(50, ExperienceType.QUESTING, p.getLocation());
			}
		}
		return true;
	}

}
