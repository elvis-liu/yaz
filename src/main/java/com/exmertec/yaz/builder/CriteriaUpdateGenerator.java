package com.exmertec.yaz.builder;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;

interface CriteriaUpdateGenerator<T> extends CriteriaGenerator<T> {

    EntityManager getEntityManager();

    default CriteriaUpdate<T> createUpdate(Class<T> targetType) {
        CriteriaUpdate<T> criteriaUpdate = getEntityManager().getCriteriaBuilder().createCriteriaUpdate(targetType);
        criteriaUpdate.from(getProtoType());

        return criteriaUpdate;
    }

    default int doUpdate(List<UpdateRule> updateRules, boolean cleanCache) {
        CriteriaUpdate<T> criteriaUpdate = createUpdate(getProtoType());

        Predicate[] generatedRestrictions = generateRestrictions(criteriaUpdate, getQueries());

        criteriaUpdate.where(generatedRestrictions);
        updateRules.forEach(rule -> criteriaUpdate.set(rule.getFieldName(), rule.getValue()));

        Query query = getEntityManager().createQuery(criteriaUpdate);
        addQueryOptions(query);

        int result = query.executeUpdate();

        if (cleanCache) {  // clean cache to force query operates not use cache after invoke update.
            getEntityManager().clear();
        }

        return result;
    }

    class UpdateRule {
        private String fieldName;
        private Object value;

        public UpdateRule(String fieldName, Object value) {
            this.fieldName = fieldName;
            this.value = value;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Object getValue() {
            return value;
        }
    }
}
