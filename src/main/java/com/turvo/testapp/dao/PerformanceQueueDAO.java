package com.turvo.testapp.dao;

import com.turvo.testapp.model.report.PerformanceQueueResult;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface PerformanceQueueDAO {

    /**
     * Insert data to tbl_test_performance_queue
     * @param values - list of values
     * @throws Exception     *
     */
    void insertData(Object[] values) throws Exception;

    /**
     * Update data in tbl_test_performance_queue
     * @param values - list of values
     * @throws Exception
     */
    void updateData(Object[] values) throws Exception;

    /**
     * Delete data in tbl_test_performance_queue
     * @param values - list of values
     * @throws Exception
     */
    void deleteData(Object[] values) throws Exception;

    /**
     * Delete all from tbl_test_performance_queue
     * @throws Exception
     */
    void deleteAll() throws Exception;

    /**
     * List of reports by reportUid
     * @param reportUid - String
     * @return - PerformanceQueueResult
     */
    PerformanceQueueResult findByReportUid(String reportUid);
}
