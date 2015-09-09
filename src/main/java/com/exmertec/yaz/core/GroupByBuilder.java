package com.exmertec.yaz.core;

import java.util.List;

import javax.persistence.Tuple;

public interface GroupByBuilder {
    AliasAssigner<GroupByBuilder> count(String field);

    List<Tuple> queryList();
}
