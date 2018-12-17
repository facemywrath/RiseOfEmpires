package facemywrath.riseofempires.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass.ExperienceType;

import facemywrath.riseofempires.main.Main;
import net.md_5.bungee.api.ChatColor;

public class CommandMap implements CommandExecutor {
	
	private Main pl;
	
	public CommandMap(Main pl)
	{
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&2RoE&9]&b Our map website link is currently: &rhttp://158.69.225.26:10029/"));
			}
		return true;
	}

}
