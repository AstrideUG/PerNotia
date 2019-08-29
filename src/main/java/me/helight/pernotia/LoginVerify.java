package me.helight.pernotia;

import me.helight.ccom.info.NoAPI;
import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.database.Person;
import me.helight.pernotia.database.GeneralPersonDao;

public class LoginVerify {

    private GeneralPersonDao generalPersonDao;

    private PerNotia perNotia;

    public LoginVerify(GeneralPersonDao generalPersonDao, PerNotia perNotia) {
        this.generalPersonDao = generalPersonDao;
        this.perNotia = perNotia;
    }

    @ThreadBlocking
    @NoAPI
    private VerificationResult check(Person person) {
        if (generalPersonDao.exists(person)) return VerificationResult.PASS;
        boolean existsName = generalPersonDao.existsName(person);
        boolean existsUuid = generalPersonDao.existsUuid(person);

        if (!existsName && !existsUuid) {
            return VerificationResult.NOT_REGISTERED;
        } else if (existsUuid) {
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
                    perNotia.handleRegister(person);
                    generalPersonDao.changeName(person.getUuid(), person.getName());
                    break;
                case CHANGED_NAME:
                    generalPersonDao.changeName(person.getUuid(), person.getName());
                    break;
                default:
                    perNotia.successfulLogin(person);
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
