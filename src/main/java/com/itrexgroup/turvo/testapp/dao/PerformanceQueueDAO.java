package com.itrexgroup.turvo.testapp.dao;

import com.itrexgroup.turvo.testapp.model.queue.PerformanceQueue;
import com.itrexgroup.turvo.testapp.model.queue.PerformanceQueueResult;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface PerformanceQueueDAO {

    /**
     * Insert data to tbl_test_performance_queue
     * @param queue - Performance Queue
     * @throws RuntimeException
     */
    void insertData(PerformanceQueue queue) throws RuntimeException;

    /**
     * Update Attempts num in tbl_test_performance_queue
     * @param reportUid - report Uid
     * @throws RuntimeException
     */
    void updateAttempts(String reportUid) throws RuntimeException;

    /**
     * Delete data in tbl_test_performance_queue
     * @param reportUid - report Uid
     * @throws RuntimeException
     */
    void deleteData(String reportUid) throws RuntimeException;

    /**
     * Delete all from tbl_test_performance_queue
     * @throws Exception
     */
    void deleteAll() throws RuntimeException;

    /**
     * List of reports by reportUid
     * @param reportUid - String
     * @return - PerformanceQueueResult
     */
    PerformanceQueueResult findByReportUid(String reportUid);
}
