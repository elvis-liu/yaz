package com.exmertec.yaz.core;

import java.util.Collection;

public interface QueryBuilder {
    Query eq(Object value);

    Query ne(Object value);

    Query fullFuzzyLike(String value);
    Query like(String value);

    Query in(Object... values);

    <T> Query in(Collection<T> values);

    Query nin(Object... values);

    <T> Query nin(Collection<T> values);

    <T extends Comparable<? super T>> Query between(T value1, T value2);

    <T extends Comparable<? super T>> Query gtComparable(T value);

    <T extends Number> Query gt(T number);

    <T extends Comparable<? super T>> Query ltComparable(T value);

    <T extends Number> Query lt(T number);

    <T extends Comparable<? super T>> Query gteComparable(T value);

    <T extends Number> Query gte(T number);

    <T extends Comparable<? super T>> Query lteComparable(T value);

    <T extends Number> Query lte(T number);
}
