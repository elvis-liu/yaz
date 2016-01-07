package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.and;
import static com.exmertec.yaz.BaseDao.field;
import static com.exmertec.yaz.BaseDao.or;
import static com.exmertec.yaz.BaseDao.subquery;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.Order;
import com.exmertec.yaz.model.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
    public void should_query_when_like_number() throws Exception {
        new UserBuilder().name("abc").points(1000).save();
        new UserBuilder().name("abcd").points(21000).save();
        User user = new UserDao().where(field("points").likeLiterally("100_")).querySingle();
        assertThat(user.getName()).isEqualTo("abc");
        List<User> users = new UserDao().where(field("points").likeLiterally("1%")).queryList();
        assertThat(users.size()).isEqualTo(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_illegal_argument_exception() throws Exception {
        new UserDao().where(field("xxx").eq("fdas")).queryList();
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
    public void should_query_when_in_list_conditions() throws Exception {
        prepareUser("name1");
        prepareUser("name2");

        List<User> result = new UserDao().where(field("name").in(Arrays.asList("name1"))).queryList();

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
    public void should_query_when_in_empty_list_conditions() throws Exception {
        prepareUser("name");

        List<User> result = new UserDao().where(field("name").in(new ArrayList<>())).queryList();

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
    public void should_query_with_and() throws Exception {
        prepareUser("abc");
        prepareUser("aaa");
        prepareUser("bbb");

        List<User> result = new UserDao().where(and(field("name").like("a"), field("name").like("ab"))).queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("abc");
    }

    @Test
    public void should_query_with_or() throws Exception {
        prepareUser("abc");
        prepareUser("aaa");
        prepareUser("bbb");

        List<User> result = new UserDao().where(or(field("name").like("ab"), field("name").like("aa")))
                .ascendingBy("name")
                .queryList();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("aaa");
        assertThat(result.get(1).getName()).isEqualTo("abc");
    }

    @Test
    public void should_allow_conditional_query() throws Exception {
        prepareUser("a");
        prepareUser("b");

        List<User> result = new UserDao().where(
                field("name").eq("a").when(false), // ignore query
                field("name").eq("b").when(true) // effective query
        ).queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("b");
    }

    @Test
    public void should_query_with_sub_query() throws Exception {
        buildUser().name("a").save();
        Long userId = buildUser().name("b").save();

        buildOrder().userId(userId).save();

        List<User> result = new UserDao().where(field("id").in(subquery(Order.class, "userId"))).queryList();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("b");
    }

    @Test
    public void should_query_list_with_index() throws Exception {
        prepareUser("a");
        prepareUser("b");
        prepareUser("c");

        List<User> result = new UserDao().where().ascendingBy("name").queryList(1, 2);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("b");
        assertThat(result.get(1).getName()).isEqualTo("c");
    }
}
