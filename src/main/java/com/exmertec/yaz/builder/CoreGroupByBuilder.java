package com.exmertec.yaz.builder;

import static java.util.stream.Collectors.toList;

import com.exmertec.yaz.core.AliasAssigner;
import com.exmertec.yaz.core.GroupByBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public class CoreGroupByBuilder<T> implements GroupByBuilder {
    private final CriteriaQueryGenerator<T> criteriaQueryGenerator;
    private final String groupByField;
    private final List<GroupByAggregator> aggregators;

    public CoreGroupByBuilder(CriteriaQueryGenerator<T> criteriaQueryGenerator, String groupByField) {
        this.criteriaQueryGenerator = criteriaQueryGenerator;
        this.groupByField = groupByField;
        aggregators = new ArrayList<>();
    }

    @Override
    public AliasAssigner<GroupByBuilder> count(String targetField) {
        return addAggregator(targetField, (cb, root) -> cb.count(root.get(targetField)));
    }

    private AliasAssigner<GroupByBuilder> addAggregator(String targetField,
                                                        GroupByExpressionGenerator expressionGenerator) {
        GroupByAggregator aggregator = new GroupByAggregator(targetField, expressionGenerator);
        aggregators.add(aggregator);
        return aggregator;
    }

    @Override
    public List<Tuple> queryList() {
        EntityManager entityManager = criteriaQueryGenerator.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleQuery = criteriaBuilder.createTupleQuery();

        Root<T> root = tupleQuery.from(criteriaQueryGenerator.getProtoType());
        List<Selection<?>> selections = generateMultiSelections(criteriaBuilder, root);
        tupleQuery.multiselect(selections);
        tupleQuery.groupBy(root.get(groupByField));
        tupleQuery.where(criteriaQueryGenerator.generateRestrictions(tupleQuery, criteriaQueryGenerator.getQueries()));
        criteriaQueryGenerator.appendOrderBy(criteriaBuilder, tupleQuery);

        return entityManager.createQuery(tupleQuery).getResultList();
    }

    private List<Selection<?>> generateMultiSelections(CriteriaBuilder cb, Root<T> root) {
        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get(groupByField).alias(groupByField));
        selections.addAll(aggregators.stream().map(
            aggregator -> aggregator.generateSelection(cb, root)).collect(toList()));
        return selections;
    }

    private class GroupByAggregator implements AliasAssigner<GroupByBuilder> {
        private String field;
        private GroupByExpressionGenerator expressionGenerator;
        private String alias;
        private Expression<?> expression;

        public GroupByAggregator(String field, GroupByExpressionGenerator expressionGenerator) {
            this.field = field;
            this.expressionGenerator = expressionGenerator;
        }

        @Override
        public GroupByBuilder as(String alias) {
            this.alias = alias;
            return CoreGroupByBuilder.this;
        }

        public String getField() {
            return field;
        }

        public Selection<?> generateSelection(CriteriaBuilder cb, Root root) {
            expression = expressionGenerator.generateExpression(cb, root);
            return expression.alias(alias);
        }
    }

    @FunctionalInterface
    private interface GroupByExpressionGenerator {
        Expression<?> generateExpression(CriteriaBuilder cb, Root root);
    }
}
