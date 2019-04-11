package com.itrexgroup.turvo.testapp;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import com.itrexgroup.turvo.testapp.service.report.IReportService;
import com.itrexgroup.turvo.testapp.service.table.ICreateTableService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@Slf4j
public class TestappApplication implements CommandLineRunner {

    @Value("${turvo.preload.database}")
    private boolean preloadDatabase;

    @Value("${turvo.quartz.jobs.remove}")
    private boolean removeAllJobs;

    private ICreateTableService createTableService;

    private IReportService reportService;

    public static void main(String[] args) {
        SpringApplication.run(TestappApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

        if (removeAllJobs) {
            try {
                // remove all scheduler (quartz) jobs
                reportService.removeAllSchedulerJobs();
            } catch (RuntimeException e) {
                log.error("[ERROR][removeAllSchedulerJobs] Remove queues of reports was failed.", e);
            } catch (SchedulerException e) {
                log.error("[ERROR][removeAllSchedulerJobs] Delete all quartz jobs of reports was failed.", e);
            }
        }

        if (preloadDatabase) {
            try {
                // preloading for databases
                // drop and create tbl_test_task and tbl_test_performance_queue with their sequences
                createTableService.createReportTables();

                // drop and create tbl_test_task tables with their sequences
                // fill tbl_test_task by default values
                for (DatabaseEnum db: DatabaseEnum.values()) {
                    createTableService.createAndFillTable(db);
                }
            } catch (RuntimeException e) {
                log.error("[ERROR][createAndFillTable] Create and fill tables was failed.", e);
            }
        } else {
            try {
                // report with state in_progress to failed state
                reportService.setFailedForInProgressReports();
            } catch (Exception e) {
                log.error("[ERROR][setFailedForInProgressReports] Set 'failed' for ' in_progress' reports was failed.", e);
            }
        }
    }

    @Autowired
    public void setCreateTableService(ICreateTableService createTableService) {
        this.createTableService = createTableService;
    }

    @Autowired
    public void setReportService(IReportService reportService) {
        this.reportService = reportService;
    }
}
