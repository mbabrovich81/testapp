package com.turvo.testapp.dao;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface CreateReportDAO {

    /**
     * Drop and create tbl_test_task and tbl_test_performance_queue with their sequences
     * @throws Exception
     */
    void createTableWithDropping() throws Exception;
    
}
