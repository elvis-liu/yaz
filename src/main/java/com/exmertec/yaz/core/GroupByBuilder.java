package com.exmertec.yaz.core;

import java.util.List;

import javax.persistence.Tuple;

public interface GroupByBuilder {
    AliasAssigner<GroupByBuilder> count(String field);

    AliasAssigner<GroupByBuilder> sum(String field);

    AliasAssigner<GroupByBuilder> avg(String field);

    AliasAssigner<GroupByBuilder> max(String field);

    AliasAssigner<GroupByBuilder> min(String field);

    List<Tuple> queryList();

    List<Tuple> queryPage(int pageSize, int pageIndex);

    GroupByBuilder ascendingByAlias(String alias);

    GroupByBuilder descendingByAlias(String alias);

}
