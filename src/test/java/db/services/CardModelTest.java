package db.services;

import db.exceptions.DatabaseException;
import db.services.impl.db.AccountDAO;
import db.services.impl.db.DBAccountServiceImpl;
import db.services.impl.db.DBSessionFactoryService;
import org.junit.Test;

/**
 * Created by igor on 03.06.16.
 */
public class CardModelTest {

    protected boolean prepareDb() {
        final DBSessionFactoryService sessionFactory = new DBSessionFactoryService("hibernate.test.cfg.xml");
        sessionFactory.configure();
        final DBAccountServiceImpl dba = new DBAccountServiceImpl(sessionFactory, new AccountDAO());
        return true;
    }
    @Test
    public boolean testAddModelToBase() {
        prepareDb();

        return true;
    }
}
