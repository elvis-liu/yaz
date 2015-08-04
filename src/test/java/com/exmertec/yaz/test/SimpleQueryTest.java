package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.User;

import org.junit.Test;

import java.util.List;

public class SimpleQueryTest extends TestBase {
    @Test
    public void should_query_by_id() throws Exception {
        String userName = "name";
        Long id = prepareUser(userName);

        User queriedUser = new UserDao().idEquals(id).querySingle();

        assertThat(queriedUser.getName()).isEqualTo(userName);
    }

    @Test
    public void should_query_when_like() throws Exception {
        final String userName = "name";
        prepareUser(userName);

        List<User> result = new UserDao().where(field("name").like(userName.substring(1))).queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(userName);
    }

    @Test
    public void should_query_when_not_like() throws Exception {
        final String userName = "name";
        final String notLike = "not_like";
        prepareUser(userName);

        List<User> result = new UserDao().where(field("name").like(notLike)).queryList();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void should_query_when_in_conditions() throws Exception {
        prepareUser("name1");
        prepareUser("name2");

        List<User> result = new UserDao().where(field("name").in("name1")).queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("name1");
    }

    @Test
    public void should_query_when_in_empty_conditions() throws Exception {
        prepareUser("name");

        List<User> result = new UserDao().where(field("name").in()).queryList();

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void should_query_when_eq_null() throws Exception {
        prepareUser("name");
        prepareUser(null);

        List<User> result = new UserDao().where(field("name").eq(null)).queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isNull();
    }

    @Test
    public void should_query_when_ne_null() throws Exception {
        prepareUser("name");
        prepareUser(null);

        List<User> result = new UserDao().where(field("name").ne(null)).queryList();

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
}
