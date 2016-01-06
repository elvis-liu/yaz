package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.util.List;

public class DistinctSelectActionTest extends TestBase {
    @Test
    public void should_distinct_count() throws Exception {
        prepareUser("a");
        prepareUser("a");
        prepareUser("b");

        Long count = new UserDao().where(field("name").fullFuzzyLike("a")).distinctSelect("name").count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void should_query_single_with_select() throws Exception {
        buildUser().name("abc").save();
        buildUser().name("abc").save();

        String name = new UserDao().where(field("name").eq("abc")).distinctSelect("name").querySingle(String.class);

        assertThat(name).isEqualTo("abc");
    }

    @Test
    public void should_query_list_with_select() throws Exception {
        buildUser().name("c").save();
        buildUser().name("c").save();
        buildUser().name("a").save();
        buildUser().name("b").save();
        buildUser().name("b").save();

        List<String> names = new UserDao().where().ascendingBy("name").distinctSelect("name").queryList(String.class);

        assertThat(names.size()).isEqualTo(3);
        assertThat(names.get(0)).isEqualTo("a");
        assertThat(names.get(1)).isEqualTo("b");
        assertThat(names.get(2)).isEqualTo("c");
    }

    @Test
    public void should_query_page_with_select() throws Exception {
        buildUser().name("c").save();
        buildUser().name("c").save();
        buildUser().name("a").save();
        buildUser().name("b").save();
        buildUser().name("b").save();

        List<String> names = new UserDao().where().ascendingBy("name").distinctSelect("name").queryPage(String.class, 2,
                                                                                                        1);

        assertThat(names.size()).isEqualTo(1);
        assertThat(names.get(0)).isEqualTo("c");
    }

    @Test
    public void should_query_first_with_select() throws Exception {
        buildUser().name("c").save();
        buildUser().name("c").save();
        buildUser().name("a").save();
        buildUser().name("b").save();
        buildUser().name("b").save();

        String name = new UserDao().where().ascendingBy("name").distinctSelect("name").queryFirst(String.class);

        assertThat(name).isEqualTo("a");
    }
}
