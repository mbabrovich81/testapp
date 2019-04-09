package com.itrexgroup.turvo.testapp.service.performance;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;

/**
 *
 * Performance service.
 * Check Performance for DB performance
 * Created by maxim.babrovich on 29.03.2019.
 */
public interface IPerformanceService {

    /**
     * Check performance of DB performance
     * @param requestUid - request uid
     * @param query - String
     * @return List of QueryResult
     */
    String checkPerformance(long requestUid, String query) throws Exception;

    /**
     * Check performance of DB performance
     * @param requestUid - request uid
     * @param query - String
     * @return List of QueryResult
     */
    void checkPerformance(long requestUid, DatabaseEnum db, String reportUid, String query) ;

    /**
     * Repeated check performance of DB performance
     * @param requestUid - request uid
     * @param reportUid - report uid
     * @param timesTriggered - number of triggered
     * @param query - SQL query
     * @throws Exception
     */
    void repeatedCheckPerformance(long requestUid, String reportUid, int timesTriggered, String query) throws Exception;
}
