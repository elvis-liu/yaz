package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

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

        List<Tuple> resultList = new UserDao().where().descendingBy("name").groupBy("name").count("name").as("nameCount").queryList();

        assertThat(resultList.size()).isEqualTo(3);
        assertThat(resultList.get(0).get("name")).isEqualTo("c");
        assertThat(resultList.get(0).get("nameCount")).isEqualTo(3L);
        assertThat(resultList.get(1).get("name")).isEqualTo("b");
        assertThat(resultList.get(1).get("nameCount")).isEqualTo(1L);
        assertThat(resultList.get(2).get("name")).isEqualTo("a");
        assertThat(resultList.get(2).get("nameCount")).isEqualTo(2L);
    }
}
