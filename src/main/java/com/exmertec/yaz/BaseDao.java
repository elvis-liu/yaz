package com.exmertec.yaz;

import com.exmertec.yaz.builder.CoreCommandBuilder;
import com.exmertec.yaz.builder.FieldQueryBuilder;
import com.exmertec.yaz.builder.IdEqualsCommandBuilder;
import com.exmertec.yaz.core.AdvancedCommandBuilder;
import com.exmertec.yaz.core.BasicCommandBuilder;
import com.exmertec.yaz.core.Query;
import com.exmertec.yaz.core.QueryBuilder;
import com.exmertec.yaz.core.ThreadLocalEntityManagerHelper;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;

public abstract class BaseDao<T> {
    private static final Logger LOG = Logger.getLogger(BaseDao.class);

    protected final EntityManager entityManager;

    private final Class<T> prototype;

    public BaseDao(EntityManager entityManager, Class<T> prototype) {
        this.entityManager = entityManager;
        this.prototype = prototype;

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
        return new IdEqualsCommandBuilder<>(entityManager, prototype, id);
    }

    public AdvancedCommandBuilder<T> with(Query... queries) {
        return new CoreCommandBuilder<>(entityManager, prototype).with(queries);
    }

    public static QueryBuilder field(String fieldName) {
        return new FieldQueryBuilder(fieldName);
    }
}