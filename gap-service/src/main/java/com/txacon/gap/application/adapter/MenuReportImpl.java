package com.txacon.gap.application.adapter;

import com.txacon.gap.application.exceptions.ApiError;
import com.txacon.gap.application.exceptions.BusinessInvalidException;
import com.txacon.gap.domain.bussines.entities.Business;
import com.txacon.gap.domain.report.port.MenuReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuReportImpl implements MenuReport {

    private final DataSource dataSource;

    @Override
    public JasperPrint createPdfReport(Business business) {
        try {
            JasperReport jasperReport = JasperCompileManager
                    .compileReport(MenuReportImpl.class.getClassLoader().getResourceAsStream("Coffee.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("businessId", business.getId());
            parameters.put("businessName", business.getName());
            parameters.put("businessDescription", business.getDescription());
            
            return JasperFillManager.fillReport(jasperReport, parameters, dataSource.getConnection());
        } catch (Exception exception) {
            log.error("Error on render report", exception);
            throw new BusinessInvalidException(ApiError.ERROR_ON_REPORT_RENDER);
        }
    }

}
