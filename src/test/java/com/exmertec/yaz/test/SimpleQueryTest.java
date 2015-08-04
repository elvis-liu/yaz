package com.exmertec.yaz.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.BaseDao;
import com.exmertec.yaz.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class SimpleQueryTest {
    @PersistenceContext
    protected EntityManager entityManager;

    @Test
    public void should_query_by_id() throws Exception {
        String userName = "name";
        Long id = prepareUser(userName);

        UserDao userDao = new UserDao();
        User queriedUser = userDao.idEquals(id).querySingle();

        assertThat(queriedUser.getName()).isEqualTo(userName);
    }

    @Test
    public void should_query_when_like() throws Exception {
        final String userName = "name";
        prepareUser(userName);

        List<User> result = new UserDao() {
            public List<User> queryList() {
                return with(field("name").like(userName.substring(1))).queryList();
            }
        }.queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(userName);
    }

    @Test
    public void should_query_when_not_like() throws Exception {
        final String userName = "name";
        final String notLike = "not_like";
        prepareUser(userName);

        List<User> result = new UserDao() {
            public List<User> queryList() {
                return with(field("name").like(notLike)).queryList();
            }
        }.queryList();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void should_query_when_in_conditions() throws Exception {
        prepareUser("name1");
        prepareUser("name2");

        List<User> result = new UserDao() {
            public List<User> queryList() {
                return with(field("name").in("name1")).queryList();
            }
        }.queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("name1");
    }

    @Test
    public void should_query_when_in_empty_conditions() throws Exception {
        prepareUser("name");

        List<User> result = new UserDao() {
            public List<User> queryList() {
                return with(field("name").in()).queryList();
            }
        }.queryList();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void should_query_when_eq_null() throws Exception {
        prepareUser("name");
        prepareUser(null);

        List<User> result = new UserDao() {
            public List<User> queryList() {
                return with(field("name").eq(null)).queryList();
            }
        }.queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isNull();
    }

    @Test
    public void should_query_when_ne_null() throws Exception {
        prepareUser("name");
        prepareUser(null);

        List<User> result = new UserDao() {
            public List<User> queryList() {
                return with(field("name").ne(null)).queryList();
            }
        }.queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("name");
    }

    @Test
    public void should_query_page_with_index_and_size() throws Exception {
        prepareUser("n1");
        prepareUser("n2");
        prepareUser("n3");
        prepareUser("n4");
        prepareUser("s1");
        prepareUser("s2");

        List<User> result = new UserDao() {
            public List<User> queryList() {
                return with(field("name").like("n")).queryPage(2, 1);
            }
        }.queryList();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).startsWith("n");
        assertThat(result.get(1).getName()).startsWith("n");
    }

    @Test
    public void should_query_single_result() throws Exception {
        prepareUser("n1");
        prepareUser("n2");

        User user = new UserDao() {
            public User query() {
                return with(field("name").eq("n1")).querySingle();
            }
        }.query();

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("n1");
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_when_query_single_has_multi_result() throws Exception {
        prepareUser("n1");
        prepareUser("n2");

        new UserDao() {
            public User query() {
                return with(field("name").like("n")).querySingle();
            }
        }.query();
    }

    private Long prepareUser(String name) {
        UserDao userDao = new UserDao();

        User user = new User();
        user.setName(name);
        userDao.save(user);

        return user.getId();
    }

    private class UserDao extends BaseDao<User> {
        public UserDao() {
            super(SimpleQueryTest.this.entityManager, User.class);
        }
    }
}
