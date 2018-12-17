package facemywrath.riseofempires.events;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.herocraftonline.heroes.Heroes;

import facemywrath.riseofempires.main.Main;

public class PlayerJoin implements Listener {

	private Main pl;

	public PlayerJoin(Main main)
	{
		pl = main;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e)
	{
		pl.getFileManager().checkFiles();
		this.pl.getUserManager().addUser(e.getPlayer());
		File dataf = pl.getDataFolder();
		File empireFolder = new File(dataf, "empires");
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new BukkitRunnable()
		{
			public void run()
			{
				Heroes.getInstance().getCharacterManager().getHero(e.getPlayer()).setVerboseExp(true);
				Heroes.getInstance().getCharacterManager().getHero(e.getPlayer()).setVerboseMana(true);
				if(pl.getUserManager().getUser(e.getPlayer()).getFile().exists())
				{
					File file = pl.getUserManager().getUser(e.getPlayer()).getFile();
					FileConfiguration config = YamlConfiguration.loadConfiguration(file);
					if(!config.contains("TutorialDone") || !config.getBoolean("TutorialDone"))
					{
					//	e.getPlayer().teleport(new Location(Bukkit.getWorld("Pophasus"), -1870.5, 65, 634.5));
					}
				}
			}
		}, 10L);
		/*if(empireFolder.listFiles().length > 0)
			for(File f : empireFolder.listFiles())
			{
				FileConfiguration config = YamlConfiguration.loadConfiguration(f);
				if(config.contains("Members"))
				{
					for(String str : config.getConfigurationSection("Members").getKeys(false))
					{
						if(config.getString("Members." + str + ".UUID").equals(e.getPlayer().getUniqueId()))
						{
							EmpireManager.getEmpireByFile(f).getMemberManager().addMember(e.getPlayer().getUniqueId(), Rank.valueOf(config.getString("Members." + str + ".Rank")));
						}
					}
				}
			}*/
	}

}
