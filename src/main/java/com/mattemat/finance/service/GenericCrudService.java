package com.mattemat.finance.service;

import java.util.List;

public interface GenericCrudService<T, K> {
    K create(T t);

    K read(Long id);

    List<K> readAll();

    void update(Long id, T t);

    void delete(Long id);
}
