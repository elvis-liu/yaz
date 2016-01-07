package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.User;

import org.junit.Test;

import java.util.List;

public class SimpleActionTest extends TestBase {
    @Test
    public void should_count() throws Exception {
        prepareUser("a");
        prepareUser("ab");
        prepareUser("b");

        Long count = new UserDao().where(field("name").like("a")).count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void should_query_page_with_index_and_size() throws Exception {
        prepareUser("n1");
        prepareUser("n2");
        prepareUser("n3");
        prepareUser("n4");
        prepareUser("s1");
        prepareUser("s2");

        List<User> result = new UserDao().where(field("name").like("n")).queryPage(2, 1);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).startsWith("n");
        assertThat(result.get(1).getName()).startsWith("n");
    }

    @Test
    public void should_query_single_result() throws Exception {
        prepareUser("n1");
        prepareUser("n2");

        User user = new UserDao().where(field("name").eq("n1")).querySingle();

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("n1");
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_when_query_single_has_multi_result() throws Exception {
        prepareUser("n1");
        prepareUser("n2");

        new UserDao().where(field("name").like("n")).querySingle();
    }

    @Test
    public void should_query_fist_result() throws Exception {
        prepareUser("b");
        prepareUser("c");
        prepareUser("a");

        User user = new UserDao().where().ascendingBy("name").queryFirst();

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("a");
    }
}
