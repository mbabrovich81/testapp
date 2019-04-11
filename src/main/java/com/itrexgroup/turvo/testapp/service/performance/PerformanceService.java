package com.itrexgroup.turvo.testapp.service.performance;

import com.itrexgroup.turvo.testapp.dao.MultiDatabaseDAO;
import com.itrexgroup.turvo.testapp.dao.PerformanceQueueDAO;
import com.itrexgroup.turvo.testapp.dao.ReportDAO;
import com.itrexgroup.turvo.testapp.exception.EmptyArgumentsException;
import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import com.itrexgroup.turvo.testapp.model.queue.PerformanceQueue;
import com.itrexgroup.turvo.testapp.model.report.Report;
import com.itrexgroup.turvo.testapp.model.report.ReportState;
import com.itrexgroup.turvo.testapp.service.job.PerformanceSchedulerJob;
import com.itrexgroup.turvo.testapp.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Performance service.
 * Execute performance in DB
 * Created by maxim.babrovich on 29.03.2019.
 */
@Service
@Slf4j
public class PerformanceService implements IPerformanceService {

    private MultiDatabaseDAO multiDatabaseDAO;

    private ReportDAO reportDAO;

    private PerformanceQueueDAO performanceQueueDAO;

    private Scheduler scheduler;

    private IPerformanceService performanceService;

    @Value("${turvo.scheduler.job.intervalInMinutes}")
    private int intervalInMinutes;

    @Value("${turvo.scheduler.job.repeatCount}")
    private int repeatCount;

    @Override
    public String checkPerformance(long requestUid, String query) throws RuntimeException, SchedulerException {

        if (StringUtils.isEmpty(query)) {
            throw new EmptyArgumentsException("Query can't be empty");
        }

        log.info("[{}][checkPerformance] Start to check performance. Query: {}", requestUid, query);
        // generate unique report uid
        String reportUid =  Utils.generateUuid();

        // count of reports with state 'in_progress'
        long countInProgress = reportDAO.countInProgress().orElse(0L);

        // if there aren't reports in progress (count == 0, all databases are free)
        if (countInProgress == 0) {

            // Start to check performance in parallel threads for every databases
            ForkJoinPool forkJoinPool = new ForkJoinPool(DatabaseEnum.values().length);

            forkJoinPool.submit(() -> Arrays.stream(DatabaseEnum.values()).parallel().forEach(db -> {
                performanceService.asyncCheckPerformance(requestUid, db, reportUid, query);
            }));
        } else {
            // if there are some reports in progress (count > 0, some databases are busy)
            // put query to the queue
            performanceQueueDAO.insertData(getNewPerformanceQueueData(reportUid, query));

            // start job in scheduler
            JobDetail jobDetail = buildJobDetail(requestUid, reportUid, query);
            Trigger trigger = buildJobTrigger(jobDetail);
            scheduler.scheduleJob(jobDetail, trigger);
        }

        log.info("[{}][checkPerformance] Returning report uid: {}", requestUid, reportUid);

        return reportUid;
    }

    @Override
    public void repeatedCheckPerformance(long requestUid, String reportUid, int timesTriggered, String query)
            throws RuntimeException, SchedulerException {
        log.info("[{}][repeatedCheckPerformance] Start to repeated check performance. ReportUid {}. Query: {}"
                , requestUid, reportUid, query);

        // count of reports with state 'in_progress'
        long countInProgress = reportDAO.countInProgress().orElse(0L);

        // if there aren't report in progress (count == 0, all databases are free)
        if (countInProgress == 0) {

            deletePerformanceQueue(requestUid, reportUid);

            // Start to check performance in parallel threads for every databases
            ForkJoinPool forkJoinPool = new ForkJoinPool(DatabaseEnum.values().length);

            forkJoinPool.submit(() -> Arrays.stream(DatabaseEnum.values()).parallel().forEach(db -> {
                performanceService.asyncCheckPerformance(requestUid, db, reportUid, query);
            }));
        } else {
            // if there are some reports in progress (count > 0, some databases are busy)
            // update the queue
            performanceQueueDAO.updateAttempts(reportUid);

            if (timesTriggered >= repeatCount) {
                deletePerformanceQueue(requestUid, reportUid);
            }
        }

        log.info("[{}][repeatedCheckPerformance] Finish to repeated check performance. ReportUid {}. Query: {}", requestUid, reportUid, query);
    }

    @Override
    public void asyncCheckPerformance(long requestUid, DatabaseEnum db, String reportUid, String query) {
        log.info("[{}][checkPerformance] Start to execute performance {}. Db: {}", requestUid, query, db.name().toUpperCase());

        try {
            reportDAO.insertData(getNewReportData(db, reportUid, query));

            Report performanceReport = executeCheckingPerformance(requestUid, db, reportUid, query);

            reportDAO.updateData(performanceReport);

        } catch (DataAccessException e) {
            log.error("[ERROR][{}][checkPerformance] Insert/update performance report {} was failed. Db: {}"
                    , requestUid, reportUid, db.name().toUpperCase(), e);
        }

        log.info("[{}][checkPerformance] Finish to execute performance {}. Db: {}", requestUid, query, db.name().toUpperCase());
    }

    /**
     * Execute method for checking performance of query
     * @param requestUid - requestUid
     * @param db - database
     * @param reportUid - reportUid
     * @param query - SQL query
     * @return Report entity
     */
    private Report executeCheckingPerformance(long requestUid, DatabaseEnum db, String reportUid, String query) {
        try {
            long startDate = System.currentTimeMillis();

            long start = System.nanoTime();
            // Execute performance
            List<Map<String, Object>> list = multiDatabaseDAO.queryForList(db, query);
            long end = System.nanoTime();

            long endDate = System.currentTimeMillis();

            log.info("[{}][checkPerformance] Query {} executed successfully. Db: {}", requestUid, query, db.name().toUpperCase());

            long queryTime = end - start;

            return getSuccessReportData(db, reportUid, queryTime, startDate, endDate, Utils.getResultMsg(list.size()));
        } catch (DataAccessException e) {
            // if query can't execute than return Report with error message
            log.warn("[WARNING][{}][checkPerformance] Query {} can't be executed. Only 'select' performance. Db: {}. Msg: {}"
                    , requestUid, query, db.name().toUpperCase(), e.getMessage());

            return getErrorReportData(db, reportUid, Utils.toJson(e));
        }
    }

    private void deletePerformanceQueue(long requestUid, String reportUid) throws RuntimeException, SchedulerException {
        log.info("[{}][deletePerformanceQueue] Start to remove job of report {}.", requestUid, reportUid);

        // delete queue data from DB
        performanceQueueDAO.deleteData(reportUid);

        // delete Performance job
        JobKey jobKey = JobKey.jobKey(reportUid, Utils.SCHEDULER_JOB_GROUP);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

        log.info("[{}][deletePerformanceQueue] Performance job key {} to delete", requestUid, jobKey);

        if (jobDetail != null) {

            scheduler.deleteJob(jobKey);

            log.info("[{}][deletePerformanceQueue] Performance job {} successfully deleted", requestUid, jobKey);

        }

        log.info("[{}][deletePerformanceQueue] Finish to remove queue and job of report {}.", requestUid, reportUid);
    }

    private Report getNewReportData(DatabaseEnum db, String reportUid, String query) {
        return Report.builder()
                .reportUid(reportUid)
                .state(ReportState.in_progress)
                .databaseName(db)
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .query(query)
                .build();
    }

    private  Report getSuccessReportData(DatabaseEnum db, String reportUid, long queryTime
            , long startDate, long endDate, String resMsg) {

        return Report.builder()
                .state(ReportState.success)
                .timeInNanos(queryTime)
                .startDate(new Timestamp(startDate))
                .endDate(new Timestamp(endDate))
                .resMsg(resMsg)
                .reportUid(reportUid)
                .databaseName(db)
                .build();
    }

    private  Report getErrorReportData(DatabaseEnum db, String reportUid, String errMsg) {
        return Report.builder()
                .state(ReportState.failed)
                .resMsg(errMsg)
                .reportUid(reportUid)
                .databaseName(db)
                .build();
    }

    private PerformanceQueue getNewPerformanceQueueData(String reportUid, String query) {
        return PerformanceQueue.builder()
                .reportUid(reportUid)
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .query(query)
                .build();
    }

    private JobDetail buildJobDetail(long requestUid, String reportUid, String query) {

        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("requestUid", requestUid);
        jobDataMap.put("reportUid", reportUid);
        jobDataMap.put("query", query);

        return newJob(PerformanceSchedulerJob.class)
                .withIdentity(reportUid, Utils.SCHEDULER_JOB_GROUP)
                .withDescription(Utils.SCHEDULER_JOB_DESCRIPTION)
                .usingJobData(jobDataMap)
                .storeDurably(true)
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail) {
        SimpleScheduleBuilder scheduleBuilder = simpleSchedule()
                .withIntervalInMinutes(intervalInMinutes)
                .withRepeatCount(repeatCount)
                .withMisfireHandlingInstructionNextWithExistingCount();

        return newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), Utils.SCHEDULER_TRIGGER_GROUP)
                .withDescription(Utils.SCHEDULER_TRIGGER_DESCRIPTION)
                .startAt(new Date())
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Autowired
    public void setMultiDatabaseDAO(MultiDatabaseDAO multiDatabaseDAO) {
        this.multiDatabaseDAO = multiDatabaseDAO;
    }

    @Autowired
    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    @Autowired
    public void setPerformanceQueueDAO(PerformanceQueueDAO performanceQueueDAO) {
        this.performanceQueueDAO = performanceQueueDAO;
    }

    @Autowired
    public void setScheduler(@Qualifier("schedulerFactory") Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Autowired
    public void setPerformanceService(IPerformanceService performanceService) {
        this.performanceService = performanceService;
    }
}
