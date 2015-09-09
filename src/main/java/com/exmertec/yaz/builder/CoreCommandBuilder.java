package com.exmertec.yaz.builder;

import static java.util.stream.Collectors.toList;

import com.exmertec.yaz.core.AdvancedCommandBuilder;
import com.exmertec.yaz.core.DistinctSelectionBuilder;
import com.exmertec.yaz.core.GroupByBuilder;
import com.exmertec.yaz.core.Query;
import com.exmertec.yaz.core.SelectionBuilder;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;

public class CoreCommandBuilder<T> implements AdvancedCommandBuilder<T>, CriteriaQueryGenerator<T> {
    private static final Logger LOG = Logger.getLogger(CoreCommandBuilder.class);

    private final EntityManager entityManager;
    private final Class<T> prototype;

    private final List<Query> addedQueries = new LinkedList<>();

    private LockModeType lockModeType;

    private List<OrderByRule> orderByRules = new LinkedList<>();

    public CoreCommandBuilder(EntityManager entityManager, Class<T> prototype) {
        this.entityManager = entityManager;
        this.prototype = prototype;
    }

    @Override
    public AdvancedCommandBuilder<T> ascendingBy(String... fieldNames) {
        orderByRules.addAll(Arrays.asList(fieldNames).stream()
                                .map(name -> new OrderByRule(true, name))
                                .collect(toList()));
        return this;
    }

    @Override
    public AdvancedCommandBuilder<T> descendingBy(String... fieldNames) {
        orderByRules.addAll(Arrays.asList(fieldNames).stream()
                                .map(name -> new OrderByRule(false, name))
                                .collect(toList()));
        return this;
    }

    @Override
    public AdvancedCommandBuilder<T> lockBy(LockModeType lockModeType) {
        this.lockModeType = lockModeType;
        return this;
    }

    @Override
    public Long count() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        return doQuerySingleForType(Long.class, criteriaBuilder::count);
    }

    @Override
    public T queryFirst() {
        List<T> resultList = doQueryList(query -> query.setMaxResults(1));

        if (resultList.isEmpty()) {
            LOG.info("Failed to get first result of " + prototype.getName());
            return null;
        } else {
            return resultList.get(0);
        }
    }

    @Override
    public List<T> queryPage(int pageSize, int pageIndex) {
        List<T> resultList = doQueryList(query -> query.setMaxResults(pageSize).setFirstResult(pageIndex * pageSize));

        if (resultList.isEmpty()) {
            LOG.info(String.format("Failed to find entity of %s, of page (%d, %d)",
                                   prototype.getName(), pageSize, pageIndex));
        }

        return resultList;
    }

    @Override
    public List<T> queryList() {
        List<T> resultList = doQueryList(null);

        if (resultList.isEmpty()) {
            LOG.info("Failed to find any result of " + prototype.getName());
        }

        return resultList;
    }

    @Override
    public T querySingle() {
        List<T> resultList = doQueryList(query -> query.setMaxResults(2));

        if (resultList.isEmpty()) {
            LOG.info("Failed to get single result of " + prototype.getName());
            return null;
        } else if (resultList.size() > 1) {
            LOG.warn("Find more than one result with single query of " + prototype.getName());
            throw new IllegalStateException();
        } else {
            return resultList.get(0);
        }
    }

    @Override
    public AdvancedCommandBuilder<T> where(Query... queries) {
        addedQueries.addAll(Arrays.asList(queries));
        return this;
    }

    @Override
    public SelectionBuilder select(String fieldName) {
        return new CoreSelectionBuilder<>(this, fieldName);
    }

    @Override
    public DistinctSelectionBuilder distinctSelect(String fieldName) {
        return new CoreDistinctSelectionBuilder<>(this, fieldName);
    }

    @Override
    public GroupByBuilder groupBy(String fieldName) {
        return new CoreGroupByBuilder<>(this, fieldName);
    }

    @Override
    public Class<T> getProtoType() {
        return prototype;
    }

    @Override
    public LockModeType getLockMode() {
        return lockModeType;
    }

    @Override
    public List<OrderByRule> getOrderByRules() {
        return orderByRules;
    }

    @Override
    public List<Query> getQueries() {
        return addedQueries;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
