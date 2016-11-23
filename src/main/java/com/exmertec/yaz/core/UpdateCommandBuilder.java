package com.exmertec.yaz.core;

public interface UpdateCommandBuilder<T> {
    UpdateCommandBuilder<T> update(String fieldName, Object value);

    int execute();

    int execute(boolean cleanCache);
}
