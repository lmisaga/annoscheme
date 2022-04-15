package com.sclad.scladapp.service;

import java.util.List;

import com.sclad.scladapp.entity.DefectReport;
import com.sclad.scladapp.model.DefectReportModel;

public interface DefectReportService extends AbstractService<DefectReport> {

	DefectReport create(DefectReportModel model);

	List<DefectReport> getAll();

	void resolveFaultReport(Long id);

	DefectReportModel findById(Long id);

}
