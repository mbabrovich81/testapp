package com.itrexgroup.turvo.testapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itrexgroup.turvo.testapp.model.queue.PerformanceQueue;
import com.itrexgroup.turvo.testapp.model.report.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

/**
 * Static utils methods
 *
 * Created by maxim.babrovich on 05.04.2019.
 */
@Slf4j
public class Utils {

    public static final String SCHEDULER_JOB_GROUP = "performance-jobs";
    public static final String SCHEDULER_TRIGGER_GROUP = "performance-triggers";
    public static final String SCHEDULER_JOB_DESCRIPTION = "Performance Checking Job";
    public static final String SCHEDULER_TRIGGER_DESCRIPTION = "Performance Checking Trigger";

    private static final String EMPTY = "";

    private static final String REPORT_LINK = "../query/performance/report?r=";

    private static final String RESULT_MSQ_PREFIX = "Result size: ";

    public static String toJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[JSON]", e);
            return null;
        }
    }

    public static Map<String, Object> reportToMap(Report report) {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> result = mapper.convertValue(report, Map.class);

        if (report.getCreatedDate() != null) {
            result.put("createdDate", report.getCreatedDate());
        }

        if (report.getStartDate() != null) {
            result.put("startDate", report.getStartDate());
        }

        if (report.getEndDate() != null) {
            result.put("endDate", report.getEndDate());
        }

        return result;
    }

    public static Map<String, Object> performanceQueueToMap(PerformanceQueue queue) {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> result = mapper.convertValue(queue, Map.class);

        if (queue.getCreatedDate() != null) {
            result.put("createdDate", queue.getCreatedDate());
        }

        return result;
    }

    public static String getReportLink(String reportUid) {
        if (!StringUtils.isEmpty(reportUid)) {
            return REPORT_LINK + reportUid;
        }
        return EMPTY;
    }

    public static String generateUuid() {
        UUID uuid = UUID.randomUUID();
        String hexNumber = uuid.toString().replaceAll("-", "");
        BigInteger bigInteger = new BigInteger(hexNumber, 16);
        String radix36Uuid = bigInteger.toString(Character.MAX_RADIX);
        return radix36Uuid.toUpperCase();
    }

    public static String getResultMsg(int listSize) {
        return RESULT_MSQ_PREFIX + listSize;
    }
}
