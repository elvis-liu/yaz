package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.BasicCommandBuilder;
import com.exmertec.yaz.core.ThreadLocalEntityManagerHelper;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public final class IdEqualsCommandBuilder<T> implements BasicCommandBuilder {
    private final EntityManager entityManager;
    private final Class<T> protoType;
    private Object id;
    private LockModeType lockModeType;

    public IdEqualsCommandBuilder(Class<T> protoType, Object id) {
        this.protoType = protoType;
        this.id = id;
        this.entityManager = ThreadLocalEntityManagerHelper.getEntityManager();
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
