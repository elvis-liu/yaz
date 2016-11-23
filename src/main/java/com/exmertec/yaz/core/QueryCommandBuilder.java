package com.exmertec.yaz.core;

public interface QueryCommandBuilder<T> {
    QueryCommandBuilder<T> where(Query... queries);

    SelectionBuilder select(String fieldName);

    DistinctSelectionBuilder distinctSelect(String fieldName);

    GroupByBuilder groupBy(String... fieldNames);
}
