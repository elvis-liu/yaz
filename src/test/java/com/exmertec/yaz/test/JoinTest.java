package com.exmertec.yaz.test;

import static com.exmertec.yaz.BaseDao.joinBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.exmertec.yaz.model.UserOrder;

import org.junit.Test;

import java.util.List;

public class JoinTest extends TestBase {
    @Test
    public void should_query_entity_with_relation() throws Exception {
        Long userId = buildUser().name("a").save();
        builderOrder().amount(10.0).userId(userId).save();
        builderOrder().amount(5.0).userId(userId).save();

        List<UserOrder> resultList = new GenericDao<>(UserOrder.class).where(
            joinBy("orders").ofRelationField("amount").gt(6.0)).queryList();

        assertThat(resultList.size()).isEqualTo(1);
    }
}
