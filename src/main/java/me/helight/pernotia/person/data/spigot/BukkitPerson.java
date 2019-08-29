package me.helight.pernotia.person.data.spigot;

import lombok.SneakyThrows;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.person.DataAccessPerson;
import me.helight.pernotia.person.data.SpecificPerson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BukkitPerson extends SpecificPerson {

    public BukkitPerson(DataAccessPerson accessPerson) {
        super(accessPerson);
    }

    @Override
    public void sendMessage(String message) {
        OfflinePlayer offlinePlayer = toPlayer((Person) delegate());
        if (offlinePlayer.isOnline()) {
            Player player = (Player)offlinePlayer;
            player.sendMessage(message);
        }
    }

    @Override
    public void disconnect(String message) {
        OfflinePlayer offlinePlayer = toPlayer((Person) delegate());
        if (offlinePlayer.isOnline()) {
            Player player = (Player)offlinePlayer;
            player.kickPlayer(message);
        }
    }

    public static Person fromPlayer(OfflinePlayer offlinePlayer) {
        Person person = new Person();
        person.setName(offlinePlayer.getName());
        person.setUuid(offlinePlayer.getUniqueId().toString());
        return person;
    }

    public static OfflinePlayer toPlayer(Person person) {
        return Bukkit.getOfflinePlayer(UUID.fromString(person.getUuid()));
    }

    public void storeItemstack(String field, ItemStack itemStack) {
        getAccessPerson().set(field, toBase64(itemStack));
    }

    public ItemStack getItemstack(String field) {
        return fromBase64(getAccessPerson().get(field, String.class));
    }

    @SneakyThrows
    private static String toBase64(ItemStack itemStack) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(itemStack);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    @SneakyThrows
    private static ItemStack fromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);


            ItemStack itemStack = (ItemStack)dataInput.readObject();
            dataInput.close();
            return itemStack;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    @Nullable
    public static Player toOnlinePlayer(Person person) {
        OfflinePlayer offlinePlayer = toPlayer(person);

        if (!offlinePlayer.isOnline()) {
            return null;
        }

        return (Player) offlinePlayer;
    }

}
