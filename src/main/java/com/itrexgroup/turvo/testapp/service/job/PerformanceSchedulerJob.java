package com.itrexgroup.turvo.testapp.service.job;

import com.itrexgroup.turvo.testapp.service.performance.PerformanceService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Created by maxim.babrovich on 07.04.2019.
 */

@Component
@Slf4j
@DisallowConcurrentExecution
public class PerformanceSchedulerJob extends QuartzJobBean {

    private PerformanceService performanceService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("[PerformanceSchedulerJob] Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        try {
            JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

            long requestUid = jobDataMap.getLong("requestUid");
            String reportUid = jobDataMap.getString("reportUid");
            String query = jobDataMap.getString("query");

            int timesTriggered = ((SimpleTriggerImpl)jobExecutionContext.getTrigger()).getTimesTriggered();
            log.info("[{}][PerformanceSchedulerJob] timesTriggered {} with key {}", requestUid, timesTriggered, jobExecutionContext.getJobDetail().getKey());

            performanceService.repeatedCheckPerformance(requestUid, reportUid, timesTriggered, query);

            log.info("[{}][PerformanceSchedulerJob] Job executed successfully. Key: {}", requestUid, jobExecutionContext.getJobDetail().getKey());

        } catch (Exception e) {
            log.error("[PerformanceSchedulerJob][ERROR] Job executing was failed with key {}", jobExecutionContext.getJobDetail().getKey(), e);
        }
    }

    @Autowired
    public void setPerformanceService(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

}
