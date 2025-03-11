package me.meowquantum.com.mrangos;

import me.meowquantum.com.mrangos.Commands.CommandManager;
import me.meowquantum.com.mrangos.Listener.JoinListener;
import me.meowquantum.com.mrangos.Managers.HistorialManager;
import me.meowquantum.com.mrangos.Managers.LuckPermsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MRangos extends JavaPlugin {

    private HistorialManager historialManager;
    private LuckPermsManager luckPermsManager;

    @Override
    public void onEnable() {
        this.historialManager = new HistorialManager(this);
        this.luckPermsManager = new LuckPermsManager();

        getServer().getPluginManager().registerEvents(new JoinListener(this, luckPermsManager, historialManager), this);

        CommandManager commandManager = new CommandManager(this, historialManager, luckPermsManager);
        getCommand("mrangos").setExecutor(commandManager);
        getCommand("mrangos").setTabCompleter(commandManager);
    }
}