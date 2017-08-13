package fr.dariusmtn.caulcrafting.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CaulCrafting;
import fr.dariusmtn.caulcrafting.CraftArray;
import mkremins.fanciful.FancyMessage;

public class CaulCraftingCommandExecutor implements CommandExecutor {

	private CaulCrafting plugin;
    public CaulCraftingCommandExecutor(CaulCrafting instance) {
          this.plugin = instance; 
    }
    
    @SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(cmd.getName().equalsIgnoreCase("caulcrafting")){
			//Joueur réel
			if(sender instanceof Player){
				Player player = (Player)sender;
				//Language selection
				if(plugin.lang.getExactLanguage().equalsIgnoreCase("default")) {
					if(player.isOp()){
						player.sendMessage("§d§l➤ Thank you for downloading §b§lCaulCrafting§d§l !");
						player.sendMessage("§eFor a better experience, please §bselect§e by clicking with mouse your §bcorresponding §llanguage§e :");
						HashMap<String, String> langs = plugin.languagesAvailable;
						for(String loc : langs.keySet()) {
							new FancyMessage(" §e•§2 " + langs.get(loc))
							.tooltip("§b" + plugin.lang.getTranslation("welcome_language_pickup_tooltip", loc))
							.command("/caulcraftingconfig setlang " + loc)
							.send(player);
						}
						return true;
					}
					player.sendMessage("§cThe plugin need to be initialized by an OP player");
					return false;
				} else {
					//Sous commandes
					if(args.length > 0){
						if(args[0].equalsIgnoreCase("create")){
							if(player.hasPermission("caulcrafting.admin.create")){
								//CRÉATION DE CRAFT
								if(args.length > 1){
									if(args.length == 3){
										if(!plugin.editor.containsKey(player)){
											CraftArray craftcmd = new CraftArray();
											//On converti en ItemStack
											boolean error = false;
											for(int i = 1; i <= 2; i++){
												ArrayList<String> part = new ArrayList<String>();
												if(i == 1){
													//Craft
													part = new ArrayList<String>(Arrays.asList(args[1].split(",")));
												} else if(i == 2){
													//Result
													part = new ArrayList<String>(Arrays.asList(args[2].split(",")));
												}
												for(String eitm : part){
													if(error == false){
														ItemStack itms = null;
														//Détection du nombre
														int amount = 1;
														if(eitm.contains("*")){
															try{
																amount = Integer.parseInt(eitm.substring(eitm.indexOf("*")+1, eitm.length()));
															} catch (Exception e){
																//...
															}
														}
														eitm = eitm.replace("*" + amount, "");
														//ID avec Data
														if(eitm.contains(":")){
															ArrayList<String> idastr = new ArrayList<String>(Arrays.asList(eitm.split(":")));
															try{
																itms = new ItemStack(Material.getMaterial(Integer.parseInt(idastr.get(0))),amount);
																itms.setDurability(Short.parseShort(idastr.get(1)));
															} catch (Exception e){
																try{
																	itms = new ItemStack(Material.getMaterial(idastr.get(0).toUpperCase()),amount);
																	itms.setDurability(Short.parseShort(idastr.get(1)));
																} catch (Exception ee){
																	//...
																}
															}
														} else {
															try{
																if(Integer.parseInt(eitm) >= 0){
																	itms = new ItemStack(Material.getMaterial(Integer.parseInt(eitm)),amount);
																}
															} catch (Exception e){
																try{
																	itms = new ItemStack(Material.getMaterial(eitm.toUpperCase()),amount);
																} catch (Exception ee){
																	player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_wrong") + eitm);
																}
															}
															
														}
														if(itms != null){
															if(i == 1){
																craftcmd.addCraftItem(itms);
															} else if(i == 2){
																craftcmd.addResultItem(itms);
															}
														} else {
															error = true;
														}
													}
												}
											}
											
											if(!craftcmd.isEmpty()){
												if(error == false){
													player.sendMessage("§d§l➤ " + plugin.lang.getTranslation("craftmaking_thats_right"));
													plugin.craftFormat.getCraftRecap(craftcmd, "§b" + plugin.lang.getTranslation("craftmaking_craft_typed")).send(player);
													player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_craft_confirm_cmd"));
													plugin.editor.put(player, 3);
													plugin.craft.put(player, craftcmd);
													return true;
												}
											}
										} else {
											player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_craft_need_confirm"));
											player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_craft_confirm_cmd"));
											return false;
										}
									}
									player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_example_cmd_1"));
									player.sendMessage("§b" + plugin.lang.getTranslation("craftmaking_example_cmd_2"));
									return false;
								} else {
									if(!plugin.editor.containsKey(player)){
										//Listes éditeur
										plugin.editor.put(player, 1);
										plugin.craft.put(player, new CraftArray());
										//Explications
										player.sendMessage("§d§l➤ " + plugin.lang.getTranslation("craftmaking_step_1"));
										player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_step_1_explain"));
										player.sendMessage("§f§l§m-----");
										player.sendMessage("§7" + plugin.lang.getTranslation("craftmaking_editor_cmd_exit"));
										player.sendMessage("§7" + plugin.lang.getTranslation("craftmaking_editor_cmd_removelast"));
										player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_editor_cmd_next"));
										player.sendMessage("§d§l§m-----");
										return true;
									}
									player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_already_in"));
									return false;
								}
							}
							player.sendMessage("§c" + plugin.lang.getTranslation("general_do_not_permission"));
							return false;
						}
						else if(args[0].equalsIgnoreCase("list")){
							if(player.hasPermission("caulcrafting.admin.list")) {
								//Intervalle d'action (pagination)
								int page = 0;
								if(args.length > 1){
									String p = args[1];
									try{
										page = Integer.parseInt(p);
										if(page < 0)
											page = 0;
									}catch (Exception e){
										player.sendMessage("§c" + plugin.lang.getTranslation("craftlist_no_display"));
										return true;
									}
								}
								int mincraft = 10*page;
								int maxcraft = mincraft + 10;
								//On récup les crafts
								ArrayList<CraftArray> craftlist = plugin.craftStorage.getCrafts();
								//On affiche la liste
								int count = 0;
								if(craftlist.size()-1 >= mincraft){
									player.sendMessage("§d§l§m-----§b(" + plugin.lang.getTranslation("craftlist_page_nb").replace("%number%", "" + page) + ")");
									for(CraftArray crafts : craftlist){
										if(count >= mincraft && count <= maxcraft){
											plugin.craftFormat.getCraftRecap(crafts, "§6§l" + count + ".").send(player);
										}
										count++;
									}
									if(maxcraft < craftlist.size()-1){
										player.sendMessage("§d§l§m-----§b(" + plugin.lang.getTranslation("craftlist_next_page") + " §3/... list " + (page+1) + "§b)");
									} else {
										player.sendMessage("§d§l§m-----§b(" + plugin.lang.getTranslation("craftlist_list_finished") + ")");
									}
								} else {
									player.sendMessage("§c" + plugin.lang.getTranslation("craftlist_no_display"));
								}
								return true;
							}
							player.sendMessage("§c" + plugin.lang.getTranslation("general_do_not_permission"));
							return false;
						}
						else if(args[0].equalsIgnoreCase("remove")){
							if(player.hasPermission("caulcrafting.admin.remove")) {
								//Numéro du craft
								int nb = -1;
								if(args.length > 1){
									String p = args[1];
									try{
										nb = Integer.parseInt(p);
										if(nb < 0)
											nb = -1;
									}catch (Exception e){
										nb = -1;
									}
								}
								if(nb != -1){
									//Getting all crafts
									ArrayList<CraftArray> craftlist = plugin.craftStorage.getCrafts();
									//If craft number is lower than the higher craft number
									if(craftlist.size()-1 >= nb){
										CraftArray specraft = craftlist.get(nb);
										plugin.craftFormat.getCraftRecap(specraft, "§7" + plugin.lang.getTranslation("craftremove_removed")).send(player);
										//Removing
										plugin.craftStorage.removeCraft(nb);
										//Reload config ;)
										plugin.reloadConfig();
										return true;
									}
								}
								player.sendMessage("§c" + plugin.lang.getTranslation("craftremove_error"));
								return true;
							}
							player.sendMessage("§c" + plugin.lang.getTranslation("general_do_not_permission"));
							return false;
						}
					}
					player.sendMessage("§b§lCaulCrafting v" + plugin.getDescription().getVersion() + " §b" + plugin.lang.getTranslation("maincmd_by"));
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§6§l" + plugin.lang.getTranslation("maincmd_create").toUpperCase());
					player.sendMessage("§e/caulcrafting §2create §7§o" + plugin.lang.getTranslation("maincmd_create_easy"));
					player.sendMessage("§e§o/caulcrafting §2§ocreate §b§o" + plugin.lang.getTranslation("maincmd_create_cmd_args") + " §7§o" + plugin.lang.getTranslation("maincmd_create_fast"));
					player.sendMessage("§7§l" + plugin.lang.getTranslation("maincmd_list").toUpperCase());
					player.sendMessage("§e/caulcrafting §blist");
					player.sendMessage("§7§l" + plugin.lang.getTranslation("maincmd_remove").toUpperCase());
					player.sendMessage("§e/caulcrafting §cremove " + plugin.lang.getTranslation("maincmd_remove_args"));
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§d§l" + plugin.lang.getTranslation("maincmd_discord"));
					player.sendMessage("§bhttps://discord.gg/w628upr");
					return false;
				}
			}
			sender.sendMessage("§c" + plugin.lang.getTranslation("maincmd_robot"));
			return false;
		}
		return false;
	}
	
}
