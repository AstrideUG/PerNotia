package me.helight.pernotia.common;

import lombok.Getter;
import me.helight.ccom.config.ConfigBuilder;
import me.helight.ccom.config.defaults.SimpleRedisConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisManager {

    private static RedisManager instance;

    @Getter
    private RedissonClient redissonClient;

    public RedisManager() {
        instance = this;
        init();
    }

    @SuppressWarnings("unchecked")
    public void init() {
        me.helight.ccom.config.Config<SimpleRedisConfig> simpleConfig = new ConfigBuilder<SimpleRedisConfig>()
                .setClass(SimpleRedisConfig.class)
                .setDefault(SimpleRedisConfig.DEFAULT)
                .setSubfolder("plugins/PetNotia/")
                .setFilename("redisCredentials.json")
                .build();

        SimpleRedisConfig redisConfig = simpleConfig.read();
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisConfig.getHost()+":"+redisConfig.getPort()).setPassword(redisConfig.getPassword());
        redissonClient = (Redisson.create(config));
    }


    public static RedisManager getInstance() {
        if (instance == null) {
            return new RedisManager();
        } else {
            return instance;
        }
    }
}
