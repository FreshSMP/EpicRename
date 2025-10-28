package com.gmail.justbru00.epic.rename.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ImportTabCompleter implements TabCompleter {

	private final ArrayList<String> importFirstArgumentList = new ArrayList<>();
	private final ArrayList<String> importHandInventorySecondArgumentList = new ArrayList<>();
	private final ArrayList<String> importRawSecondArgumentList = new ArrayList<>();
	private final ArrayList<String> empty = new ArrayList<>();

	public ImportTabCompleter() {
		importFirstArgumentList.add("hand");
		importFirstArgumentList.add("inventory");
		importFirstArgumentList.add("raw");

		importHandInventorySecondArgumentList.add("<webUrl>");

		importRawSecondArgumentList.add("<rawYAML>");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!command.getName().equalsIgnoreCase("import")) {
			return null;
		}

		if (args.length == 1) {
			if (!args[0].equals("")) {
				ArrayList<String> completion = new ArrayList<>();

				for (String first : importFirstArgumentList) {
					if (first.toLowerCase().startsWith(args[0].toLowerCase())) {
						completion.add(first);
					}
				}

				return completion;
			} else {
				return importFirstArgumentList;
			}
		} else if (args.length == 2) {
			if (!args[1].equals("")) {
				if (args[0].equalsIgnoreCase("hand") || args[0].equalsIgnoreCase("inventory")) {
					ArrayList<String> completion = new ArrayList<>();

					for (String second : importHandInventorySecondArgumentList) {
						if (second.toLowerCase().startsWith(args[1].toLowerCase())) {
							completion.add(second);
						}
					}

					return completion;
				} else if (args[0].equalsIgnoreCase("raw")) {
					ArrayList<String> completion = new ArrayList<>();

					for (String second : importRawSecondArgumentList) {
						if (second.toLowerCase().startsWith(args[1].toLowerCase())) {
							completion.add(second);
						}
					}

					return completion;
				} else {
					return empty;
				}
			} else {
				// No text in second argument yet
				if (args[0].equalsIgnoreCase("hand") || args[0].equalsIgnoreCase("inventory")) {
					return importHandInventorySecondArgumentList;
				} else if (args[0].equalsIgnoreCase("raw")) {
					return importRawSecondArgumentList;
				} else {
					return empty;
				}
			}
		}

		return empty;
	}
}
