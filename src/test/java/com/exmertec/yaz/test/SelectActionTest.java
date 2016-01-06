package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class SelectActionTest extends TestBase {
    @Test
    public void should_distinct_count() throws Exception {
        prepareUser("a");
        prepareUser("a");
        prepareUser("b");

        Long count = new UserDao().where(field("name").fullFuzzyLike("a")).select("name").distinctCount();

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

    @Test
    public void should_query_single_with_select() throws Exception {
        buildUser().name("abc").save();

        String name = new UserDao().where(field("name").eq("abc")).select("name").querySingle(String.class);

        assertThat(name).isEqualTo("abc");
    }

    @Test
    public void should_query_list_with_select() throws Exception {
        buildUser().name("c").save();
        buildUser().name("a").save();
        buildUser().name("b").save();

        List<String> names = new UserDao().where().ascendingBy("name").select("name").queryList(String.class);

        assertThat(names.size()).isEqualTo(3);
        assertThat(names.get(0)).isEqualTo("a");
        assertThat(names.get(1)).isEqualTo("b");
        assertThat(names.get(2)).isEqualTo("c");
    }

    @Test
    public void should_query_page_with_select() throws Exception {
        buildUser().name("c").save();
        buildUser().name("a").save();
        buildUser().name("b").save();

        List<String> names = new UserDao().where().ascendingBy("name").select("name").queryPage(String.class, 2, 1);

        assertThat(names.size()).isEqualTo(1);
        assertThat(names.get(0)).isEqualTo("c");
    }

    @Test
    public void should_query_first_with_select() throws Exception {
        buildUser().name("c").save();
        buildUser().name("a").save();
        buildUser().name("b").save();

        String name = new UserDao().where().ascendingBy("name").select("name").queryFirst(String.class);

        assertThat(name).isEqualTo("a");
    }

    @Test
    public void should_query_earliest_date() throws Exception {
        buildOrder().timeCreated(Date.from(Instant.ofEpochSecond(1000))).save();
        buildOrder().timeCreated(Date.from(Instant.ofEpochSecond(2000))).save();
        buildOrder().timeCreated(Date.from(Instant.ofEpochSecond(3000))).save();

        Date time = new OrderDao().where().select("timeCreated").minComparable(Date.class);
        assertThat(time.toInstant().getEpochSecond()).isEqualTo(1000);
    }

    @Test
    public void should_query_latest_date() throws Exception {
        buildOrder().timeCreated(Date.from(Instant.ofEpochSecond(1000))).save();
        buildOrder().timeCreated(Date.from(Instant.ofEpochSecond(2000))).save();
        buildOrder().timeCreated(Date.from(Instant.ofEpochSecond(3000))).save();

        Date time = new OrderDao().where().select("timeCreated").maxComparable(Date.class);
        assertThat(time.toInstant().getEpochSecond()).isEqualTo(3000);
    }
}
