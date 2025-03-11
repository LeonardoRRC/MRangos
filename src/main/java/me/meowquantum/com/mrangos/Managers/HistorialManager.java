package me.meowquantum.com.mrangos.Managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.meowquantum.com.mrangos.MRangos;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistorialManager {

    private final MRangos plugin;
    private final File historialFile;
    private final Gson gson;
    private Map<String, List<Map<String, String>>> historial;

    public HistorialManager(MRangos plugin) {
        this.plugin = plugin;
        this.historialFile = new File(plugin.getDataFolder(), "historial.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!historialFile.exists()) {
            try {
                historialFile.createNewFile();
                historial = new HashMap<>();
                guardarHistorial();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.historial = cargarHistorial();
        }
    }

    public void registrarHistorial(String usuario, String staff, String rango, String razon, boolean temporal) {
        historial.computeIfAbsent(usuario, k -> new java.util.ArrayList<>());

        Map<String, String> entry = new HashMap<>();
        entry.put("rango", rango);
        entry.put("dado_por", staff);
        entry.put("razon", razon);
        entry.put("fecha", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        entry.put("temporal", String.valueOf(temporal));

        historial.get(usuario).add(entry);
        guardarHistorial();
    }

    public boolean mostrarHistorial(CommandSender sender, String usuario) {
        if (!historial.containsKey(usuario) || historial.get(usuario).isEmpty()) {
            return false;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  &6❇ Historial de &l"+ usuario +" &6❇"));
        for (Map<String, String> entry : historial.get(usuario)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m                                         "));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r "));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &fRango&8: &6" + entry.get("rango")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &fDado Por&8: &b" + entry.get("dado_por")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &fDescripción&8: &e" + entry.get("razon")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &fFecha&8: &a" + entry.get("fecha")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &f¿Temporal?&8: &c" + entry.get("temporal")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r "));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m                                         "));
        }
        return true;
    }

    public boolean yaRegistrado(String usuario, String rango) {
        return historial.containsKey(usuario) &&
                historial.get(usuario).stream()
                        .anyMatch(entry -> entry.get("rango").equalsIgnoreCase(rango));
    }

    private Map<String, List<Map<String, String>>> cargarHistorial() {
        if (!historialFile.exists()) return new HashMap<>();
        try {
            String content = new String(Files.readAllBytes(historialFile.toPath()));
            return gson.fromJson(content, new TypeToken<Map<String, List<Map<String, String>>>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void guardarHistorial() {
        try (FileWriter writer = new FileWriter(historialFile)) {
            gson.toJson(historial, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
