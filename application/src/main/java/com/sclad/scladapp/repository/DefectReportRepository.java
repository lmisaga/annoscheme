package com.sclad.scladapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sclad.scladapp.entity.DefectReport;

public interface DefectReportRepository extends JpaRepository<DefectReport, Long> {
}
