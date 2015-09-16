package com.exmertec.yaz.core;

import java.util.List;

public interface SelectionBuilder {
    Long distinctCount();

    Double avg();

    <T extends Number> T min(Class<T> targetType);

    <T extends Number> T max(Class<T> targetType);

    <T extends Number> T sum(Class<T> targetType);

    <T> T querySingle(Class<T> targetType);

    <T> T queryFirst(Class<T> targetType);

    <T> List<T> queryList(Class<T> targetType);

    <T> List<T> queryList(Class<T> targetType, int startIndex, int size);

    <T> List<T> queryPage(Class<T> targetType, int pageSize, int pageIndex);
}
