package com.exmertec.yaz.builder;

import com.exmertec.yaz.core.PathBuilder;
import com.exmertec.yaz.core.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class CorePathBuilder implements PathBuilder {
    private final List<String> cascadedPaths;

    public CorePathBuilder() {
        cascadedPaths = new ArrayList<>();
    }

    @Override
    public QueryBuilder field(String field) {
        return new PathAwareQueryBuilderAdapter(new FieldQueryBuilder(field), cascadedPaths);
    }

    @Override
    public PathBuilder embedded(String embeddedField) {
        cascadedPaths.add(embeddedField);
        return this;
    }
}
