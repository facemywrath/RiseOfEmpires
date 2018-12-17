package facemywrath.riseofempires.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import facemywrath.riseofempires.main.Main;

public class CommandFinishTutorial implements CommandExecutor {

	private Main pl;

	public CommandFinishTutorial(Main pl)
	{
		this.pl = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(args.length > 0)
		{
			if(Bukkit.getOfflinePlayer(args[0]).isOnline())
			{
				Player player = Bukkit.getPlayer(args[0]);
				if(pl.getUserManager().getUser(player) != null && pl.getUserManager().getUser(player).duringTutorial())
				{
					Location loc = new Location(Bukkit.getWorld("Pophasus"), -1921.5, 77, 700.5);
					loc.setYaw(179.5f);
					player.teleport(loc);
					pl.getUserManager().getUser(player).finishTutorial();
				}
			}
		}
		else if(sender instanceof Player)
		{
			Player player = (Player) sender;
			if(pl.getUserManager().getUser(player) != null && pl.getUserManager().getUser(player).duringTutorial())
			{
				Location loc = new Location(Bukkit.getWorld("Pophasus"), -1921.5, 77, 700.5);
				loc.setYaw(179.5f);
				player.teleport(loc);
				pl.getUserManager().getUser(player).finishTutorial();
			}
		}
		return true;
	}

}
