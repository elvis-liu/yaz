package com.exmertec.yaz;

import com.exmertec.yaz.builder.CoreCommandBuilder;
import com.exmertec.yaz.builder.FieldQueryBuilder;
import com.exmertec.yaz.builder.IdEqualsCommandBuilder;
import com.exmertec.yaz.core.AdvancedCommandBuilder;
import com.exmertec.yaz.core.BasicCommandBuilder;
import com.exmertec.yaz.core.Query;
import com.exmertec.yaz.core.QueryBuilder;
import com.exmertec.yaz.core.ThreadLocalEntityManagerHelper;
import com.exmertec.yaz.expression.ExpressionGenerator;
import com.exmertec.yaz.expression.SubqueryExpressionGenerator;
import com.exmertec.yaz.query.BooleanQuery;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;

public abstract class BaseDao<T> {
    private static final Logger LOG = Logger.getLogger(BaseDao.class);

    protected EntityManager entityManager;

    private final Class<T> prototype;

    protected BaseDao(EntityManager entityManager, Class<T> prototype) {
        this.prototype = prototype;
        setEntityManager(entityManager);
    }

    protected BaseDao(Class<T> prototype) {
        this.prototype = prototype;
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        ThreadLocalEntityManagerHelper.setEntityManager(entityManager);
    }

    public void save(T entity) {
        entityManager.persist(entity);
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public void remove(T entity) {
        entityManager.remove(entity);
    }

    public BasicCommandBuilder<T> idEquals(Object id) {
        return new IdEqualsCommandBuilder<>(prototype, id);
    }

    public AdvancedCommandBuilder<T> where(Query... queries) {
        return new CoreCommandBuilder<>(prototype).where(queries);
    }

    public static QueryBuilder field(String fieldName) {
        return new FieldQueryBuilder(fieldName);
    }

    public static ExpressionGenerator subquery(Class<?> fromType, String targetField, Query... queries) {
        return SubqueryExpressionGenerator.subquery(fromType, targetField, queries);
    }

    public static Query and(Query... queries) {
        return new BooleanQuery(ThreadLocalEntityManagerHelper.getCriteriaBuilder()::and, queries);
    }

    public static Query or(Query... queries) {
        return new BooleanQuery(ThreadLocalEntityManagerHelper.getCriteriaBuilder()::or, queries);
    }
}