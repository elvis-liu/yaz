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

import javax.persistence.EntityManager;

public class FieldQueryBuilder implements QueryBuilder {
    private EntityManager entityManager;
    private String fieldName;

    public FieldQueryBuilder(EntityManager entityManager, String fieldName) {
        this.entityManager = entityManager;
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
        return new LikeQuery(fieldName, value);
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
    public Query nin(Object... values) {
        if (values == null || values.length == 0) {
            return new EmptyQuery();
        }

        return new NinQuery(fieldName, values);
    }

    @Override
    public <T extends Comparable<? super T>> Query between(T value1, T value2) {
        return new ComparableBetweenQuery<>(fieldName, value1, value2);
    }

    @Override
    public <T extends Comparable<? super T>> Query gt(T value) {
        return new ComparableCompareQuery<>(fieldName, value, entityManager.getCriteriaBuilder()::greaterThan);
    }

    @Override
    public <T extends Comparable<? super T>> Query lt(T value) {
        return new ComparableCompareQuery<>(fieldName, value, entityManager.getCriteriaBuilder()::lessThan);
    }

    @Override
    public <T extends Comparable<? super T>> Query gte(T value) {
        return new ComparableCompareQuery<>(fieldName, value, entityManager.getCriteriaBuilder()::greaterThanOrEqualTo);
    }

    @Override
    public <T extends Comparable<? super T>> Query lte(T value) {
        return new ComparableCompareQuery<>(fieldName, value, entityManager.getCriteriaBuilder()::lessThanOrEqualTo);
    }

    @Override
    public <T extends Number> Query gt(T number) {
        return new NumberCompareQuery<>(fieldName, number, entityManager.getCriteriaBuilder()::gt);
    }

    @Override
    public <T extends Number> Query lt(T number) {
        return new NumberCompareQuery<>(fieldName, number, entityManager.getCriteriaBuilder()::lt);
    }

    @Override
    public <T extends Number> Query gte(T number) {
        return new NumberCompareQuery<>(fieldName, number, entityManager.getCriteriaBuilder()::ge);
    }

    @Override
    public <T extends Number> Query lte(T number) {
        return new NumberCompareQuery<>(fieldName, number, entityManager.getCriteriaBuilder()::le);
    }
}
