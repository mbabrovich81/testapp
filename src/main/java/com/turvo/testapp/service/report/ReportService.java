package com.turvo.testapp.service.report;

import com.turvo.testapp.dao.PerformanceQueueDAO;
import com.turvo.testapp.dao.ReportDAO;
import com.turvo.testapp.model.report.PerformanceQueueResult;
import com.turvo.testapp.model.report.ReportResult;
import com.turvo.testapp.model.report.ReportState;
import com.turvo.testapp.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Report service.
 * Get report of the performance checking
 * Created by maxim.babrovich on 05.04.2019.
 */
@Service
@Slf4j
public class ReportService implements IReportService {

    private ReportDAO reportDAO;

    private PerformanceQueueDAO performanceQueueDAO;

    private Scheduler scheduler;

    @Value("${turvo.scheduler.job.repeatCount}")
    private int repeatCount;

    @Override
    public List<ReportResult> getPerformanceReport(long requestUid, String reportUid) throws Exception {
        log.info("[{}][getPerformanceReport] Start to get performance report. ReportUid: {}", requestUid, reportUid);

        List<ReportResult> result;

        try {
            // find performance report by the reportUid
            result = reportDAO.findByReportUid(reportUid);

            if (CollectionUtils.isEmpty(result)) {
                PerformanceQueueResult performanceQueue = performanceQueueDAO.findByReportUid(reportUid);

                if (performanceQueue != null
                        && performanceQueue.getAttemptsNum() < repeatCount) {
                    result.add(convertToReportResult(performanceQueue));
                }
            }
        } catch (Exception e) {
            log.error("[ERROR][{}][getPerformanceReport] Get report of query performance was failed. {} ", requestUid, reportUid, e);
            throw new Exception(e);
        }

        log.info("[{}][getPerformanceReport] Finish to get performance report. ReportUid: {}", requestUid, reportUid);

        return result;
    }

    @Override
    public void setFailedForInProgressReports() {
        log.info("[setFailedForInProgressReports] Start to set 'failed' for ' in_progress' reports.");

        try {
            // Change state for reports (in_progress to failed)
            reportDAO.updateState(getUpdateStatusReportData(ReportState.failed, ReportState.in_progress));

        } catch (Exception e) {
            log.error("[ERROR][setFailedForInProgressReports] Set 'failed' for ' in_progress' reports was failed.", e);
        }

        log.info("[setFailedForInProgressReports] Finish to set 'failed' for ' in_progress' reports.");
    }

    @Override
    public void removeAllSchedulerJobs() {
        log.info("[removeAllJobs] Start to remove all jobs of reports.");

        try {
            // delete all queue data from DB
            performanceQueueDAO.deleteAll();
        } catch (Exception e) {
            log.error("[ERROR][removeAllJobs] Remove queues of reports {} was failed.", e);
        }

        try {
            // delete Performance job
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Utils.SCHEDULER_JOB_GROUP));
            if (CollectionUtils.isEmpty(jobKeys)) {

                log.info("[removeAllJobs] There are No jobs of reports");
            }
            scheduler.deleteJobs(new LinkedList<>(jobKeys));

        } catch (Exception e) {
            log.error("[ERROR][removeAllJobs] Delete all quartz jobs of reports was failed.", e);
        }

        log.info("[removeAllJobs] Finish to remove all jobs of reports.");
    }

    private ReportResult convertToReportResult(PerformanceQueueResult performanceQueue) {
        ReportResult result = new ReportResult();

        result.setReportUid(performanceQueue.getReportUid());
        result.setState(ReportState.in_waiting_queue);
        result.setCreatedDate(performanceQueue.getCreatedDate());
        result.setQuery(performanceQueue.getQuery());

        return result;
    }

    private Object[] getUpdateStatusReportData(ReportState target, ReportState source) {
        return new Object[] { target.name(), source.name() };
    }

    @Autowired
    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    @Autowired
    public void setPerformanceQueueDAO(PerformanceQueueDAO performanceQueueDAO) {
        this.performanceQueueDAO = performanceQueueDAO;
    }

    @Autowired
    public void setScheduler(@Qualifier("schedulerFactory") Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
