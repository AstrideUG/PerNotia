package me.helight.pernotia.person.impl;

import com.google.inject.Inject;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.common.RedisManager;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.person.DataAccessPerson;
import org.redisson.api.RMap;

import javax.annotation.Nullable;

public class CachedPerson extends DataAccessPerson {

    public CachedPerson(Person person) {
        super(person);
        PerNotia.injector.injectMembers(this);
    }

    @Inject
    private RedisManager redisManager;

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
        Object object = redisManager.getRedissonClient().getMap("pernotia_" + person.getUuid()).get(field);
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
        if (value == null) {
            redisManager.getRedissonClient().getMap("pernotia_" + person.getUuid()).remove(field);
        } else {
            redisManager.getRedissonClient().getMap("pernotia_" + person.getUuid()).put(field,value);
        }
    }

    /**
     * Returns the raw Redisson-{@link RMap} corresponding to the given player
     */
    public RMap getMap() {
        return redisManager.getRedissonClient().getMap("pernotia_" + person.getUuid());
    }

}
