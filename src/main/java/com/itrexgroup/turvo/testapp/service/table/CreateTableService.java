package com.itrexgroup.turvo.testapp.service.table;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface CreateTableService {

    /**
     * Drop and create tbl_test_task with sequence
     * fill tbl_test_task by default values
     * @param db - DatabaseEnum
     */
    void createAndFillTable(DatabaseEnum db) throws RuntimeException;

    /**
     * Drop and create tbl_test_performance_report with sequence
     */
    void createReportTables() throws RuntimeException;
}
