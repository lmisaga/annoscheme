package com.sclad.scladapp.mapper;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.springframework.stereotype.Component;

import com.sclad.scladapp.entity.DefectReport;
import com.sclad.scladapp.model.DefectReportModel;

@Component
public class DefectReportMapper extends AbstractEntityMapper<DefectReport, DefectReportModel> {

	protected DefectReportMapper() {
		super(DefectReport.class, DefectReportModel.class);
	}

	@Action(actionType = ActionType.END, message = "report.find.mapToDto", parentMessage = "report.find.findById", diagramIdentifiers = {"report.find"})
	public DefectReportModel toDto(DefectReport entity) {
		DefectReportModel model = new DefectReportModel();
		if (entity.getDevice() != null) {
			model.setDeviceSerialNumber(null);
		}
		return super.toDto(entity);
	}
}
