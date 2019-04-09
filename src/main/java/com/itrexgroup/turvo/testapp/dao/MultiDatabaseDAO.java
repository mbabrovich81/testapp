package com.itrexgroup.turvo.testapp.dao;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;

import java.util.List;
import java.util.Map;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface MultiDatabaseDAO {

    /**
     * Drop and create tbl_test_task with sequence
     * @param db - DatabaseEnum
     */
    void createTableWithDropping(DatabaseEnum db) throws Exception;

    /**
     * Insert data to tbl_test_task
     * @param db - DatabaseEnum
     * @param values - list of default values
     */
    void insertData(DatabaseEnum db, List<Object[]> values) throws Exception;

    /**
     *  Execute SELECT query
     * @param db - DatabaseEnum
     * @param query - SELECT query
     * @return List of Map
     */
    List<Map<String, Object>> queryForList(DatabaseEnum db, String query);


}
