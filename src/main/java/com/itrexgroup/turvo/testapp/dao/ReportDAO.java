package com.itrexgroup.turvo.testapp.dao;

import com.itrexgroup.turvo.testapp.model.report.Report;
import com.itrexgroup.turvo.testapp.model.report.ReportResult;
import com.itrexgroup.turvo.testapp.model.report.ReportState;

import java.util.List;
import java.util.Optional;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface ReportDAO {

    /**
     * Insert data to tbl_test_performance_report
     * @param report - report
     * @throws RuntimeException     *
     */
    void insertData(Report report) throws RuntimeException;

    /**
     * Update data in tbl_test_performance_report
     * @param report - report
     * @throws RuntimeException
     */
    void updateData(Report report) throws RuntimeException;

    /**
     * Update state in tbl_test_performance_report
     * @param target - target state
     * @param source - source state
     * @throws RuntimeException
     */
    void updateState(ReportState target, ReportState source) throws RuntimeException;

    /**
     *  Count reports with state in_progress
     * @return
     */
    Optional<Long> countInProgress();

    /**
     * List of reports by reportUid
     * @param reportUid - String
     * @return - list of ReportResult
     */
    List<ReportResult> findByReportUid(String reportUid);
}
