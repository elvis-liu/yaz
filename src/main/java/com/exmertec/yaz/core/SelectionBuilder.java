package com.exmertec.yaz.core;

public interface SelectionBuilder {
    Long distinctCount();

    Double avg();

    <T extends Number> T min(Class<T> targetType);

    <T extends Number> T max(Class<T> targetType);

    <T extends Number> T sum(Class<T> targetType);
}
