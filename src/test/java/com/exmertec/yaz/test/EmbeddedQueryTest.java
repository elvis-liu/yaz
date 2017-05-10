package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.embedded;
import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.Address;
import com.exmertec.yaz.model.Contact;
import com.exmertec.yaz.model.User;

import org.junit.Test;

import java.util.List;

public class EmbeddedQueryTest extends TestBase {
    @Test
    public void should_query_by_embedded_field() throws Exception {
        buildUser().name("a").contact(new Contact("a@test.com", null)).save();
        buildUser().name("b").contact(new Contact("b@test.com", null)).save();

        List<User> users = new UserDao().where(
            embedded("contact").field("email").eq("a@test.com")
        ).queryList();

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getName()).isEqualTo("a");
        assertThat(users.get(0).getContact().getEmail()).isEqualTo("a@test.com");
    }

    @Test
    public void should_query_by_2_levels_embedded_field() throws Exception {
        buildUser().name("a").contact(new Contact("a@test.com", new Address("city_a"))).save();
        buildUser().name("b").contact(new Contact("b@test.com", new Address("city_b"))).save();

        List<User> users = new UserDao().where(
            embedded("contact").embedded("address").field("city").eq("city_b")
        ).queryList();

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getName()).isEqualTo("b");
        assertThat(users.get(0).getContact().getEmail()).isEqualTo("b@test.com");
        assertThat(users.get(0).getContact().getAddress().getCity()).isEqualTo("city_b");
    }

    @Test
    public void should_query_with_mixed_conditions() throws Exception {
        buildUser().name("a").contact(new Contact("a@test.com", new Address("city_a"))).save();
        buildUser().name("b").contact(new Contact("b@test.com", new Address("city_b"))).save();
        buildUser().name("c").contact(new Contact("c@test.com", new Address("city_b"))).save();

        List<User> users = new UserDao().where(
            embedded("contact").embedded("address").field("city").eq("city_b"),
            field("name").eq("c")
        ).queryList();

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getName()).isEqualTo("c");
        assertThat(users.get(0).getContact().getEmail()).isEqualTo("c@test.com");
        assertThat(users.get(0).getContact().getAddress().getCity()).isEqualTo("city_b");
    }
}
