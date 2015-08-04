package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.User;

import org.junit.Test;

import java.util.List;

import javax.persistence.LockModeType;

public class SimpleOptionTest extends TestBase {
    @Test
    public void should_accept_ascending_order() throws Exception {
        prepareUser("b");
        prepareUser("c");
        prepareUser("d");
        prepareUser("a");

        List<User> result = new UserDao().where().ascendingBy("name").queryList();

        assertThat(result.get(0).getName()).isEqualTo("a");
        assertThat(result.get(1).getName()).isEqualTo("b");
        assertThat(result.get(2).getName()).isEqualTo("c");
        assertThat(result.get(3).getName()).isEqualTo("d");
    }

    @Test
    public void should_accept_descending_order() throws Exception {
        prepareUser("b");
        prepareUser("c");
        prepareUser("d");
        prepareUser("a");

        List<User> result = new UserDao().where().descendingBy("name").queryList();

        assertThat(result.get(0).getName()).isEqualTo("d");
        assertThat(result.get(1).getName()).isEqualTo("c");
        assertThat(result.get(2).getName()).isEqualTo("b");
        assertThat(result.get(3).getName()).isEqualTo("a");
    }

    @Test
    public void should_accept_lock_by() throws Exception {
        prepareUser("a");

        User user = new UserDao().where(field("name").eq("a")).lockBy(LockModeType.PESSIMISTIC_WRITE).querySingle();

        assertThat(entityManager.getLockMode(user)).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }
}
