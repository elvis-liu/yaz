package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.User;
import com.exmertec.yaz.model.UserType;

import org.junit.Test;

import java.util.List;

public class SimpleUpdateTest extends TestBase {
    private static final Integer DEFAULT_USER_POINTS = 10;
    private static final Integer UPDATED_USER_POINTS = 5;

    @Test
    public void should_success_update_all_data_when_without_any_filters() throws Exception {
        buildUser().name("a").points(DEFAULT_USER_POINTS).save();
        buildUser().name("b").points(DEFAULT_USER_POINTS).save();
        buildUser().name("c").points(DEFAULT_USER_POINTS).save();
        buildUser().name("d").points(DEFAULT_USER_POINTS).save();


        UserDao userDao = new UserDao();
        assertThat(userDao.where().update("points", UPDATED_USER_POINTS).apply(true)).isEqualTo(4);
        List<User> users = userDao.where().queryList();
        assertThat(users).hasSize(4);
        users.stream().forEach(user -> assertThat(user.getPoints()).isEqualTo(UPDATED_USER_POINTS));
    }

    @Test
    public void should_success_update_special_data_when_use_filter() throws Exception {
        buildUser().name("a").points(DEFAULT_USER_POINTS).save();
        buildUser().name("b").points(DEFAULT_USER_POINTS).save();

        UserDao userDao = new UserDao();
        assertThat(userDao.where(field("name").eq("a"))
            .update("points", UPDATED_USER_POINTS)
            .apply(true)).isEqualTo(1);
        List<User> users = userDao.where(field("points").eq(DEFAULT_USER_POINTS)).queryList();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("b");

        users = userDao.where(field("points").eq(UPDATED_USER_POINTS)).queryList();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("a");
    }

    @Test
    public void should_succcess_update_multi_fields_when_invoke_multi_update() throws Exception {
        buildUser().name("a").points(DEFAULT_USER_POINTS).type(UserType.NORMAL).save();
        buildUser().name("b").points(DEFAULT_USER_POINTS).type(UserType.NORMAL).save();
        buildUser().name("c").points(DEFAULT_USER_POINTS).type(UserType.NORMAL).save();
        buildUser().name("d").points(DEFAULT_USER_POINTS).type(UserType.NORMAL).save();

        UserDao userDao = new UserDao();
        assertThat(userDao.where().update("points", UPDATED_USER_POINTS)
            .update("type", UserType.VIP).apply(true)).isEqualTo(4);

        List<User> users = userDao.where().queryList();
        assertThat(users).hasSize(4);
        users.stream().forEach(user -> {
            assertThat(user.getPoints()).isEqualTo(UPDATED_USER_POINTS);
            assertThat(user.getType()).isEqualTo(UserType.VIP);
        });
    }
}
