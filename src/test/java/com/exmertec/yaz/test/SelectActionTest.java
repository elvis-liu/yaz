package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class SelectActionTest extends TestBase {
    @Test
    public void should_count() throws Exception {
        prepareUser("a");
        prepareUser("a");
        prepareUser("b");

        Long count = new UserDao().where(field("name").like("a")).select().count("name");

        assertThat(count).isEqualTo(2);
    }
}
