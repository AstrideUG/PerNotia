package me.helight.pernotia.bungee;

import me.helight.pernotia.PerNotia;
import me.helight.pernotia.database.Person;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Proxy;

public class PerNotiaBungeeImpl extends Plugin implements PerNotia {

    @Override
    public void onEnable() {
        hook();
    }

    @Override
    public void sendMessage(Person person, String message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getUniqueId().toString().equals(person.getUuid())) {
                player.sendMessage(TextComponent.fromLegacyText(message));
            }
        }
    }
}
