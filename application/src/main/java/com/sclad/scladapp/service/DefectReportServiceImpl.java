package com.sclad.scladapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.sclad.scladapp.entity.DefectReport;
import com.sclad.scladapp.exceptions.DefectReportNotFoundException;
import com.sclad.scladapp.model.DefectReportModel;
import com.sclad.scladapp.repository.DefectReportRepository;
import com.sclad.scladapp.repository.UploadedFileRepository;

@Service
public class DefectReportServiceImpl implements DefectReportService {

    private final DefectReportRepository defectReportRepository;
    private final DeviceService deviceService;
    private final UploadedFileRepository uploadedFileRepository;

    @Autowired
    public DefectReportServiceImpl(DefectReportRepository defectReportRepository, DeviceService deviceService, UploadedFileRepository uploadedFileRepository) {
        this.defectReportRepository = defectReportRepository;
        this.deviceService = deviceService;
        this.uploadedFileRepository = uploadedFileRepository;
    }

    @Override
    public DefectReport create(DefectReportModel model) {
        DefectReport defectReport = new DefectReport();
        if (deviceService.getById(model.getDevice().getId()) != null) {
            defectReport.setDevice(model.getDevice());
        }
        defectReport.setDateOfDiscovery(model.getDateOfDiscovery());
        defectReport.setDeviceSerialNumber(model.getDeviceSerialNumber());
        defectReport.setFaultDescription(model.getFaultDescription());
        if (model.getAttachmentId() != null) {
            defectReport.setAttachment(uploadedFileRepository.getOne(model.getAttachmentId()));
        }
        defectReportRepository.save(defectReport);
        return defectReport;
    }

    @Override
    public List<DefectReport> getAll() {
        return defectReportRepository.findAll();
    }

    @Override
    //Delete fault report - resolve
    public void resolveFaultReport(Long id) {
        DefectReport defectReport = getById(id);
        if (defectReport != null) {
            //check if there is any file attached to fault report
            defectReportRepository.delete(defectReport);
            if (defectReport.getAttachment() != null && defectReport.getAttachment().getId() != null) {
                uploadedFileRepository.delete(defectReport.getAttachment());
            }
        }
    }

    @Override
    public DefectReport getById(Long id) {
        DefectReport defectReport = this.defectReportRepository.findById(id).orElse(null);
        if (defectReport != null) {
            return defectReport;
        } else {
            throw new DefectReportNotFoundException();
        }
    }
}
