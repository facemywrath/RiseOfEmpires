package facemywrath.riseofempires.managers;

import java.io.File;
import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.util.FormatLore;

public class FileManager {
	
	private Main pl;
	
	private FileConfiguration schematicsFile;

	public FileManager(Main main) {
		pl = main;
		checkFiles();
	}
	
	public void checkFiles()
	{
		File dataf = pl.getDataFolder();
		if(!dataf.exists())
			dataf.mkdir();
		File empiresFolder = new File(dataf, "empires");
		if(!empiresFolder.exists())
			empiresFolder.mkdir();
		File schematicsFolder = new File(dataf, "schematics");
		if(!schematicsFolder.exists())
			schematicsFolder.mkdir();
		File userfolder = new File(dataf, "users");
		if(!userfolder.exists())
			userfolder.mkdir();
		File schematicsFile = new File(dataf, "schematics.yml");
		if(!schematicsFile.exists())
			createSchematicsFile();
		else
			this.schematicsFile = YamlConfiguration.loadConfiguration(schematicsFile);
	}
	
	public void createSchematicsFile()
	{
		File dataf = pl.getDataFolder();
		File schematicsFile = new File(dataf, "schematics.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(schematicsFile);
		try{
			schematicsFile.createNewFile();
			config.set("Town_Hall.Item.Type", "WOOD");
			config.set("Town_Hall.Item.Display", "&2Structure: Town Hall");
			config.set("Town_Hall.Item.Lore", Arrays.asList("&7&oPlace this in a flat", "&7&oarea to create your empire."));
			config.set("Home.Item.Type", "LOG");
			config.set("Home.Item.Display", "&2Structure: Home");
			config.set("Home.Item.Lore", Arrays.asList("&7&oBuilding must be placed", "&7&oin a flat 6x6 area."));
			config.set("Goldmine.Item.Type", "GOLD_ORE");
			config.set("Goldmine.Item.Display", "&2Structure: Goldmine");
			config.set("Goldmine.Item.Lore", Arrays.asList("&7&oBuilding must be placed", "&7&oin a flat 10x10 area."));
			config.set("Ironmine.Item.Type", "IRON_ORE");
			config.set("Ironmine.Item.Display", "&2Structure: Ironmine");
			config.set("Ironmine.Item.Lore", Arrays.asList("&7&oBuilding must be placed", "&7&oin a flat 10x10 area."));
			config.set("Coalmine.Item.Type", "COAL_ORE");
			config.set("Coalmine.Item.Display", "&2Structure: Coalmine");
			config.set("Coalmine.Item.Lore", Arrays.asList("&7&oBuilding must be placed", "&7&oin a flat 10x10 area."));
			config.set("Redstonemine.Item.Type", "REDSTONE_ORE");
			config.set("Redstonemine.Item.Display", "&2Structure: Redstonemine");
			config.set("Redstonemine.Item.Lore", Arrays.asList("&7&oBuilding must be placed", "&7&oin a flat 10x10 area."));
			config.set("Diamondmine.Item.Type", "DIAMOND_ORE");
			config.set("Diamondmine.Item.Display", "&2Structure: Diamondmine");
			config.set("Diamondmine.Item.Lore", Arrays.asList("&7&oBuilding must be placed", "&7&oin a flat 10x10 area."));
			config.set("BuildZone.Item.Type", "WORKBENCH"); 
			config.set("BuildZone.Item.Display", "&2Structure: BuildZone");
			config.set("BuildZone.Item.Lore", Arrays.asList("&7&oBuilding must be placed", "&7&oin a flat 10x10 area."));
			config.set("Outpost.Item.Type", "SMOOTH_BRICK");
			config.set("Outpost.Item.Display", "&2Structure: Outpost");
			config.set("Outpost.Item.Lore", Arrays.asList("&7&oBuilding must be placed", "&&7&oin a flat 10x10 area."));
			config.save(schematicsFile);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.schematicsFile = config;
	}
	
	//Getters
	public Main getMain()
	{
		return this.pl;
	}

	public FileConfiguration getSchematicsFile()
	{
		return this.schematicsFile;
	}
	
}
