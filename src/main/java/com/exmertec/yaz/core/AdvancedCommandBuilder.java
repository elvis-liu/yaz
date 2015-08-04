package com.exmertec.yaz.core;

import java.util.List;

import javax.persistence.LockModeType;

public interface AdvancedCommandBuilder<T> extends BasicCommandBuilder<T> {
    // options
    AdvancedCommandBuilder<T> ascendingBy(String... fieldNames);

    AdvancedCommandBuilder<T> descendingBy(String... fieldNames);

    @Override
    AdvancedCommandBuilder<T> lockBy(LockModeType lockModeType);

    // actions
    Long count();

    Long distinctCount(String fieldName);

    T queryFirst();

    List<T> queryPage(int pageSize, int pageIndex);

    List<T> queryList();

    // queries
    AdvancedCommandBuilder<T> with(Query... queries);

    AdvancedCommandBuilder<T> and(Query... queries);

    AdvancedCommandBuilder<T> or(Query... queries);
}
