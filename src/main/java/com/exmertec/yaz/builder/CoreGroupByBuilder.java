package com.exmertec.yaz.builder;

import static java.util.stream.Collectors.toList;


import com.exmertec.yaz.core.AliasAssigner;
import com.exmertec.yaz.core.GroupByBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

public class CoreGroupByBuilder<T> implements GroupByBuilder {
    private final CriteriaQueryGenerator<T> criteriaQueryGenerator;
    private final List<String> groupByFields;
    private final List<GroupByAggregator> aggregators;
    private final List<AliasOrderByRule> aliasOrderByRules;

    public CoreGroupByBuilder(CriteriaQueryGenerator<T> criteriaQueryGenerator, List<String> groupByFields) {
        this.criteriaQueryGenerator = criteriaQueryGenerator;
        this.groupByFields = groupByFields;
        aggregators = new ArrayList<>();
        aliasOrderByRules = new ArrayList<>();
    }

    @Override
    public AliasAssigner<GroupByBuilder> count(String targetField) {
        return addAggregator(targetField, CriteriaBuilder::count);
    }

    @Override
    public AliasAssigner<GroupByBuilder> distinctCount(String targetField) {
        return addAggregator(targetField, CriteriaBuilder::countDistinct);
    }

    @Override
    public AliasAssigner<GroupByBuilder> sum(String targetField) {
        return addAggregator(targetField, CriteriaBuilder::sum);
    }

    @Override
    public AliasAssigner<GroupByBuilder> avg(String targetField) {
        return addAggregator(targetField, CriteriaBuilder::avg);
    }

    @Override
    public AliasAssigner<GroupByBuilder> max(String targetField) {
        return addAggregator(targetField, CriteriaBuilder::max);
    }

    @Override
    public AliasAssigner<GroupByBuilder> min(String targetField) {
        return addAggregator(targetField, CriteriaBuilder::min);
    }

    private AliasAssigner<GroupByBuilder> addAggregator(String targetField,
                                                        AggregatorExpressionGenerator expressionGenerator) {
        GroupByAggregator aggregator = new GroupByAggregator(targetField, expressionGenerator);
        aggregators.add(aggregator);
        return aggregator;
    }

    @Override
    public List<Tuple> queryList() {
        return doTupleQueryList(null);
    }

    @Override
    public List<Tuple> queryPage(int pageSize, int pageIndex) {
        return doTupleQueryList(query -> query.setMaxResults(pageSize).setFirstResult(pageIndex));
    }

    private List<Tuple> doTupleQueryList(Consumer<TypedQuery<Tuple>> queryManipulator) {
        EntityManager entityManager = criteriaQueryGenerator.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleQuery = criteriaBuilder.createTupleQuery();

        Root<T> root = tupleQuery.from(criteriaQueryGenerator.getProtoType());
        List<Selection<?>> selections = generateMultiSelections(criteriaBuilder, root);
        tupleQuery.multiselect(selections);
        tupleQuery.groupBy(getGroupByExpression(root));
        tupleQuery.where(criteriaQueryGenerator.generateRestrictions(tupleQuery, criteriaQueryGenerator.getQueries()));
        tupleQuery.orderBy(getOrders(criteriaBuilder, root));

        TypedQuery<Tuple> query = entityManager.createQuery(tupleQuery);

        if (queryManipulator != null) {
            queryManipulator.accept(query);
        }

        return query.getResultList();
    }


    private List<Order> getOrders(CriteriaBuilder criteriaBuilder, Root<T> root) {
        Stream<Order> fieldOrderStream = criteriaQueryGenerator.getOrderByRules().stream()
            .map(rule -> rule.getOrder(criteriaBuilder, root));
        Stream<Order> aliasOrderStream = aliasOrderByRules.stream()
            .map(rule -> rule.getAliasOrder(criteriaBuilder, root));

        return Stream.concat(fieldOrderStream, aliasOrderStream).collect(toList());
    }

    @Override
    public GroupByBuilder ascendingByAlias(String alias) {
        aliasOrderByRules.add(new AliasOrderByRule(true, alias));
        return this;
    }

    @Override
    public GroupByBuilder descendingByAlias(String alias) {
        aliasOrderByRules.add(new AliasOrderByRule(false, alias));
        return this;
    }

    private List<Expression<?>> getGroupByExpression(Root<T> root) {
        return groupByFields.stream().map(field -> root.get(field)).collect(toList());
    }

    private List<Selection<?>> generateMultiSelections(CriteriaBuilder cb, Root<T> root) {
        List<Selection<?>> selections = new ArrayList<>();
        selections.addAll(groupByFields.stream().map(field -> root.get(field).alias(field)).collect(toList()));
        selections.addAll(aggregators.stream().map(
            aggregator -> aggregator.generateSelection(cb, root)).collect(toList()));
        return selections;
    }

    private class GroupByAggregator implements AliasAssigner<GroupByBuilder> {
        private String field;
        private AggregatorExpressionGenerator expressionGenerator;
        private String alias;

        public GroupByAggregator(String field, AggregatorExpressionGenerator expressionGenerator) {
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

        public Selection<?> generateSelection(CriteriaBuilder criteriaBuilder, Root root) {
            return getExpression(criteriaBuilder, root).alias(alias);
        }

        public String getAlias() {
            return alias;
        }

        public Expression getExpression(CriteriaBuilder criteriaBuilder, Root<T> root) {
            return expressionGenerator.generate(criteriaBuilder, root.get(field));
        }
    }

    @FunctionalInterface
    private interface AggregatorExpressionGenerator<T> {
        Expression<T> generate(CriteriaBuilder cb, Path<T> path);
    }

    private class AliasOrderByRule {
        private boolean isAscending;
        private String alias;

        public AliasOrderByRule(boolean isAscending, String alias) {
            this.isAscending = isAscending;
            this.alias = alias;
        }

        public Order getAliasOrder(CriteriaBuilder criteriaBuilder, Root<T> root) {
            Optional<GroupByAggregator> aggregator = aggregators.stream().filter(
                agg -> agg.getAlias().equals(alias)).findFirst();
            Expression expression = aggregator.orElseThrow(IllegalStateException::new).getExpression(criteriaBuilder,
                                                                                                     root);
            return isAscending ? criteriaBuilder.asc(expression) : criteriaBuilder.desc(expression);
        }
    }
}
