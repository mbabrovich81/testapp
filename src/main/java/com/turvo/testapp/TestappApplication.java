package com.turvo.testapp;

import com.turvo.testapp.model.DatabaseEnum;
import com.turvo.testapp.service.report.IReportService;
import com.turvo.testapp.service.table.ICreateTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
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
            // remove all scheduler (quartz) jobs
            reportService.removeAllSchedulerJobs();
        }

        if (preloadDatabase) {
            // preloading for databases
            // drop and create tbl_test_task and tbl_test_performance_queue with their sequences
            createTableService.createReportTables();

            // drop and create tbl_test_task tables with their sequences
            // fill tbl_test_task by default values
            for (DatabaseEnum db: DatabaseEnum.values()) {
                createTableService.createAndFillTable(db);
            }
        } else {
            // report with state in_progress to failed state
            reportService.setFailedForInProgressReports();
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
