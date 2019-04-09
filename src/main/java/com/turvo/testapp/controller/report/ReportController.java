package com.turvo.testapp.controller.report;

import com.turvo.testapp.controller.dto.PerformanceReportDTO;
import com.turvo.testapp.controller.dto.ReportItemDTO;
import com.turvo.testapp.model.report.ReportResult;
import com.turvo.testapp.model.report.ReportState;
import com.turvo.testapp.service.report.IReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim.babrovich on 04.04.2019.
 */

@RestController
@Slf4j
public class ReportController {

    private IReportService reportService;

    @GetMapping("/query/performance/report")
    public ResponseEntity<PerformanceReportDTO> getQueryPerformanceReport(@RequestParam(value = "r") String reportUid) {

        // fix the start time of the request and use it like requestUid
        long requestUid = System.nanoTime();

        log.info("[{}][query/performance/report] Start to get report: {}", requestUid, reportUid);

        if (StringUtils.isEmpty(reportUid)) {
            log.error("[{}][query/performance/report] Getting reportUid can't be empty.", requestUid);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        PerformanceReportDTO response;

        try {
            // start to get report
            List<ReportResult> reportResults = reportService.getPerformanceReport(requestUid, reportUid);

            response = convertToPerformanceReportDTO(reportResults, reportUid);
        } catch (Exception e) {
            log.error("[ERROR][{}][query/performance/report] Check query performance was failed. {} ", requestUid, reportUid, e);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("[{}][query/performance/report] Finish to get report: {}", requestUid, reportUid);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private PerformanceReportDTO convertToPerformanceReportDTO(List<ReportResult> reportResults, String reportUid) {

        ReportState state = CollectionUtils.isEmpty(reportResults) ? ReportState.not_exists : ReportState.success;
        String query = null;
        Timestamp createdDate = null;
        List<ReportItemDTO> items = new ArrayList<>();

        for (ReportResult rr: reportResults) {
            if (rr.getState().getRate() > state.getRate()) {
                state = rr.getState();
            }

            if (StringUtils.isEmpty(query)) {
                query = rr.getQuery();
            }

            if (createdDate == null) {
                createdDate = rr.getCreatedDate();
            }

            items.add(new ReportItemDTO(rr.getState(), rr.getRequestTime(), rr.getDatabase(), rr.getRequestStart()
                    , rr.getRequestEnd(), rr.getResMsg()));
        }

        return new PerformanceReportDTO(reportUid, state, query, createdDate, items);
    }

    @Autowired
    public void setReportService(IReportService reportService) {
        this.reportService = reportService;
    }
}
