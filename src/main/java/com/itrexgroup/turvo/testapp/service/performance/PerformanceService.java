package com.itrexgroup.turvo.testapp.service.performance;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import org.quartz.SchedulerException;

/**
 *
 * Performance service.
 * Check Performance for DB performance
 * Created by maxim.babrovich on 29.03.2019.
 */
public interface PerformanceService {

    /**
     * Check performance of DB performance
     * @param requestUid - request uid
     * @param query - String
     * @return report Uid
     */
    String checkPerformance(long requestUid, String query) throws RuntimeException, SchedulerException;

    /**
     * Repeated check performance of DB performance
     * @param requestUid - request uid
     * @param reportUid - report uid
     * @param timesTriggered - number of triggered
     * @param query - SQL query
     */
    void repeatedCheckPerformance(long requestUid, String reportUid, int timesTriggered, String query)
            throws RuntimeException, SchedulerException;

    /**
     * Check performance of DB performance in parallel threads
     * @param requestUid - request uid
     * @param query - String
     */
    void asyncCheckPerformance(long requestUid, DatabaseEnum db, String reportUid, String query) ;
}
