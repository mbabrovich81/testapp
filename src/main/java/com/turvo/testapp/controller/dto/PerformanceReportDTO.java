package com.turvo.testapp.controller.dto;

import com.turvo.testapp.model.report.ReportState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by maxim.babrovich on 05.04.2019.
 */

@Data
@AllArgsConstructor
public class PerformanceReportDTO {

    // num of report
    private String reportUid;

    // report state
    private ReportState state;

    // query
    private String query;

    //created date
    private Timestamp createdDate;

    // query
    private List<ReportItemDTO> items;
}
