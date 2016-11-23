package com.exmertec.yaz.core;

import java.util.List;

import javax.persistence.LockModeType;

public interface AdvancedCommandBuilder<T> extends BasicCommandBuilder<T>, QueryCommandBuilder<T> {
    // options
    AdvancedCommandBuilder<T> ascendingBy(String... fieldNames);

    AdvancedCommandBuilder<T> descendingBy(String... fieldNames);

    @Override
    AdvancedCommandBuilder<T> lockBy(LockModeType lockModeType);

    // actions
    Long count();

    T queryFirst();

    List<T> queryPage(int pageSize, int pageIndex);

    List<T> queryList();

    List<T> queryList(int startIndex, int size);

    // query
    @Override
    AdvancedCommandBuilder<T> where(Query... queries);
}
