package com.exmertec.yaz.core;

import java.util.Collection;

public interface QueryBuilder {
    Query eq(Object value);

    Query ne(Object value);

    Query like(String value);

    Query in(Object... values);

    Query in(Collection<Object> values);

    Query nin(Object... values);

    Query nin(Collection<Object> values);

    <T extends Comparable<? super T>> Query between(T value1, T value2);

    <T extends Comparable<? super T>> Query gt(T value);

    <T extends Number> Query gt(T number);

    <T extends Comparable<? super T>> Query lt(T value);

    <T extends Number> Query lt(T number);

    <T extends Comparable<? super T>> Query gte(T value);

    <T extends Number> Query gte(T number);

    <T extends Comparable<? super T>> Query lte(T value);

    <T extends Number> Query lte(T number);
}
