package me.helight.pernotia.person;

import com.google.common.collect.ForwardingObject;
import com.google.inject.Inject;
import lombok.NonNull;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.configuration.PerNotiaConfiguration;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.person.message.Disconnect;
import me.helight.pernotia.person.message.RawMessage;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.api.listener.StatusListener;

public class MessagePerson extends ForwardingObject {

    private Person person;

    @Inject
    private RedissonClient redissonClient;

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

    @ThreadBlocking
    public void removeListener(int id) {
        getTopic().removeListener(id);
    }

    @ThreadBlocking
    public void removeListener(MessageListener messageListener) {
        getTopic().removeListener(messageListener);
    }

    @ThreadBlocking
    public void disconnect(String message) {
        Disconnect disconnect = new Disconnect(message);
        getTopic().publishAsync(disconnect);
    }

    @Override
    protected Object delegate() {
        return person;
    }
}
