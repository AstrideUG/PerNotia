package me.helight.pernotia.spigot;

import me.helight.pernotia.PerNotia;
import me.helight.pernotia.ServiceMode;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.person.data.spigot.BukkitPerson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PerNotiaSpigotImpl extends JavaPlugin {

    private PerNotiaImpl perNotia;

    @Override
    public void onEnable() {
        perNotia = new PerNotiaImpl();
        perNotia.hook();
        Bukkit.getPluginManager().registerEvents(perNotia, this);
    }

    public class PerNotiaImpl extends PerNotia implements Listener {

        public PerNotiaImpl() {
            super(ServiceMode.SPIGOT);
        }

        @Override
        public void sendMessage(Person person, String message) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(person.getUuid()));
            if (player.isOnline()) {
                ((Player)player).sendMessage(message);
            }
        }

        @EventHandler
        public void onConnect(PlayerJoinEvent event) {
           perNotia.handleConnect(BukkitPerson.fromPlayer(event.getPlayer()));
        }


        @EventHandler
        public void onDisconnect(PlayerQuitEvent event) {
            perNotia.handleDisconnect(BukkitPerson.fromPlayer(event.getPlayer()));
        }
    }
}
