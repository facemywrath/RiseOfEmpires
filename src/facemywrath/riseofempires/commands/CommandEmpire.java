package facemywrath.riseofempires.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.util.Direction;

import facemywrath.riseofempires.empires.Empire;
import facemywrath.riseofempires.empires.EmpireManager;
import facemywrath.riseofempires.empires.EmpireType;
import facemywrath.riseofempires.empires.buildings.BuildingManager;
import facemywrath.riseofempires.empires.claims.ClaimManager;
import facemywrath.riseofempires.empires.members.Member;
import facemywrath.riseofempires.empires.members.MemberManager;
import facemywrath.riseofempires.enums.Rank;
import facemywrath.riseofempires.main.Main;
import facemywrath.riseofempires.util.AlphaNumStr;
import facemywrath.riseofempires.util.GetDirection;

public class CommandEmpire implements CommandExecutor {

	private Main pl;
	private List<UUID> pchats = new ArrayList<>();
	private List<UUID> overrides = new ArrayList<>();
	private HashMap<UUID, Empire> invites = new HashMap<>();

	public CommandEmpire(Main main) {
		pl = main;	
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if(!(sender instanceof Player))
		{
			if(args.length == 0)
			{
				sender.sendMessage("Only players can use this command");
			}
			else if(args.length == 1)
			{
				pl.addExperienceModifier();
			}
			else if(args.length == 2)
			{
				if(args[0].equalsIgnoreCase("give"))
				{
					if(Bukkit.getOfflinePlayer(args[1]).isOnline())
					{
						Bukkit.getPlayer(args[1]).getInventory().addItem(BuildingManager.getBuildingItem("Town_Hall"));
					}
				}
			}
			return true;
		}
		Player p = (Player) sender;
		if(args.length > 0)
		{
			if(args[0].equalsIgnoreCase("bank"))
				if(MemberManager.getMember(p) != null)
					bankCommand(sender, cmd, lbl, args);
		}
		switch(args.length)
		{
		case 0:
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[RoE]&b Type '/empire help' for a full list of commands."));
			break;
		case 1:
			if(args[0].equalsIgnoreCase("list"))
			{
				List<Empire> popularity = new ArrayList<>();
				for(Empire emp1 : pl.getEmpireManager().getEmpires())
				{
					int i = -1;
					Empire temp = null;
					for(Empire emp : pl.getEmpireManager().getEmpires())
					{
						if(emp.getType() == EmpireType.EMPIRE)
						{
							if(!emp.equals(emp1) && !popularity.contains(emp))
							{
								if(emp.getMemberManager().getOnlineMembers().size() > i)
								{
									i = emp.getMemberManager().getOnlineMembers().size();
									temp = emp;
								}
							}
						}
					}
					if(temp != null)
					{
						popularity.add(temp);
					}
				}
				if(popularity.size() < 8)
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9]&7Empire List: Page 1/1"));
					for(Empire emp : popularity)
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEmpire " + emp.getName() + " &7: " + "&aPlayers Online: " + emp.getMemberManager().getOnlineMembers().size() + "/" + emp.getMemberManager().getMembers().size() + " &7: " + "&aLevel: " + emp.getBuildingManager().getTownHall().getLevel()));
					}
				}
				else
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9]&7Empire List: Page 1/" + (1+(int) popularity.size()/7)));	
					for(int k = 0; k < 7; k++)
					{
						Empire emp = popularity.get(k);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEmpire " + emp.getName() + " &7: " + "&aPlayers Online: " + emp.getMemberManager().getOnlineMembers().size() + "/" + emp.getMemberManager().getMembers().size() + " &7: " + "&aLevel: " + emp.getBuildingManager().getTownHall().getLevel()));
					}
				}
			}
			else if(args[0].equalsIgnoreCase("home"))
			{
				if(MemberManager.getMember(p) != null)
				{
					p.teleport(MemberManager.getMember(p).getEmpire().getBuildingManager().getTownHall().getLocation());
				}
			}
			else if(args[0].equalsIgnoreCase("map"))
			{
				if(p.hasPermission("roe.map"))
				{
					int chunkx = p.getLocation().getChunk().getX();
					int chunkz = p.getLocation().getChunk().getZ();
					List<String> empires = new ArrayList<>();
					String innerempires = "";
					Direction dir = GetDirection.getDirection(p.getLocation().getYaw());
					List<Empire> empireList = new ArrayList<>();
					for(int j = -5; j < 6; j++)
					{
						for(int i = -5; i < 6; i++)
						{
							Chunk chunk = p.getWorld().getChunkAt(chunkx+i, chunkz+j);
							switch(dir)
							{
							case SOUTH:
								chunk = p.getWorld().getChunkAt(chunkx-i, chunkz-j);
								break;
							case EAST:
								chunk = p.getWorld().getChunkAt(chunkx-j, chunkz-i);
								break;
							case WEST:
								chunk = p.getWorld().getChunkAt(chunkx+j, chunkz+i);
								break;
							}
							if(ClaimManager.getEmpireAt(chunk) == null)
							{
								if(i==0 && j==0)
									innerempires += "&1o";
								else
									innerempires += "&7o";
							}
							else
							{
								Empire emp = ClaimManager.getEmpireAt(chunk);
								if(!empireList.contains(emp))
									empireList.add(emp);
								String replacekey = (empireList.indexOf(emp)+1) + "";
								if(empireList.indexOf(emp)>8)
								{
									int x = empireList.indexOf(emp)-9;
									replacekey = Character.getName(65+x);
								}
								if(i == 0 && j==0)
								{
									innerempires += "&1" + (replacekey);
								}
								else
								{
									switch(emp.getType())
									{
									case EMPIRE:
										innerempires += "&2" + (replacekey);
										break;
									case SANCTUARY:
										innerempires += "&b" + (replacekey);
										break;
									case BATTLEGROUND:
										innerempires += "&4" + (replacekey);
									}
								}
							}
						}
						empires.add(innerempires);
						innerempires = "";
					}
					for(String s : empires)
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8Map Key: &bUp is " + StringUtils.capitalize(dir.toString().toLowerCase())));
					for(int i = 0; i < empireList.size(); i++)
					{
						Empire emp = empireList.get(i);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2" + (i+1) + ": &b" + StringUtils.capitalize(emp.getType().toString().toLowerCase()) + " " + emp.getName()));
					}
				}
			}
			else if(args[0].equalsIgnoreCase("rl") && p.isOp())
			{
				//this.pl.reloadEM();
			}
			else if(args[0].equalsIgnoreCase("help"))
			{
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&o[RoE] Help: Page 1/2"));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e members &7Lists your empires members."));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e leave &7Leave your empire. &8Note: If you are emperor you must make someone else emperor or disband before you can leave."));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e rank (get/set) (player-name) &7Get or set the rank of a member of your empire. &8Note: Only the emperor can set a person's rank."));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e chat &7Toggle empire-only chat."));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&o[RoE] &9Type /e help 2 to view the second page."));
			}
			else if(args[0].equalsIgnoreCase("open"))
			{
				if(MemberManager.getMember(p) != null)
				{
					Member member = MemberManager.getMember(p);
					if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL)
					{
						member.getEmpire().toggleOpen(true);
						member.getEmpire().broadcastToMembers("&2Empire is now set to &bopen. &2Everyone &bCitizen &2rank or above may now invite others.");
					}
					else
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4Only &bEmperor &2and &bGenerals &2can open or close the empire."));
					}
				}
			}
			else if(args[0].equalsIgnoreCase("close"))
			{
				if(MemberManager.getMember(p) != null)
				{
					Member member = MemberManager.getMember(p);
					if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL)
					{
						member.getEmpire().toggleOpen(false);
						member.getEmpire().broadcastToMembers("&2Empire is now set to &bclosed. &2Only &bEmperor &2and &bGenerals &2can invite others.");
					}
					else
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4Only &bEmperor &2and &bGenerals &2can open or close the empire."));
					}
				}

			}
			else if(args[0].equalsIgnoreCase("leave"))
			{
				if(MemberManager.getMember(p) != null)
				{
					if(MemberManager.getMember(p).getRank() != Rank.EMPEROR)
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', MemberManager.getMember(p).getEmpire().getTag() + "&5Are you sure you want to leave? Type &7'/e leave confirm' &5to confirm."));
					else
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', MemberManager.getMember(p).getEmpire().getTag() + "&4Must relinquish ownership or disband the empire before you can leave."));
				}
			}
			else if(args[0].equalsIgnoreCase("chat"))
			{
				if(pchats.contains(p.getUniqueId()) && MemberManager.getMember(p) != null)
				{
					pchats.remove(p.getUniqueId());
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', MemberManager.getMember(p).getEmpire().getTag() + "&6Switching to global chat."));
				}
				else if(!pchats.contains(p.getUniqueId()) && MemberManager.getMember(p) != null)
				{
					pchats.add(p.getUniqueId());
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', MemberManager.getMember(p).getEmpire().getTag() + "&2Switching to empire chat."));
				}
			}
			else if(args[0].equalsIgnoreCase("accept") && invites.containsKey(p.getUniqueId()))
			{
				Empire empire = invites.get(p.getUniqueId());
				empire.getMemberManager().addMember( p.getUniqueId(), Rank.CITIZEN);
				invites.get(p.getUniqueId()).broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&2" + p.getName() + " has joined the empire!"));
				invites.remove(p.getUniqueId());
			}
			else if(args[0].equalsIgnoreCase("members") && MemberManager.getMember(p) != null)
			{
				String emp = "&7Emperor: &b";
				String gen = "&7Generals: &b";
				String bui = "&7Builders: &b";
				String cit = "&7Citizens: &b";
				String out = "&7Outcasts: &b";
				for(Member member : MemberManager.getMember(p).getEmpire().getMemberManager().getMembers())
				{
					OfflinePlayer pl = Bukkit.getOfflinePlayer(member.getUUID());
					String name = pl.getName() + ", ";
					if(member.getRank() == Rank.EMPEROR)
					{
						if(pl.isOnline())
							emp+= "&b";
						else
							emp+= "&7";
						emp += name;
					}
					else if(member.getRank() == Rank.GENERAL)
					{
						if(pl.isOnline())
							gen+= "&b";
						else
							gen+= "&7";
						gen += name;
					}
					else if(member.getRank() == Rank.BUILDER)
					{
						if(pl.isOnline())
							bui+= "&b";
						else
							bui+= "&7";
						bui += name;
					}
					else if(member.getRank() == Rank.CITIZEN)
					{
						if(pl.isOnline())
							cit+= "&b";
						else
							cit+= "&7";
						cit += name;
					}
					else if(member.getRank() == Rank.OUTCAST)
					{
						if(pl.isOnline())
							out+= "&b";
						else
							out+= "&7";
						out += name;
					}
				}
				emp = emp.substring(0, emp.length()-2);
				gen = gen.substring(0, gen.length()-2);
				bui = bui.substring(0, bui.length()-2);
				cit = cit.substring(0, cit.length()-2);
				out = out.substring(0, out.length()-2);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&o---Members---"));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', emp));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', gen));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', bui));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', cit));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', out));
			}
			else if(args[0].equalsIgnoreCase("rank"))
			{
				if(MemberManager.getMember(p) != null)
				{
					Member member = MemberManager.getMember(p);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&2You are a(n) &b" + StringUtils.capitalise(member.getRank().toString().toLowerCase()) + " &2in &b" + member.getEmpire().getName()));
				}
			}
			break;
		case 2:
			if(args[0].equalsIgnoreCase("admin"))
			{
				if(args[1].equalsIgnoreCase("override") && p.hasPermission("roe.admin.override"))
				{
					if(overrides.isEmpty() || !overrides.contains(p.getUniqueId()))
					{
						overrides.add(p.getUniqueId());
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2Enabling Admin Override."));
					}
					else if(overrides.contains(p.getUniqueId()))
					{
						overrides.remove(p.getUniqueId());
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2Disabling Admin Override."));
					}
				}
			}
			else if(args[0].equalsIgnoreCase("list"))
			{
				int page = -1;
				try{
					page = Integer.parseInt(args[1]);
				}catch(Exception e){}
				if(page > -1)
				{
					List<Empire> popularity = new ArrayList<>();
					for(Empire emp1 : pl.getEmpireManager().getEmpires())
					{
						int i = -1;
						Empire temp = null;
						for(Empire emp : pl.getEmpireManager().getEmpires())
						{
							if(emp.getType() == EmpireType.EMPIRE)
							{
								if(!emp.equals(emp1) && !popularity.contains(emp))
								{
									if(emp.getMemberManager().getOnlineMembers().size() > i)
									{
										i = emp.getMemberManager().getOnlineMembers().size();
										temp = emp;
									}
								}
							}
						}
						if(temp != null)
						{
							popularity.add(temp);
						}
					}
					int i = 0;
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9]&7Empire List: Page " + page + "/" + (1+(int) popularity.size()/7)));
					int l = page*7;
					if(popularity.size() < (page*7))
						l = popularity.size();
					for(int k = (page-1)*7; k < l; k++)
					{
						Empire emp = popularity.get(k);
						if(i == 0)
							i = 1;
						else
							i = 0;
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEmpire " + emp.getName() + " &7: " + "&aPlayers Online: " + emp.getMemberManager().getOnlineMembers().size() + "/" + emp.getMemberManager().getMembers().size() + " &7: " + "&aLevel: " + emp.getBuildingManager().getTownHall().getLevel()));
					}
				}
			}
			else if(args[0].equalsIgnoreCase("help"))
			{
				if(args[1].equals("2"))
				{
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&o[RoE] Help: Page 2/2"));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e bank &7View list of empire bank commands."));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e map &7View map of empires around you."));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e invite &7Invite a player to your empire."));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e accept &7Accept the last empire invitation."));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e tag (tag) &7Allows the emperor to set the empire's tag."));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2/e kick (playername) &7Allows the emperor to kick a player from the empire."));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&o[RoE] &9Type /e help 1 to view the first page."));
				}
			}
			else if(args[0].equalsIgnoreCase("give") && p.isOp())
			{
				if(Bukkit.getOfflinePlayer(args[1]).isOnline())
				{
					Player receiver = Bukkit.getPlayer(args[1]);
					receiver.getInventory().addItem(BuildingManager.getBuildingItem("Town_Hall"));
				}
			}
			else if(args[0].equalsIgnoreCase("leave"))
			{
				if(MemberManager.getMember(p) != null)
				{
					if(args[1].equalsIgnoreCase("confirm"))
					{
						if(MemberManager.getMember(p).getRank() != Rank.EMPEROR)
							MemberManager.getMember(p).getMemberManager().playerLeaveEmpire(MemberManager.getMember(p));
						else
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', MemberManager.getMember(p).getEmpire().getTag() + "&4Must relinquish ownership or disband the empire before you can leave."));

					}
				}
			}
			else if(args[0].equalsIgnoreCase("invite") && Bukkit.getOfflinePlayer(args[1]).isOnline() && MemberManager.getMember(p) != null)
			{
				Member member = MemberManager.getMember(p);
				if(member.getEmpire().isOpen() && member.getRank() != Rank.OUTCAST)
				{
					Player player = Bukkit.getPlayer(args[1]);
					if(invites.containsKey(player))
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player already has a pending empire invite"));
					}
					else if(MemberManager.getMember(player) != null)
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player is already in an empire."));
					}else{
						invites.put(player.getUniqueId(), MemberManager.getMember(p).getEmpire());
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2" + p.getName() + " has invited you to the empire " + MemberManager.getMember(p).getEmpire().getName()));
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "Type &7/Empire accept &2to accept."));
						member.getEmpire().broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&2" + player.getName() + " has been invited"));
					}
				}
				else if(!member.getEmpire().isOpen())
				{
					if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL)
					{

						Player player = Bukkit.getPlayer(args[1]);
						if(invites.containsKey(player))
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player already has a pending empire invite"));
						}
						else if(MemberManager.getMember(player) != null)
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player is already in an empire."));
						}else{
							invites.put(player.getUniqueId(), MemberManager.getMember(p).getEmpire());
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2" + p.getName() + " has invited you to the empire " + MemberManager.getMember(p).getEmpire().getName()));
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "Type &7/Empire accept &2to accept."));
							member.getEmpire().broadcastToMembers(ChatColor.translateAlternateColorCodes('&', "&2" + player.getName() + " has been invited"));
						}

					}
					else
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4Empire is closed. Only &bEmperor &2and &bGenerals &2 may invite others."));
					}
				}
			}
			if(args[0].equalsIgnoreCase("tag") && MemberManager.getMember(p) != null)
			{
				Member member = MemberManager.getMember(p);
				Empire emp = member.getEmpire();
				if(member.getRank() == Rank.EMPEROR)
				{
					if(isAlphaNumeric(args[1]))
					{
						if(!EmpireManager.isTagUsed(args[1]))
						{
							if(args[1].length() < 5)
								emp.setTag(args[1]);
							else
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Maximum of four character tag limit."));
						}
						else
						{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Another empire is using that tag."));
						}
					}
					else
					{
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Tag must be Alpha-Numeric."));
					}
				}
			}
			else if(args[0].equalsIgnoreCase("kick") && MemberManager.getMember(p) != null)
			{
				Member member = MemberManager.getMember(p);
				Empire emp = member.getEmpire();
				if(member.getRank() == Rank.EMPEROR || member.getRank() == Rank.GENERAL)
				{
					if(MemberManager.getMember(args[1]) != null)
					{
						Member mem2 = MemberManager.getMember(args[1]);
						if(!mem2.equals(member) && mem2.getRank() != member.getRank() && MemberManager.getMember(args[1]).getEmpire().equals(emp))
						{
							emp.getMemberManager().kickMember(mem2);
						}
					}
				}
			}
			else if(args[0].equalsIgnoreCase("rank") && MemberManager.getMember(p) != null)
			{
				Member member = MemberManager.getMember(p);
				if(args[1].equalsIgnoreCase("get"))
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4Proper syntax: /e rank get (name)"));
				else if(args[1].equalsIgnoreCase("set"))
					if(member.getRank() == Rank.EMPEROR)
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4Proper Syntax: /e rank set (name) (rank)"));
					else
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4You do not have permission to set players' ranks."));
				else
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[RoE]&b Type '/empire help' for a full list of commands."));			}
			break;
		case 3:
			if(args[0].equalsIgnoreCase("rank") && MemberManager.getMember(p) != null)
			{
				if(args[1].equalsIgnoreCase("get"))
				{
					if(MemberManager.getMember(args[2]) != null)
					{
						Member member = MemberManager.getMember(p);
						Member mem2 = MemberManager.getMember(args[2]);
						if(member.getEmpire().equals(mem2.getEmpire()))
						{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&b" + Bukkit.getOfflinePlayer(mem2.getUUID()).getName() + " &2is a(n) &b" + StringUtils.capitalise(member.getRank().toString().toLowerCase()) + " &2in &b" + member.getEmpire().getName()));
						}
					}
				}
				else if(args[1].equalsIgnoreCase("set"))
				{
					if(MemberManager.getMember(args[2]) != null)
					{
						Member member = MemberManager.getMember(p);
						if(member.getRank() == Rank.EMPEROR)
						{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4Must specify a rank. Possible ranks are &bOutcast, Citizen, Builder, General, and Emperor."));
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&7Note: When setting another player to emperor, you get demoted to citizen."));
						}
					}
				}
			}
			break;
		case 4:
			if(args[0].equalsIgnoreCase("rank") && MemberManager.getMember(p) != null)
			{
				if(args[1].equalsIgnoreCase("get"))
				{
					if(MemberManager.getMember(args[2]) != null)
					{
						Member member = MemberManager.getMember(p);
						Member mem2 = MemberManager.getMember(args[2]);
						if(member.getEmpire().equals(mem2.getEmpire()))
						{
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&b" + Bukkit.getOfflinePlayer(mem2.getUUID()).getName() + " &2is a(n) &b" + StringUtils.capitalise(member.getRank().toString().toLowerCase()) + " &2in &b" + member.getEmpire().getName()));
						}
					}
				}
				else if(args[1].equalsIgnoreCase("set"))
				{
					Member member = MemberManager.getMember(p);
					if(member.getRank() == Rank.EMPEROR)
					{
						if(MemberManager.getMember(Bukkit.getOfflinePlayer(args[2]).getUniqueId()) != null)
						{
							Member member2 = MemberManager.getMember(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
							if(member2.equals(member))
							{
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', member.getEmpire().getTag() + "&4Can not change your own rank."));
							}
							if(args[3].equalsIgnoreCase("outcast") || args[3].equalsIgnoreCase("CITIZEN") || args[3].equalsIgnoreCase("builder") || args[3].equalsIgnoreCase("general") || args[3].equalsIgnoreCase("emperor"))
							{
								if(args[3].equalsIgnoreCase("outcast"))
									member2.setRank(Rank.OUTCAST);
								if(args[3].equalsIgnoreCase("CITIZEN"))
									member2.setRank(Rank.CITIZEN);
								if(args[3].equalsIgnoreCase("Builder"))
									member2.setRank(Rank.BUILDER);
								if(args[3].equalsIgnoreCase("general"))
									member2.setRank(Rank.GENERAL);
								if(args[3].equalsIgnoreCase("emperor"))
								{
									member2.setRank(Rank.EMPEROR);
									member.setRank(Rank.CITIZEN);
								}
							}
						}
					}
				}
			}
			else if(args[0].equalsIgnoreCase("admin") && p.isOp())
			{
				if(args[1].equalsIgnoreCase("create"))
				{
					if(args[2].equalsIgnoreCase("Sanctuary") || args[2].equalsIgnoreCase("Battleground"))
					{
						this.pl.getEmpireManager().getEmpires().add(new Empire(this.pl.getEmpireManager(), p.getLocation(), args[3], args[2].toUpperCase()));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2" + StringUtils.capitalize(args[2].toLowerCase()) + " " + args[3] + " created."));

					}
				} else if (args[1].equalsIgnoreCase("size"))
				{
					if(EmpireManager.doesEmpireExist(args[2]))
					{
						int i = -1;
						try{
							i = Integer.parseInt(args[3]);
						}
						catch(Exception e){}
						if(i != -1)
						{
							for(Empire emp : pl.getEmpireManager().getEmpires())
							{
								if(emp.getType() != EmpireType.EMPIRE)
								{
									if(emp.getName().equalsIgnoreCase(args[2]))
									{
										emp.setClaimSize(i);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9[&bRoE&9] &2" + StringUtils.capitalize(emp.getType().toString().toLowerCase()) + " " + emp.getName() + " size set to " + i + "."));
										return true;
									}
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	public void bankCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		Member member = MemberManager.getMember((Player) sender);
		switch(args.length)
		{
		case 1:
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&o---Available Bank Commands---"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/empire bank balance;b"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/empire bank deposit;d (amount)"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/empire bank withdraw;w (amount)"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&o-----------------------------"));
			break;
		case 2:
			if(args[1].equalsIgnoreCase("balance") || args[1].equals("b"))
			{
				sender.sendMessage(member.getEmpire().getName() + " Gold: " + member.getEmpire().getGold());
			}
			else if(args[1].equals("deposit") || args[1].equalsIgnoreCase("d") || args[1].equals("withdraw") || args[1].equalsIgnoreCase("w"))
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Error: Must insert an amount"));
			}
			break;
		case 3:
			if(args[1].equalsIgnoreCase("deposit") || args[1].equalsIgnoreCase("d"))
			{
				double amount = -1;
				try{
					amount = Double.parseDouble(args[2]);
					if(pl.getEconomy().getBalance(sender.getName()) > amount)
					{
						pl.getEconomy().withdrawPlayer(sender.getName(), amount);
						member.getEmpire().addMoney(amount);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + amount + " Gold deposited. New balance: " + member.getEmpire().getGold()));
					}
				}catch(Exception e)
				{
					sender.sendMessage(ChatColor.DARK_RED + "Must input a numerical amount.");
				}
			}
		}
	}

	public boolean isAlphaNumeric(String str)
	{ 
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean containsPlayer(Player p)
	{
		if(pchats.contains(p.getUniqueId()))
			return true;
		return false;
	}

	public List<UUID> getPChats()
	{
		return this.pchats;
	}
	
	public List<UUID> getOverrides()
	{
		return this.overrides;
	}

}
