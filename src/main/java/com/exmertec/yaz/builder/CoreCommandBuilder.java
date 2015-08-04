package com.exmertec.yaz.builder;

import static java.util.stream.Collectors.toList;

import com.exmertec.yaz.core.AdvancedCommandBuilder;
import com.exmertec.yaz.core.Query;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public class CoreCommandBuilder<T> implements AdvancedCommandBuilder<T> {
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
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        return doQuerySingleForType(Long.class, criteriaBuilder::count);
    }

    @Override
    public Long distinctCount(String fieldName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        return doQuerySingleForType(Long.class, root -> criteriaBuilder.countDistinct(root.get(fieldName)));
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

    private Root<T> getRoot(CriteriaQuery<?> criteriaQuery) {
        Set<Root<?>> roots = criteriaQuery.getRoots();
        if (roots.size() != 1) {
            throw new IllegalStateException("Query contains more than one root entity!");
        }

        return (Root<T>) roots.iterator().next();
    }

    private <R> CriteriaQuery<R> createQuery(Class<R> resultType) {
        CriteriaQuery<R> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(resultType);
        criteriaQuery.from(prototype);

        return criteriaQuery;
    }

    private CriteriaQuery<T> createQueryCriteriaByAttributes() {
        CriteriaQuery<T> criteriaQuery = createQuery(prototype);
        criteriaQuery.select(getRoot(criteriaQuery));

        return criteriaQuery;
    }

    private Predicate[] generateRestrictions(AbstractQuery<?> abstractQuery, List<Query> queries) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        return queries.stream()
            .map(query -> Optional.of(query.toRestrictions(criteriaBuilder, abstractQuery)).get())
            .flatMap(restrictions -> restrictions.stream())
            .toArray(Predicate[]::new);
    }

    private void addQueryOptions(TypedQuery<?> query) {
        if (lockModeType != null) {
            query.setLockMode(lockModeType);
        }
    }

    private <R> R doQuerySingleForType(Class<R> resultType, Function<Root<T>, Selection<R>> selectionFunction) {
        CriteriaQuery<R> criteriaQuery = createQuery(resultType);

        criteriaQuery.select(selectionFunction.apply(getRoot(criteriaQuery)));

        Predicate[] generatedRestrictions = generateRestrictions(criteriaQuery, addedQueries);

        criteriaQuery.where(generatedRestrictions);
        TypedQuery<R> query = entityManager.createQuery(criteriaQuery);
        addQueryOptions(query);

        return query.getSingleResult();
    }

    private List<T> doQueryList(Consumer<TypedQuery<T>> queryManipulator) {
        CriteriaQuery<T> criteria = createQueryCriteriaByAttributes();
        appendOrderBy(entityManager.getCriteriaBuilder(), criteria);

        Predicate[] generatedRestrictions = generateRestrictions(criteria, addedQueries);

        criteria.where(generatedRestrictions);

        TypedQuery<T> query = entityManager.createQuery(criteria);
        if (queryManipulator != null) {
            queryManipulator.accept(query);
        }
        addQueryOptions(query);

        return query.getResultList();
    }

    private void appendOrderBy(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteria) {
        final Root<?> entity = criteria.getRoots().iterator().next();

        if (!orderByRules.isEmpty()) {
            criteria.orderBy(orderByRules.stream()
                                 .map(rule -> rule.getOrder(criteriaBuilder, entity))
                                 .collect(toList()));
        }
    }

    private static class OrderByRule {
        private boolean isAscending;
        private String fieldName;

        public OrderByRule(boolean isAscending, String fieldName) {
            this.isAscending = isAscending;
            this.fieldName = fieldName;
        }

        public Order getOrder(CriteriaBuilder criteriaBuilder, Root<?> entity) {
            if (isAscending) {
                return criteriaBuilder.asc(entity.get(fieldName));
            } else {
                return criteriaBuilder.desc(entity.get(fieldName));
            }
        }
    }
}
