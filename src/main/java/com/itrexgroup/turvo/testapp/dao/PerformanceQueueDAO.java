package com.itrexgroup.turvo.testapp.dao;

import com.itrexgroup.turvo.testapp.model.queue.PerformanceQueue;
import com.itrexgroup.turvo.testapp.model.queue.PerformanceQueueResult;

import java.util.Map;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface PerformanceQueueDAO {

    /**
     * Insert data to tbl_test_performance_queue
     * @param queue - Performance Queue
     * @throws Exception
     */
    void insertData(PerformanceQueue queue) throws Exception;

    /**
     * Update Attempts num in tbl_test_performance_queue
     * @param reportUid - report Uid
     * @throws Exception
     */
    void updateAttempts(String reportUid) throws Exception;

    /**
     * Delete data in tbl_test_performance_queue
     * @param reportUid - report Uid
     * @throws Exception
     */
    void deleteData(String reportUid) throws Exception;

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
