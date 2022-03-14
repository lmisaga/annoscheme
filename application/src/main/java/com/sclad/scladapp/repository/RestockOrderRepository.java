package com.sclad.scladapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sclad.scladapp.entity.RestockOrder;

@Repository
public interface RestockOrderRepository extends JpaRepository<RestockOrder, Long> {
}
