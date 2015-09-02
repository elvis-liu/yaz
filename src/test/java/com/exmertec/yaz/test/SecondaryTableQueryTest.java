package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.field;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.UserOrder;

import org.junit.Test;

import java.util.List;

public class SecondaryTableQueryTest extends TestBase {
    @Test
    public void should_query_by_id() throws Exception {
        String userName = "name";
        Long id = buildUser().name(userName).save();

        UserOrder userOrder = new GenericDao<>(UserOrder.class).idEquals(id).querySingle();

        assertThat(userOrder.getUserName()).isEqualTo(userName);
        assertThat(userOrder.getOrderId()).isNull();
    }

    @Test
    public void should_query_by_secondary_table_columns() throws Exception {
        Long userAId = buildUser().name("a").save();
        buildOrder().userId(userAId).amount(10.0).save();
        Long userBId = buildUser().name("b").save();
        buildOrder().userId(userBId).amount(4.0).save();

        List<UserOrder> resultList = new GenericDao<>(UserOrder.class).where(field("orderAmount").gt(5.0)).queryList();

        assertThat(resultList.size()).isEqualTo(1);
    }
}
