package com.sclad.scladapp.mapper;

import com.sclad.scladapp.entity.DefectReport;
import com.sclad.scladapp.model.DefectReportModel;
import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefectReportMapper extends AbstractEntityMapper<DefectReport, DefectReportModel> {

    protected DefectReportMapper(Class<DefectReport> entityClass, Class<DefectReportModel> defectReportModelClass) {
        super(entityClass, defectReportModelClass);
    }

    @Override
    @Action(actionType = ActionType.END, message = "device.details.mapToModel", parentMessage = "device.details.findById", diagramIdentifiers = {"device.details"})
    public DefectReportModel toDto(DefectReport defectReport) {
        if (!StringUtils.isEmpty(defectReport.getDeviceSerialNumber())) {
            defectReport.setDeviceSerialNumber("");
        }
        return super.toDto(defectReport);
    }
}
