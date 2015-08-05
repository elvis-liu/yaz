package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.BasicCommandBuilder;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public final class IdEqualsCommandBuilder<T> implements BasicCommandBuilder {
    private final EntityManager entityManager;
    private final Class<T> protoType;
    private Object id;
    private LockModeType lockModeType;

    public IdEqualsCommandBuilder(EntityManager entityManager, Class<T> protoType, Object id) {
        this.entityManager = entityManager;
        this.protoType = protoType;
        this.id = id;
    }

    @Override
    public BasicCommandBuilder lockBy(LockModeType lockModeType) {
        this.lockModeType = lockModeType;
        return this;
    }

    @Override
    public T querySingle() {
        if (lockModeType != null) {
            return entityManager.find(protoType, id, lockModeType);
        } else {
            return entityManager.find(protoType, id);
        }
    }
}
