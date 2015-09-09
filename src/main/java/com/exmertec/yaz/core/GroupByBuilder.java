package com.exmertec.yaz.core;

import java.util.List;

import javax.persistence.Tuple;

public interface GroupByBuilder {
    AliasAssigner<GroupByBuilder> count(String field);

    AliasAssigner<GroupByBuilder> sum(String fields);

    List<Tuple> queryList();

    GroupByBuilder ascendingByAlias(String alias);

    GroupByBuilder descendingByAlias(String alias);
}
