package com.itrexgroup.turvo.testapp.service.report;

import com.itrexgroup.turvo.testapp.dao.PerformanceQueueDAO;
import com.itrexgroup.turvo.testapp.dao.ReportDAO;
import com.itrexgroup.turvo.testapp.exception.EmptyArgumentsException;
import com.itrexgroup.turvo.testapp.model.queue.PerformanceQueueResult;
import com.itrexgroup.turvo.testapp.model.report.ReportResult;
import com.itrexgroup.turvo.testapp.model.report.ReportState;
import com.itrexgroup.turvo.testapp.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    public List<ReportResult> getPerformanceReport(long requestUid, String reportUid) throws RuntimeException {

        if (StringUtils.isEmpty(reportUid)) {
            throw new EmptyArgumentsException("ReportUid can't be empty");
        }

        log.info("[{}][getPerformanceReport] Start to get performance report. ReportUid: {}", requestUid, reportUid);

        // find performance report by the reportUid
        List<ReportResult> result = reportDAO.findByReportUid(reportUid);

        if (CollectionUtils.isEmpty(result)) {
            PerformanceQueueResult performanceQueue = performanceQueueDAO.findByReportUid(reportUid);

            if (performanceQueue != null
                    && performanceQueue.getAttemptsNum() < repeatCount) {
                result.add(convertToReportResult(performanceQueue));
            }
        }

        log.info("[{}][getPerformanceReport] Finish to get performance report. ReportUid: {}", requestUid, reportUid);

        return result;
    }

    @Override
    public void setFailedForInProgressReports() throws RuntimeException {
        log.info("[setFailedForInProgressReports] Start to set 'failed' for ' in_progress' reports.");

        // Change state for reports (in_progress to failed)
        reportDAO.updateState(ReportState.failed, ReportState.in_progress);

        log.info("[setFailedForInProgressReports] Finish to set 'failed' for ' in_progress' reports.");
    }

    @Override
    public void removeAllSchedulerJobs() throws RuntimeException, SchedulerException {
        log.info("[removeAllJobs] Start to remove all jobs of reports.");

        // delete all queue data from DB
        performanceQueueDAO.deleteAll();

        // delete Performance job
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Utils.SCHEDULER_JOB_GROUP));

        if (CollectionUtils.isEmpty(jobKeys)) {

            log.info("[removeAllJobs] There are No jobs of reports");

        } else {
            scheduler.deleteJobs(new LinkedList<>(jobKeys));

            log.info("[removeAllJobs] Finish to remove all jobs of reports.");
        }
    }

    private ReportResult convertToReportResult(PerformanceQueueResult performanceQueue) {
        ReportResult result = new ReportResult();

        result.setReportUid(performanceQueue.getReportUid());
        result.setState(ReportState.in_waiting_queue);
        result.setCreatedDate(performanceQueue.getCreatedDate());
        result.setQuery(performanceQueue.getQuery());

        return result;
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
