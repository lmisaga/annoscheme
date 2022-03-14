package com.sclad.scladapp.service;

import com.sclad.scladapp.entity.AbstractEntity;

public interface AbstractService<T extends AbstractEntity> {
    T getById(Long id);
}
