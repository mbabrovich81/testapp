package com.turvo.testapp.dao;

import com.turvo.testapp.model.report.ReportResult;

import java.util.List;
import java.util.Optional;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface ReportDAO {

    /**
     * Insert data to tbl_test_performance_report
     * @param values - list of values
     * @throws Exception     *
     */
    void insertData(Object[] values) throws Exception;

    /**
     * Update data in tbl_test_performance_report
     * @param values - list of values
     * @throws Exception
     */
    void updateData(Object[] values) throws Exception;

    /**
     * Update state in tbl_test_performance_report
     * @param values - list of values
     * @throws Exception
     */
    void updateState(Object[] values) throws Exception;

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
