package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.Query;
import com.exmertec.yaz.core.QueryBuilder;

import java.util.List;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

public class PathAwareQueryBuilderAdapter extends QueryBuilderAdapter {
    private final List<String> cascadedFields;

    protected PathAwareQueryBuilderAdapter(QueryBuilder delegate, List<String> cascadedFields) {
        super(delegate);
        this.cascadedFields = cascadedFields;
    }

    @Override
    protected Query wrap(Query query) {
        return new QueryWrapper(query);
    }

    private class QueryWrapper implements Query {
        private final Query originalQuery;

        public QueryWrapper(Query query) {
            this.originalQuery = query;
        }

        @Override
        public List<Predicate> toRestrictions(CriteriaBuilder criteriaBuilder, AbstractQuery<?> abstractQuery,
                                              Path<?> path) {
            Path<?> endPath = path;
            for (String field : cascadedFields) {
                endPath = endPath.get(field);
            }

            return originalQuery.toRestrictions(criteriaBuilder, abstractQuery, endPath);
        }
    }
}
