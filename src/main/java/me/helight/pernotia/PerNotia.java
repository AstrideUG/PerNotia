package me.helight.pernotia;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.database.PersonDao;
import me.helight.pernotia.modules.CommonModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class PerNotia {

    public static final ExecutorService POOL = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public static Injector injector;

    @Inject
    private LoginVerify loginVerify;

    @Inject
    private PersonDao personDao;

    public final void hook() {
        injector = Guice.createInjector(new CommonModule(this));
        injector.injectMembers(this);
    }

    public void handleInvalidUuid(Person person) {

    }

    public void handleRegister(Person person) {
        personDao.save(person);
    }

    public void handleConnect(Person person) {
        loginVerify.verify(person);
    }

    public void handleDisconnect(Person person) {

    }

    public void handleVerifyError(Person person) {

    }

    public abstract void sendMessage(Person person, String message);

}
