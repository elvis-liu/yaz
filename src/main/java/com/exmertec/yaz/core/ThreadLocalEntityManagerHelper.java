package com.exmertec.yaz.core;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

public class ThreadLocalEntityManagerHelper {
    private static final Logger LOG = Logger.getLogger(ThreadLocalEntityManagerHelper.class);

    private static final ThreadLocal<EntityManager> threadLocal;

    static {
        threadLocal = new ThreadLocal<>();
    }

    public static void setEntityManager(EntityManager entityManager) {
        threadLocal.set(entityManager);
    }

    public static EntityManager getEntityManager() {
        EntityManager entityManager = threadLocal.get();

        if (entityManager == null) {
            LOG.error("Get entity manager, but was not set yet");
            throw new IllegalStateException();
        }

        return entityManager;
    }

    public static CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }
}
