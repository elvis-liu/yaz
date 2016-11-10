package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.DistinctSelectionBuilder;

import org.apache.log4j.Logger;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;

public class CoreDistinctSelectionBuilder<T> implements DistinctSelectionBuilder {
    private static final Logger LOGGER = Logger.getLogger(CoreDistinctSelectionBuilder.class);

    private final CriteriaQueryGenerator<T> criteriaQueryGenerator;
    private final String fieldName;

    public CoreDistinctSelectionBuilder(CriteriaQueryGenerator<T> criteriaQueryGenerator, String fieldName) {
        this.criteriaQueryGenerator = criteriaQueryGenerator;
        this.fieldName = fieldName;
    }

    @Override
    public Long count() {
        return criteriaQueryGenerator.doQuerySingleForType(Long.class,
                                                           root -> getCriteriaBuilder().countDistinct(
                                                               root.get(fieldName)));
    }


    @Override
    public <T> T querySingle(Class<T> targetType) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType,
                                                                          query -> query.setMaxResults(2), true);

        if (resultList.isEmpty()) {
            LOGGER.info(String.format("Failed to select single result of %s from %s",
                                   criteriaQueryGenerator.getProtoType().getName(),
                                   fieldName));
            return null;
        } else if (resultList.size() > 1) {
            LOGGER.warn("Find more than one result with single query of "
                         + criteriaQueryGenerator.getProtoType().getName());
            throw new IllegalStateException();
        } else {
            return resultList.get(0);
        }
    }

    @Override
    public <T> T queryFirst(Class<T> targetType) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType,
                                                                          query -> query.setMaxResults(1), true);

        if (resultList.isEmpty()) {
            LOGGER.info("Failed to get first result of " + criteriaQueryGenerator.getProtoType().getName());
            return null;
        } else {
            return resultList.get(0);
        }
    }

    @Override
    public <T> List<T> queryList(Class<T> targetType) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType, null, true);

        if (resultList.isEmpty()) {
            LOGGER.info("Failed to find any result of " + criteriaQueryGenerator.getProtoType().getName());
        }

        return resultList;
    }

    @Override
    public <T> List<T> queryList(Class<T> targetType, int startIndex, int size) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType,
                                                                          query -> query.setMaxResults(size)
                                                                              .setFirstResult(startIndex),
                                                                          true);

        if (resultList.isEmpty()) {
            LOGGER.info(String.format("Failed to find entity of %s, of list (%d, %d)",
                                   criteriaQueryGenerator.getProtoType().getName(), size, startIndex));
        }

        return resultList;
    }

    @Override
    public <T> List<T> queryPage(Class<T> targetType, int pageSize, int pageIndex) {
        List<T> resultList = criteriaQueryGenerator.doQueryListWithSelect(fieldName, targetType,
                                                                          query -> query.setMaxResults(pageSize)
                                                                              .setFirstResult(pageIndex * pageSize),
                                                                          true);

        if (resultList.isEmpty()) {
            LOGGER.info(String.format("Failed to find entity of %s, of page (%d, %d)",
                                   criteriaQueryGenerator.getProtoType().getName(), pageSize, pageIndex));
        }

        return resultList;
    }

    private CriteriaBuilder getCriteriaBuilder() {
        return criteriaQueryGenerator.getEntityManager().getCriteriaBuilder();
    }
}
