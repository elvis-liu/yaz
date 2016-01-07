package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.Query;
import com.exmertec.yaz.core.QueryBuilder;
import com.exmertec.yaz.query.ComparableBetweenQuery;
import com.exmertec.yaz.query.ComparableCompareQuery;
import com.exmertec.yaz.query.EmptyQuery;
import com.exmertec.yaz.query.EqualQuery;
import com.exmertec.yaz.query.InQuery;
import com.exmertec.yaz.query.LikeQuery;
import com.exmertec.yaz.query.NeverMatchQuery;
import com.exmertec.yaz.query.NinQuery;
import com.exmertec.yaz.query.NotEqualQuery;
import com.exmertec.yaz.query.NumberCompareQuery;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;

public class FieldQueryBuilder implements QueryBuilder {
    private final String fieldName;

    public FieldQueryBuilder(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public Query eq(Object value) {
        return new EqualQuery(fieldName, value);
    }

    @Override
    public Query ne(Object value) {
        return new NotEqualQuery(fieldName, value);
    }

    @Override
    public Query like(String value) {
        return new LikeQuery(fieldName, value, true);
    }

    @Override
    public Query likeLiterally(String value) {
        return new LikeQuery(fieldName, value, false);
    }

    @Override
    public Query in(Object... values) {
        if (values == null || values.length == 0) {
            // nothing can be in empty collection
            return new NeverMatchQuery();
        }

        return new InQuery(fieldName, values);
    }

    @Override
    public <T> Query in(Collection<T> values) {
        return in(values == null ? null : values.toArray());
    }

    @Override
    public Query nin(Object... values) {
        if (values == null || values.length == 0) {
            return new EmptyQuery();
        }

        return new NinQuery(fieldName, values);
    }

    @Override
    public <T> Query nin(Collection<T> values) {
        return nin(values == null ? null : values.toArray());
    }

    @Override
    public <T extends Comparable<? super T>> Query between(T value1, T value2) {
        return new ComparableBetweenQuery<>(fieldName, value1, value2);
    }

    @Override
    public <T extends Comparable<? super T>> Query gtComparable(T value) {
        return new ComparableCompareQuery<>(fieldName, value, CriteriaBuilder::greaterThan);
    }

    @Override
    public <T extends Number> Query gt(T number) {
        return new NumberCompareQuery<>(fieldName, number, CriteriaBuilder::gt);
    }

    @Override
    public <T extends Comparable<? super T>> Query ltComparable(T value) {
        return new ComparableCompareQuery<>(fieldName, value, CriteriaBuilder::lessThan);
    }

    @Override
    public <T extends Number> Query lt(T number) {
        return new NumberCompareQuery<>(fieldName, number, CriteriaBuilder::lt);
    }

    @Override
    public <T extends Comparable<? super T>> Query gteComparable(T value) {
        return new ComparableCompareQuery<>(fieldName, value, CriteriaBuilder::greaterThanOrEqualTo);
    }

    @Override
    public <T extends Number> Query gte(T number) {
        return new NumberCompareQuery<>(fieldName, number, CriteriaBuilder::ge);
    }

    @Override
    public <T extends Comparable<? super T>> Query lteComparable(T value) {
        return new ComparableCompareQuery<>(fieldName, value, CriteriaBuilder::lessThanOrEqualTo);
    }

    @Override
    public <T extends Number> Query lte(T number) {
        return new NumberCompareQuery<>(fieldName, number, CriteriaBuilder::le);
    }
}
