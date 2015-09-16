package com.exmertec.yaz.core;

import java.util.List;

public interface DistinctSelectionBuilder {
    Long count();

    <T> T querySingle(Class<T> targetType);

    <T> T queryFirst(Class<T> targetType);

    <T> List<T> queryList(Class<T> targetType);

    <T> List<T> queryList(Class<T> targetType, int startIndex, int size);

    <T> List<T> queryPage(Class<T> targetType, int pageSize, int pageIndex);
}
