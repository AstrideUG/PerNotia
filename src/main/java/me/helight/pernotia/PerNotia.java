package me.helight.pernotia;

import com.google.common.util.concurrent.MoreExecutors;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.database.PersonDao;
import org.bouncycastle.util.io.pem.PemWriter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public interface PerNotia {

    ExecutorService POOL = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    AtomicReference<PerNotia> instance = new AtomicReference<>();
    PersonDao personDao = new PersonDao();

    static PerNotia getPerNotia() {
        PerNotia perNotia = instance.get();

        if (perNotia == null) {
            throw new NullPointerException("No instance of PerNotia is currently active");
        }

        return perNotia;
    }

    default void hook() {
        PerNotia.instance.set(this);
    }

    default void handleInvalidUuid(Person person) {

    }

    default void handleRegister(Person person) {
        personDao.save(person);
    }

    default void handleConnect(Person person) {
        LoginVerify.verify(person);
    }

    default void handleDisconnect(Person person) {

    }

    default void handleVerifyError(Person person) {

    }

    void sendMessage(Person person, String message);

}
