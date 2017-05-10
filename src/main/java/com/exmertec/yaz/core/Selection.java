package com.exmertec.yaz.core;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

public interface Selection {
    Expression toExpression(Path<?> path, CriteriaBuilder criteriaBuilder);
}
