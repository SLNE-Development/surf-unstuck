package dev.slne.unstuck.bukkit.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dev.slne.unstuck.bukkit.BukkitMain;
import net.md_5.bungee.api.ChatColor;

public class LoggingUtils {

    public static File setuplogfolder() {
        BukkitMain plugin = BukkitMain.getInstance();

        if (!plugin.getDataFolder().exists()) { // Check if plugin folder exists
            plugin.getDataFolder().mkdir(); // if not then create it
        }

        File logsfolder = new File(plugin.getDataFolder(), "logs"); // Set the path of the new logs folder

        if (!logsfolder.exists()) { // Check if logs folder exists
            logsfolder.mkdirs(); // if not then create it
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Created the logs folder");
        }

        return logsfolder;
    }

    public static void logToFile(String message) {
        BukkitMain plugin = BukkitMain.getInstance();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); // Set the Time Format
        LocalDateTime now = LocalDateTime.now(); // Get the time

        try {
            File dataFolder = plugin.getDataFolder(); // Sets file to the plugins/<pluginname> folder
            if (!dataFolder.exists()) { // Check if logs folder exists
                dataFolder.mkdir(); // if not then create it
            }
            File saveTo = new File(plugin.getDataFolder() + "/logs/unstuck.log"); // Hardcoded file name here

            if (!saveTo.exists()) { // Check if logs folder exists
                saveTo.createNewFile(); // if not then create it
            }
            FileWriter fw = new FileWriter(saveTo, true); // Create a FileWriter to edit the file
            PrintWriter pw = new PrintWriter(fw); // Create a PrintWriter

            pw.println("[" + dtf.format(now).toString() + "] " + message); // Message added to log file
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace(); // If theres any errors in this process it will print the error in console
        }
    }
}
