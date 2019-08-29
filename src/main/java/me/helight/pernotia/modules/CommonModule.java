package me.helight.pernotia.modules;

import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;
import me.helight.ccom.config.ConfigBuilder;
import me.helight.ccom.config.defaults.SimpleRedisConfig;
import me.helight.pernotia.LoginVerify;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.database.GeneralPersonDao;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

@AllArgsConstructor
public class CommonModule extends AbstractModule {

    private PerNotia perNotia;

    @SuppressWarnings("unchecked")
    protected void configure() {
        bind(PerNotia.class).toInstance(perNotia);

        GeneralPersonDao generalPersonDao = new GeneralPersonDao();
        bind(GeneralPersonDao.class).toInstance(generalPersonDao);

        SimpleRedisConfig defaultRedisConfig = SimpleRedisConfig.DEFAULT;
        defaultRedisConfig.setHost("vps640234.ovh.net");
        defaultRedisConfig.setPassword("debug");

        me.helight.ccom.config.Config<SimpleRedisConfig> simpleConfig = new ConfigBuilder<SimpleRedisConfig>()
                .setClass(SimpleRedisConfig.class)
                .setDefault(defaultRedisConfig)
                .setSubfolder("plugins/PerNotia/")
                .setFilename("redisCredentials.json")
                .build();
        SimpleRedisConfig redisConfig = simpleConfig.read();
        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useSingleServer().setAddress("redis://"+redisConfig.getHost()+":"+redisConfig.getPort());

        if (redisConfig.getPassword().equalsIgnoreCase("none")) {
            config.useSingleServer().setAddress("redis://"+redisConfig.getHost()+":"+redisConfig.getPort());
        } else {
            config.useSingleServer().setAddress("redis://"+redisConfig.getHost()+":"+redisConfig.getPort()).setPassword(redisConfig.getPassword());
        }

        config.setThreads(16);

        RedissonClient redissonClient = (Redisson.create(config));

        bind(RedissonClient.class).toInstance(redissonClient);
        bind(LoginVerify.class).toInstance(new LoginVerify(generalPersonDao,perNotia));
    }
}
