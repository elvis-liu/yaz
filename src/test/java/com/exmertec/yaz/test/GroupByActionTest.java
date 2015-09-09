package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.UserType;

import org.junit.Test;

import java.util.List;

import javax.persistence.Tuple;

public class GroupByActionTest extends TestBase {
    @Test
    public void should_count_with_group_by() throws Exception {
        buildUser().name("a").save();
        buildUser().name("a").save();
        buildUser().name("b").save();

        List<Tuple> resultList = new UserDao().where().groupBy("name").count("name").as("nameCount").queryList();

        assertThat(resultList.size()).isEqualTo(2);
    }

    @Test
    public void should_group_by_with_where_restrictions() throws Exception {
        buildUser().name("a").save();
        buildUser().name("a").save();
        buildUser().name("b").save();

        List<Tuple> resultList = new UserDao().where(field("name").ne("b")).groupBy("name").count("name").as("nameCount").queryList();

        assertThat(resultList.size()).isEqualTo(1);
        assertThat(resultList.get(0).get("nameCount")).isEqualTo(2L);
    }

    @Test
    public void should_provide_group_by_field_automatically() throws Exception {
        buildUser().name("a").save();
        buildUser().name("a").save();
        buildUser().name("b").save();

        List<Tuple> resultList = new UserDao().where(field("name").ne("b")).groupBy("name").count("name").as("nameCount").queryList();

        assertThat(resultList.size()).isEqualTo(1);
        assertThat(resultList.get(0).get("name")).isEqualTo("a");
        assertThat(resultList.get(0).get("nameCount")).isEqualTo(2L);
    }

    @Test
    public void should_allow_order_by_when_group_by() throws Exception {
        buildUser().name("c").save();
        buildUser().name("a").save();
        buildUser().name("c").save();
        buildUser().name("b").save();
        buildUser().name("c").save();
        buildUser().name("a").save();

        List<Tuple> resultList = new UserDao().where()
                .descendingBy("name")
                .groupBy("name")
                .count("name").as("nameCount")
                .queryList();

        assertThat(resultList.size()).isEqualTo(3);
        assertThat(resultList.get(0).get("name")).isEqualTo("c");
        assertThat(resultList.get(0).get("nameCount")).isEqualTo(3L);
        assertThat(resultList.get(1).get("name")).isEqualTo("b");
        assertThat(resultList.get(1).get("nameCount")).isEqualTo(1L);
        assertThat(resultList.get(2).get("name")).isEqualTo("a");
        assertThat(resultList.get(2).get("nameCount")).isEqualTo(2L);
    }

    @Test
    public void should_support_sum_after_use_group_by() throws Exception {
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(1).save();
        buildUser().name("c").points(1).save();
        buildUser().name("b").points(1).save();
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(1).save();

        List<Tuple> results = new UserDao().where()
                .descendingBy("name")
                .groupBy("name")
                .sum("points").as("sumPoints")
                .queryList();

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0).get("sumPoints")).isEqualTo(3L);
        assertThat(results.get(1).get("sumPoints")).isEqualTo(1L);
        assertThat(results.get(2).get("sumPoints")).isEqualTo(2L);
    }

    @Test
    public void should_support_multi_group_by_fields() throws Exception {
        buildUser().name("a").type(UserType.NORMAL).points(1).save();
        buildUser().name("b").type(UserType.NORMAL).points(1).save();
        buildUser().name("c").type(UserType.NORMAL).points(1).save();
        buildUser().name("a").type(UserType.NORMAL).points(1).save();
        buildUser().name("b").type(UserType.VIP).points(1).save();
        buildUser().name("c").type(UserType.VIP).points(1).save();

        List<Tuple> results = new UserDao().where().groupBy("name", "type")
                .sum("points").as("sumPoints")
                .queryList();

        assertThat(results.size()).isEqualTo(5);
        for (Tuple tuple : results) {
            if (tuple.get("name").equals("a") && tuple.get("type").equals(UserType.NORMAL)) {
                assertThat(tuple.get("sumPoints")).isEqualTo(2L);
            } else {
                assertThat(tuple.get("sumPoints")).isEqualTo(1L);
            }
        }
    }

    @Test
    public void should_support_ascending_by_alias() throws Exception {
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(1).save();
        buildUser().name("c").points(1).save();
        buildUser().name("b").points(1).save();
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(1).save();

        List<Tuple> results = new UserDao().where()
            .groupBy("name")
            .sum("points").as("sumPoints")
            .ascendingByAlias("sumPoints")
            .queryList();

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0).get("sumPoints")).isEqualTo(1L);
        assertThat(results.get(0).get("name")).isEqualTo("b");
        assertThat(results.get(1).get("sumPoints")).isEqualTo(2L);
        assertThat(results.get(1).get("name")).isEqualTo("a");
        assertThat(results.get(2).get("sumPoints")).isEqualTo(3L);
        assertThat(results.get(2).get("name")).isEqualTo("c");
    }

    @Test
    public void should_support_descending_by_alias() throws Exception {
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(1).save();
        buildUser().name("c").points(1).save();
        buildUser().name("b").points(1).save();
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(1).save();

        List<Tuple> results = new UserDao().where()
            .groupBy("name")
            .sum("points").as("sumPoints")
            .descendingByAlias("sumPoints")
            .queryList();

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0).get("sumPoints")).isEqualTo(3L);
        assertThat(results.get(0).get("name")).isEqualTo("c");
        assertThat(results.get(1).get("sumPoints")).isEqualTo(2L);
        assertThat(results.get(1).get("name")).isEqualTo("a");
        assertThat(results.get(2).get("sumPoints")).isEqualTo(1L);
        assertThat(results.get(2).get("name")).isEqualTo("b");
    }

    @Test
    public void should_support_avg_when_group_by() throws Exception {
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(2).save();
        buildUser().name("c").points(1).save();
        buildUser().name("b").points(3).save();
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(2).save();

        List<Tuple> results = new UserDao().where()
            .groupBy("name")
            .avg("points").as("avgPoints")
            .descendingByAlias("avgPoints")
            .queryList();

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0).get("avgPoints")).isEqualTo(3.0);
        assertThat(results.get(0).get("name")).isEqualTo("b");
        assertThat(results.get(1).get("avgPoints")).isEqualTo(2.0);
        assertThat(results.get(1).get("name")).isEqualTo("a");
        assertThat(results.get(2).get("avgPoints")).isEqualTo(1.0);
        assertThat(results.get(2).get("name")).isEqualTo("c");
    }

    @Test
    public void should_support_max_when_group_by() throws Exception {
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(5).save();
        buildUser().name("c").points(2).save();
        buildUser().name("b").points(6).save();
        buildUser().name("c").points(3).save();
        buildUser().name("a").points(4).save();

        List<Tuple> results = new UserDao().where()
            .groupBy("name")
            .max("points").as("maxPoints")
            .descendingByAlias("maxPoints")
            .queryList();

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0).get("maxPoints")).isEqualTo(6);
        assertThat(results.get(0).get("name")).isEqualTo("b");
        assertThat(results.get(1).get("maxPoints")).isEqualTo(5);
        assertThat(results.get(1).get("name")).isEqualTo("a");
        assertThat(results.get(2).get("maxPoints")).isEqualTo(3);
        assertThat(results.get(2).get("name")).isEqualTo("c");
    }

    @Test
    public void should_support_min_when_group_by() throws Exception {
        buildUser().name("c").points(1).save();
        buildUser().name("a").points(5).save();
        buildUser().name("c").points(2).save();
        buildUser().name("b").points(6).save();
        buildUser().name("c").points(3).save();
        buildUser().name("a").points(4).save();

        List<Tuple> results = new UserDao().where()
            .groupBy("name")
            .min("points").as("minPoints")
            .descendingByAlias("minPoints")
            .queryList();

        assertThat(results.size()).isEqualTo(3);
        assertThat(results.get(0).get("minPoints")).isEqualTo(6);
        assertThat(results.get(0).get("name")).isEqualTo("b");
        assertThat(results.get(1).get("minPoints")).isEqualTo(4);
        assertThat(results.get(1).get("name")).isEqualTo("a");
        assertThat(results.get(2).get("minPoints")).isEqualTo(1);
        assertThat(results.get(2).get("name")).isEqualTo("c");
    }
}
