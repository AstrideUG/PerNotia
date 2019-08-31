package me.helight.pernotia.person;

import com.google.common.collect.ForwardingObject;
import com.google.inject.Inject;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.configuration.PerNotiaConfiguration;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.person.message.Disconnect;
import me.helight.pernotia.person.message.Message;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;

import java.util.UUID;

public class MessagePerson extends ForwardingObject {

    private Person person;

    @Inject
    private RedissonClient redissonClient;

    @Inject
    private PerNotia perNotia;

    @Inject
    private PerNotiaConfiguration configuration;

    public MessagePerson(Person person) {
        this.person = person;
        PerNotia.injector.injectMembers(this);
    }

    private RTopic getTopic() {
        return redissonClient.getTopic(configuration.getRedisMapPrefix() + "pubsub_" + person.getUuid());
    }

    /**
     * @return ID of the Listener
     */
    @ThreadBlocking
    public <K> int addListener(Class<K> clazz, MessageListener<K> messageListener) {
        return getTopic().addListener(clazz, messageListener);
    }

    /**
     * Adds a Listener which will be unregistered, when the player disconnects
     * @return ID of the Listener
     */
    @ThreadBlocking
    public <K> int addListenerTemp(Class<K> clazz, MessageListener<K> messageListener) {
        if (perNotia.getRedisMessageListeners().containsKey(UUID.fromString(person.getUuid()))) {
            int i = getTopic().addListener(clazz, messageListener);
            perNotia.getRedisMessageListeners().get(UUID.fromString(person.getUuid())).add(i);
            return i;
        } else {
            return -1;
        }
    }


    @ThreadBlocking
    public void removeListener(int id) {
        getTopic().removeListenerAsync(id);
    }

    @ThreadBlocking
    public void removeListener(MessageListener messageListener) {
        getTopic().removeListener(messageListener);
    }

    public void disconnect(String message) {
        Disconnect disconnect = new Disconnect(message);
        getTopic().publishAsync(disconnect);
    }

    public void sendMessage(String message) {
        Message msg = new Message(message);
        getTopic().publishAsync(msg);
    }

    @Override
    protected Object delegate() {
        return person;
    }
}
