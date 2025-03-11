package me.meowquantum.com.mrangos.Managers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LuckPermsManager {

    private final LuckPerms luckPerms;

    public LuckPermsManager() {
        this.luckPerms = LuckPermsProvider.get();
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public void asignarRango(String usuario, String rango) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(usuario);
        User user = luckPerms.getUserManager().loadUser(player.getUniqueId()).join();

        if (user == null) return;

        Node node = Node.builder("group." + rango).build();
        user.data().add(node);
        luckPerms.getUserManager().saveUser(user);
    }

    public void asignarRangoTemporal(String usuario, String rango, int duracionSegundos) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(usuario);
        User user = luckPerms.getUserManager().loadUser(player.getUniqueId()).join();

        if (user == null) {
            System.out.println("[LuckPermsManager] No se pudo cargar el usuario " + usuario);
            return;
        }

        user.data().clear(node -> node instanceof InheritanceNode &&
                !((InheritanceNode) node).getGroupName().equalsIgnoreCase("default"));

        InheritanceNode node = InheritanceNode.builder(rango)
                .expiry(Duration.ofSeconds(duracionSegundos))
                .build();
        user.data().add(node);
        luckPerms.getUserManager().saveUser(user);
        System.out.println("[LuckPermsManager] Se asignó el rango temporal " + rango + " a " + usuario + " por " + duracionSegundos + " segundos.");
    }

    public List<String> obtenerRangos() {
        List<String> rangos = new ArrayList<>();
        Collection<Group> groups = luckPerms.getGroupManager().getLoadedGroups();
        for (Group group : groups) {
            rangos.add(group.getName());
        }
        return rangos;
    }
}
