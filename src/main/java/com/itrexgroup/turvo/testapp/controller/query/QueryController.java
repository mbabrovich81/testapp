package com.itrexgroup.turvo.testapp.controller.query;

import com.itrexgroup.turvo.testapp.controller.dto.QueryPerformanceDTO;
import com.itrexgroup.turvo.testapp.service.performance.PerformanceService;
import com.itrexgroup.turvo.testapp.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by maxim.babrovich on 29.03.2019.
 */

@RestController
@Slf4j
public class QueryController {

    private PerformanceService performanceService;

    @GetMapping("/query/performance/check")
    public ResponseEntity<QueryPerformanceDTO> checkQueryPerformance(@RequestParam(value = "q") String query)
            throws Exception {

        // fix the start time of the request and use it like requestUid
        long requestUid = System.nanoTime();

        log.info("[{}][query/performance/check] Start to handle performance: {}", requestUid, query);

        // start to check performance
        // get reportUid
        String reportUid = performanceService.checkPerformance(requestUid, query);

        log.info("[{}][query/performance/check] Finish to handle performance: {} ", requestUid, query);

        return new ResponseEntity<>(convertToQueryPerformanceDTO(query, reportUid), HttpStatus.OK);
    }

    private QueryPerformanceDTO convertToQueryPerformanceDTO(String query, String reportUid) {
        return new QueryPerformanceDTO(query, reportUid, Utils.getReportLink(reportUid));
    }

    @Autowired
    public void setPerformanceService(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }
}
