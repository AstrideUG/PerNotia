package me.helight.pernotia;

import com.google.inject.Inject;
import me.helight.ccom.info.NoAPI;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.database.PersonDao;
import sun.rmi.runtime.Log;

public class LoginVerify {

    private final PersonDao personDao;

    private final PerNotia perNotia;

    @Inject
    public LoginVerify(PersonDao personDao, PerNotia perNotia) {
        this.personDao = personDao;
        this.perNotia = perNotia;
    }

    @ThreadBlocking
    @NoAPI
    private VerificationResult check(Person person) {
        if (personDao.exists(person)) return VerificationResult.PASS;
        boolean existsName = personDao.existsName(person);
        boolean existsUuid = personDao.existsUuid(person);

        if (!existsName && !existsUuid) {
            return VerificationResult.NOT_REGISTERED;
        } else if (!existsName) {
            return VerificationResult.CHANGED_NAME;
        } else {
            return VerificationResult.INVALID_UUID;
        }
    }

    @ThreadBlocking
    public void verify(Person person) {
        PerNotia.POOL.execute(() -> {
            switch (check(person)) {
                case NOT_REGISTERED:
                    perNotia.handleRegister(person);
                    break;
                case INVALID_UUID:
                    perNotia.handleInvalidUuid(person);
                    break;
                case CHANGED_NAME:
                    personDao.changeName(person.getUuid(), person.getName());
                    break;
                default:
                    break;
            }
        });
    }

    public enum VerificationResult {
        PASS,
        NOT_REGISTERED,
        CHANGED_NAME,
        INVALID_UUID
    }

}
