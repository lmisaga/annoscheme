package com.sclad.scladapp.model;

import org.springframework.lang.Nullable;

public abstract class AbstractModel {

    @Nullable
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
