package com.turvo.testapp.controller.query;

import com.turvo.testapp.controller.dto.QueryPerformanceDTO;
import com.turvo.testapp.service.performance.IPerformanceService;
import com.turvo.testapp.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by maxim.babrovich on 29.03.2019.
 */

@RestController
@Slf4j
public class QueryController {

    private IPerformanceService performanceService;

    @GetMapping("/query/performance/check")
    public ResponseEntity<QueryPerformanceDTO> checkQueryPerformance(@RequestParam(value = "q") String query) {

        // fix the start time of the request and use it like requestUid
        long requestUid = System.nanoTime();

        log.info("[{}][query/performance/check] Start to handle performance: {}", requestUid, query);

        if (StringUtils.isEmpty(query)) {
            log.error("[{}][query/performance/check] Checking query can't be empty.", requestUid);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        QueryPerformanceDTO response;

        try {
            // start to check performance
            // get reportUid
            String reportUid = performanceService.checkPerformance(requestUid, query);

            response = convertToQueryPerformanceDTO(query, reportUid);

            log.info("[{}][query/performance/check] Finish to handle performance: {} ", requestUid, query);

        } catch (Exception e) {
            log.error("[ERROR][{}][query/performance/check] Check query performance was failed. {} ", requestUid, query, e);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private QueryPerformanceDTO convertToQueryPerformanceDTO(String query, String reportUid) {
        return new QueryPerformanceDTO(query, reportUid, Utils.getReportLink(reportUid));
    }

    @Autowired
    public void setPerformanceService(IPerformanceService performanceService) {
        this.performanceService = performanceService;
    }
}
