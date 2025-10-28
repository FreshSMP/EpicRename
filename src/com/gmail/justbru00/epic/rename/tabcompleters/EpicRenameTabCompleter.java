package com.gmail.justbru00.epic.rename.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class EpicRenameTabCompleter implements TabCompleter {

	private final ArrayList<String> epicrenameFirstArgumentList = new ArrayList<>();
	private final ArrayList<String> empty = new ArrayList<>();

	public EpicRenameTabCompleter() {
		epicrenameFirstArgumentList.add("help");
		epicrenameFirstArgumentList.add("license");
		epicrenameFirstArgumentList.add("reload");
		epicrenameFirstArgumentList.add("debug");
		epicrenameFirstArgumentList.add("version");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if (!command.getName().equalsIgnoreCase("epicrename")) {
			return null;
		}		

		if (args.length == 1) {			
			if (!args[0].equals("")) {
				ArrayList<String> completion = new ArrayList<>();

				for (String first : epicrenameFirstArgumentList) {
					if (first.toLowerCase().startsWith(args[0].toLowerCase())) {
						completion.add(first);
					}
				}

				return completion;				
			} else {
				return epicrenameFirstArgumentList;
			}
		}		

		return empty;
	}
}
