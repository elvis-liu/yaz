package com.exmertec.yaz.test;

import com.exmertec.yaz.BaseDao;
import com.exmertec.yaz.model.User;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public abstract class TestBase {
    @PersistenceContext
    protected EntityManager entityManager;

    protected Long prepareUser(String name) {
        UserDao userDao = new UserDao();

        User user = new User();
        user.setName(name);
        userDao.save(user);

        return user.getId();
    }

    protected class UserDao extends BaseDao<User> {
        public UserDao() {
            super(TestBase.this.entityManager, User.class);
        }
    }
}
