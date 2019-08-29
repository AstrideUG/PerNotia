package me.helight.pernotia.api;

import me.helight.ccom.info.ThreadBlocking;
import me.helight.pernotia.PerNotia;
import me.helight.pernotia.database.GeneralPersonDao;
import me.helight.pernotia.database.Person;

public class UserAPI {

    @ThreadBlocking
    public static int exists(String player) {
        GeneralPersonDao dao = getGeneralDAO();
        if (dao.existsUuid(new Person(null, player, null))) {
           return 1;
        } else return dao.existsName(new Person(null, null, player)) ? 2 : 0;
    }

    @ThreadBlocking
    public static Person getPerson(String player) {
        GeneralPersonDao dao = getGeneralDAO();
        switch (exists(player)) {
            case 0:
                return null;
            case 1:
                return dao.getPersonByUUID(player);
            case 2:
                return dao.getPersonByName(player);
            default:
                throw new IllegalArgumentException("Exists Call returned unexpected value");
        }
    }

    public static GeneralPersonDao getGeneralDAO() {
        return PerNotia.injector.getInstance(GeneralPersonDao.class);
    }

}
