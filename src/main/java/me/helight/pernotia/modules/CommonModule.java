package me.helight.pernotia.modules;

import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;
import me.helight.pernotia.LoginVerify;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.common.RedisManager;
import me.helight.pernotia.database.PersonDao;

@AllArgsConstructor
public class CommonModule extends AbstractModule {

    private PerNotia perNotia;

    protected void configure() {
        bind(PerNotia.class).toInstance(perNotia);
        bind(PersonDao.class).toInstance(new PersonDao());
        bind(RedisManager.class).toInstance(new RedisManager());
        bind(LoginVerify.class).toInstance(new LoginVerify());
    }
}
