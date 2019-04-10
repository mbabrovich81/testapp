package com.itrexgroup.turvo.testapp.persistence.dao;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */
public interface JdbcMultiDatabaseDAO {

    /**
     *  Execute SELECT query
     * @param db - DatabaseEnum
     * @param query - SELECT query
     * @return List of Map
     */
    int queryForList(DatabaseEnum db, String query);


}
