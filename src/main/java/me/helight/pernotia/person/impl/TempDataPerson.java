package me.helight.pernotia.person.impl;

import com.google.inject.Inject;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.configuration.PerNotiaConfiguration;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.person.DataAccessPerson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import javax.annotation.Nullable;

public class TempDataPerson extends DataAccessPerson {

    public TempDataPerson(Person person) {
        super(person);
        PerNotia.injector.injectMembers(this);
    }

    @Inject
    private RedissonClient redissonClient;

    @Inject
    private PerNotiaConfiguration configuration;

    /**
     * Fetches a value from the temporary datasource
     *
     * @param field The field which shall be fetched
     * @param clazz The expected class of the value
     * @return The value of the field
     */
    @SuppressWarnings("unchecked")
    @ThreadBlocking
    public <K> K get(String field, Class<K> clazz) {
        Object object = redissonClient.getMap(getKey()).get(field);
        return object == null ? null : (K) object;
    }

    /**
     * Sets/Updates a field in the temporary datasource
     *
     * @param field The field which shall be updated
     * @param value The new value
     */
    @ThreadBlocking
    public void set(String field, @Nullable Object value) {
        RMap<Object, Object> map = redissonClient.getMap(getKey());
        if (value == null) {
            map.remove(field);
        } else {
            map.put(field, value);
        }
    }

    /**
     * Returns the raw Redisson-{@link RMap} corresponding to the given player
     */
    public RMap getMap() {
        return redissonClient.getMap(getKey());
    }

    public String getKey() {
        return configuration.getRedisMapPrefix() + person.getUuid();
    }

    public void t() {
    }
}
