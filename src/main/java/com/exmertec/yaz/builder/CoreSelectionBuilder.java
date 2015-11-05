package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.SelectionBuilder;

import org.apache.log4j.Logger;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;

public class CoreSelectionBuilder<T> implements SelectionBuilder {
    private static final Logger LOG = Logger.getLogger(CoreSelectionBuilder.class);

    private final CriteriaQueryGenerator<T> criteriaQueryGenerator;
    private final String fieldName;

    public CoreSelectionBuilder(CriteriaQueryGenerator<T> criteriaQueryGenerator, String fieldName) {
        this.criteriaQueryGenerator = criteriaQueryGenerator;
        this.fieldName = fieldName;
    }

    @Override
    public Long distinctCount() {
        return criteriaQueryGenerator.doQuerySingleForType(Long.class,
                                                           root -> getCriteriaBuilder().countDistinct(
                                                               root.get(fieldName)));
    }

    @Override
    public Double avg() {
        return criteriaQueryGenerator.doQuerySingleForType(Double.class,
                                                           root -> getCriteriaBuilder().avg(
                                                               root.get(fieldName)));
    }

    @Override
    public <T extends Number> T min(Class<T> targetType) {
        return criteriaQueryGenerator.doQuerySingleForType(targetType,
                                                           root -> getCriteriaBuilder().min(
                                                               root.get(fieldName)));
    }

    @Override
    public <T extends Number> T max(Class<T> targetType) {
        return criteriaQueryGenerator.doQuerySingleForType(targetType,
                                                           root -> getCriteriaBuilder().max(
                                                               root.get(fieldName)));
    }

    @Override
    public <T extends Number> T sum(Class<T> targetType) {
        return criteriaQueryGenerator.doQuerySingleForType(targetType,
                                                           root -> getCriteriaBuilder().sum(
                                                               root.get(fieldName)));
    }

    @Override
    public <X extends Comparable<? super X>> X maxComparable(Class<X> targetType) {
        return criteriaQueryGenerator.doQuerySingleForType(targetType,
                                                           root -> getCriteriaBuilder().greatest(
                                                               root.<X>get(fieldName)));
    }

    @Override
    public <X extends Comparable<? super X>> X minComparable(Class<X> targetType) {
        return criteriaQueryGenerator.doQuerySingleForType(targetType,
                                                           root -> getCriteriaBuilder().least(
                                                               root.<X>get(fieldName)));
    }

    @Override
    public <T> T querySingle(Class<T> targetType) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType,
                                                                          query -> query.setMaxResults(2), false);

        if (resultList.isEmpty()) {
            LOG.info(String.format("Failed to select single result of %s from %s",
                                   criteriaQueryGenerator.getProtoType().getName(),
                                   fieldName));
            return null;
        } else if (resultList.size() > 1) {
            LOG.warn("Find more than one result with single query of "
                     + criteriaQueryGenerator.getProtoType().getName());
            throw new IllegalStateException();
        } else {
            return resultList.get(0);
        }
    }

    @Override
    public <T> T queryFirst(Class<T> targetType) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType,
                                                                          query -> query.setMaxResults(1), false);

        if (resultList.isEmpty()) {
            LOG.info("Failed to get first result of " + criteriaQueryGenerator.getProtoType().getName());
            return null;
        } else {
            return resultList.get(0);
        }
    }

    @Override
    public <T> List<T> queryList(Class<T> targetType) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType, null, false);

        if (resultList.isEmpty()) {
            LOG.info("Failed to find any result of " + criteriaQueryGenerator.getProtoType().getName());
        }

        return resultList;
    }

    @Override
    public <T> List<T> queryList(Class<T> targetType, int startIndex, int size) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType,
                                                                          query -> query.setMaxResults(size)
                                                                              .setFirstResult(startIndex),
                                                                          false);

        if (resultList.isEmpty()) {
            LOG.info(String.format("Failed to find entity of %s, of list (%d, %d)",
                                   criteriaQueryGenerator.getProtoType().getName(), size, startIndex));
        }

        return resultList;
    }

    @Override
    public <T> List<T> queryPage(Class<T> targetType, int pageSize, int pageIndex) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType,
                                                                          query -> query.setMaxResults(pageSize)
                                                                              .setFirstResult(pageIndex * pageSize),
                                                                          false);

        if (resultList.isEmpty()) {
            LOG.info(String.format("Failed to find entity of %s, of page (%d, %d)",
                                   criteriaQueryGenerator.getProtoType().getName(), pageSize, pageIndex));
        }

        return resultList;
    }

    private CriteriaBuilder getCriteriaBuilder() {
        return criteriaQueryGenerator.getEntityManager().getCriteriaBuilder();
    }
}
