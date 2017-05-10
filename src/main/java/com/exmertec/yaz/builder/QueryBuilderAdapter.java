package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.Query;
import com.exmertec.yaz.core.QueryBuilder;

import java.util.Collection;

public abstract class QueryBuilderAdapter implements QueryBuilder {
    private QueryBuilder delegate;

    protected QueryBuilderAdapter(QueryBuilder delegate) {
        this.delegate = delegate;
    }

    protected abstract Query wrap(Query query);

    @Override
    public Query eq(Object value) {
        return wrap(delegate.eq(value));
    }

    @Override
    public Query ne(Object value) {
        return wrap(delegate.ne(value));
    }

    @Override
    public Query like(String value) {
        return wrap(delegate.like(value));
    }

    @Override
    public Query likeLiterally(String value) {
        return wrap(delegate.likeLiterally(value));
    }

    @Override
    public Query in(Object... values) {
        return wrap(delegate.in(values));
    }

    @Override
    public <T> Query in(Collection<T> values) {
        return wrap(delegate.in(values));
    }

    @Override
    public Query nin(Object... values) {
        return wrap(delegate.nin(values));
    }

    @Override
    public <T> Query nin(Collection<T> values) {
        return wrap(delegate.nin(values));
    }

    @Override
    public <T extends Comparable<? super T>> Query between(T value1, T value2) {
        return wrap(delegate.between(value1, value2));
    }

    @Override
    public <T extends Comparable<? super T>> Query gtComparable(T value) {
        return wrap(delegate.gtComparable(value));
    }

    @Override
    public <T extends Number> Query gt(T number) {
        return wrap(delegate.gt(number));
    }

    @Override
    public <T extends Comparable<? super T>> Query ltComparable(T value) {
        return wrap(delegate.ltComparable(value));
    }

    @Override
    public <T extends Number> Query lt(T number) {
        return wrap(delegate.lt(number));
    }

    @Override
    public <T extends Comparable<? super T>> Query gteComparable(T value) {
        return wrap(delegate.gteComparable(value));
    }

    @Override
    public <T extends Number> Query gte(T number) {
        return wrap(delegate.gte(number));
    }

    @Override
    public <T extends Comparable<? super T>> Query lteComparable(T value) {
        return wrap(delegate.lteComparable(value));
    }

    @Override
    public <T extends Number> Query lte(T number) {
        return wrap(delegate.lte(number));
    }
}
