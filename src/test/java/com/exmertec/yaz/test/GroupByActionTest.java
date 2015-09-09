package com.exmertec.yaz.test;

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
}
