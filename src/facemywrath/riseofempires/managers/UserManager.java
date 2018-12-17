package facemywrath.riseofempires.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.storage.User;

public class UserManager {

	private Main main;
	private List<User> users = new ArrayList<>();

	public UserManager(Main main)
	{
		this.main = main;
		main.getFileManager().checkFiles();
		File userFile = new File(main.getDataFolder(), "users");
		for(File f : userFile.listFiles())
		{
			String struuid = f.getName().substring(0, f.getName().length()-4);
			FileConfiguration config = YamlConfiguration.loadConfiguration(f);
			UUID uuid = UUID.fromString(struuid);
			if(!config.contains("Prestige"))
			{
				addUser(Bukkit.getOfflinePlayer(uuid));
				try{
					config.save(f);
				}catch(Exception e){}
			}
			if(!config.contains("ClassPoints"))
			{
				config.set("ClassPoints", 0);
			}
			if(this.users.isEmpty() || !this.users.contains(new User(this, Bukkit.getOfflinePlayer(uuid), config.getInt("Prestige"), config.getInt("ClassPoints"))))
				this.users.add(new User(this, Bukkit.getOfflinePlayer(uuid), config.getInt("Prestige"), config.getInt("ClassPoints")));
		}
		for(Player p : Bukkit.getOnlinePlayers())
		{
			addUser(p);
		}
	}
	
	public File getFile(User user)
	{
		return new File(this.getMain().getDataFolder() + File.separator + "users", user.getOfflinePlayer().getUniqueId().toString() + ".yml");
	}

	public void addUser(OfflinePlayer offlinePlayer)
	{
		main.getFileManager().checkFiles();
		File userFolder = new File(main.getDataFolder(), "users");
		File userFile = new File(userFolder, offlinePlayer.getUniqueId().toString() + ".yml");
		if(!userFile.exists())
		{
			try{
				userFile.createNewFile();
				FileConfiguration config = YamlConfiguration.loadConfiguration(userFile);
				config.set("Name", offlinePlayer.getName());
				config.set("Prestige", 0);
				config.set("TutorialDone", false);
				config.save(userFile);
			}catch(Exception e){}
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(userFile);
		if(this.users.isEmpty() || !this.users.contains(new User(this, Bukkit.getOfflinePlayer(offlinePlayer.getUniqueId()), config.getInt("Prestige"), config.getInt("ClassPoints"))))
			this.users.add(new User(this, Bukkit.getOfflinePlayer(offlinePlayer.getUniqueId()), config.getInt("Prestige"), config.getInt("ClassPoints")));
	}

	public User getUser(Player player)
	{
		for(User user : users)
		{
			if(user.getOfflinePlayer().equals((OfflinePlayer) player))
				return user;
		}
		return null;
	}

	public Main getMain()
	{
		return main;
	}

}
