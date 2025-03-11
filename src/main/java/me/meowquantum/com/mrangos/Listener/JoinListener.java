package me.meowquantum.com.mrangos.Listener;

import me.meowquantum.com.mrangos.MRangos;
import me.meowquantum.com.mrangos.Managers.HistorialManager;
import me.meowquantum.com.mrangos.Managers.LuckPermsManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Bukkit;

import java.util.Optional;

public class JoinListener implements Listener {

    private final MRangos plugin;
    private final LuckPermsManager luckPermsManager;
    private final HistorialManager historialManager;

    public JoinListener(MRangos plugin, LuckPermsManager luckPermsManager, HistorialManager historialManager) {
        this.plugin = plugin;
        this.luckPermsManager = luckPermsManager;
        this.historialManager = historialManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            User user = luckPermsManager.getLuckPerms().getUserManager().loadUser(event.getPlayer().getUniqueId()).join();
            if (user == null) return;

            Optional<String> rango = user.getNodes().stream()
                    .filter(node -> node.getKey().startsWith("group.") && !node.getKey().equals("group.default"))
                    .map(node -> node.getKey().replace("group.", ""))
                    .findFirst();

            if (rango.isPresent()) {
                String rangoNombre = rango.get();

                if (!historialManager.yaRegistrado(playerName, rangoNombre)) {
                    historialManager.registrarHistorial(playerName, "Sistema", rangoNombre, "Migracion LuckPerms", false);
                }
            }
        });
    }
}
