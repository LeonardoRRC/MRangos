package me.meowquantum.com.mrangos.Commands;

import me.meowquantum.com.mrangos.MRangos;
import me.meowquantum.com.mrangos.Managers.HistorialManager;
import me.meowquantum.com.mrangos.Managers.LuckPermsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final MRangos plugin;
    private final HistorialManager historialManager;
    private final LuckPermsManager luckPermsManager;

    public CommandManager(MRangos plugin, HistorialManager historialManager, LuckPermsManager luckPermsManager) {
        this.plugin = plugin;
        this.historialManager = historialManager;
        this.luckPermsManager = luckPermsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mrangos.use")) {
            return true;
        }

        if (args.length < 4 && !args[0].equalsIgnoreCase("historial")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aRangos&8] ▶ &cUso incorrecto. Debes proporcionar una razón."));
            return false;
        }

        String subcomando = args[0].toLowerCase();
        String usuario = args[1];

        switch (subcomando) {
            case "darrango":
                String rango = args[2];
                String razon = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                luckPermsManager.asignarRango(usuario, rango);
                historialManager.registrarHistorial(usuario, sender.getName(), rango, razon, false);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aRangos&8] ▶ &fSe ha asignado el rango &a" + rango + " &fa &a" + usuario));
                break;

            case "darrangotemporal":
                if (args.length < 5) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aRangos&8] ▶ &cUso incorrecto. Debes proporcionar duración y razón."));
                    return false;
                }

                int duracion;
                try {
                    duracion = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aRangos&8] ▶ &cDuración inválida. Debe ser un número."));
                    return true;
                }

                String razonTemporal = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
                luckPermsManager.asignarRangoTemporal(usuario, args[2], duracion);
                historialManager.registrarHistorial(usuario, sender.getName(), args[2], razonTemporal, true);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aRangos&8] ▶ &fSe ha asignado el rango &a" + args[2] + " &fa &a" + usuario + " &fpor &a" + duracion + " &fsegundos."));
                break;

            case "historial":
                if (!historialManager.mostrarHistorial(sender, usuario)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&aRangos&8] ▶ &fNo se encontraron registros para &c" + usuario));
                }
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("darrango");
            completions.add("darrangotemporal");
            completions.add("historial");
        } else if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("darrango") || args[0].equalsIgnoreCase("darrangotemporal"))) {
            completions.addAll(luckPermsManager.obtenerRangos());
        }

        return completions;
    }
}
