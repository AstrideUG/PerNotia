package me.helight.pernotia.bungee;

import me.helight.pernotia.PerNotia;
import me.helight.pernotia.ServiceMode;
import me.helight.pernotia.database.Person;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class PerNotiaBungeeImpl extends Plugin {

    private PerNotia perNotia;

    @Override
    public void onEnable() {
        perNotia = new PerNotiaImpl();
        perNotia.hook();
    }

    public class PerNotiaImpl extends PerNotia {

        public PerNotiaImpl() {
            super(ServiceMode.BUNGEE);
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
}
