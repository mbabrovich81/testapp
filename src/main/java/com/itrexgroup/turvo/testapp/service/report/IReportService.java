package com.itrexgroup.turvo.testapp.service.report;

import com.itrexgroup.turvo.testapp.model.report.ReportResult;
import org.quartz.SchedulerException;

import java.util.List;

/**
 *
 * Report service.
 * Get report of the performance checking
 * Created by maxim.babrovich on 05.04.2019.
 */
public interface IReportService {

    /**
     * Get reports of query performance for DB's
     * @param requestUid - request uid
     * @param reportUid - report uid
     * @return List of ReportResult
     */
    List<ReportResult> getPerformanceReport(long requestUid, String reportUid) throws RuntimeException;

    /**
     *  Set failed state for report with state in_progress
     */
    void setFailedForInProgressReports() throws RuntimeException;

    /**
     * Remove all scheduler (quartz) jobs
     */
    void removeAllSchedulerJobs() throws RuntimeException, SchedulerException;
}
