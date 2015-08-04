package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class SelectActionTest extends TestBase {
    @Test
    public void should_distinct_count() throws Exception {
        prepareUser("a");
        prepareUser("a");
        prepareUser("b");

        Long count = new UserDao().where(field("name").like("a")).select("name").distinctCount();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void should_accept_avg() throws Exception {
        buildUser().points(3).save();
        buildUser().points(5).save();

        Double avg = new UserDao().where(field("points").ne(null)).select("points").avg();

        assertThat(avg).isEqualTo(4);
    }

    @Test
    public void should_accept_min() throws Exception {
        buildUser().points(3).save();
        buildUser().points(5).save();
        buildUser().points(2).save();

        Integer min = new UserDao().where(field("points").ne(null)).select("points").min(Integer.class);

        assertThat(min).isEqualTo(2);
    }

    @Test
    public void should_accept_max() throws Exception {
        buildUser().points(3).save();
        buildUser().points(5).save();
        buildUser().points(2).save();

        Integer max = new UserDao().where(field("points").ne(null)).select("points").max(Integer.class);

        assertThat(max).isEqualTo(5);
    }

    @Test
    public void should_accept_sum() throws Exception {
        buildUser().points(3).save();
        buildUser().points(5).save();
        buildUser().points(2).save();

        Integer sum = new UserDao().where(field("points").ne(null)).select("points").sum(Integer.class);

        assertThat(sum).isEqualTo(10);
    }
}
