package com.itrexgroup.turvo.testapp.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */

@Repository
@Slf4j
public class CreateReportDAOImpl implements CreateReportDAO {

    private static final String DROP_TABLE_TEST_PERFORMANCE_REPORT = "DROP TABLE IF EXISTS tbl_test_performance_report";
    private static final String DROP_SEQUENCE_SEC_TEST_PERFORMANCE_REPORT  = "DROP SEQUENCE IF EXISTS seq_tbl_test_performance_report_id";
    private static final String CREATE_SEQUENCE_SEC_TEST_PERFORMANCE_REPORT  = "CREATE SEQUENCE seq_tbl_test_performance_report_id INCREMENT BY 1 START WITH 1 CACHE 100 NO CYCLE";
    private static final String CREATE_TABLE_TEST_PERFORMANCE_REPORT = ""
            + "CREATE TABLE tbl_test_performance_report "
            + "  ( "
            + "     id   BIGINT NOT NULL DEFAULT nextval('seq_tbl_test_performance_report_id'), "
            + "     report_uid VARCHAR(64), "
            + "     state VARCHAR(64), "
            + "     database_name VARCHAR(125), "
            + "     time_in_nanos BIGINT, "
            + "     start_date TIMESTAMP WITHOUT TIME ZONE, "
            + "     end_date TIMESTAMP WITHOUT TIME ZONE, "
            + "     created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL, "
            + "     query TEXT, "
            + "     res_msg TEXT "
            + "  )";

    private static final String DROP_TABLE_TEST_PERFORMANCE_QUEUE = "DROP TABLE IF EXISTS tbl_test_performance_queue";
    private static final String DROP_SEQUENCE_SEC_TEST_PERFORMANCE_QUEUE  = "DROP SEQUENCE IF EXISTS seq_tbl_test_performance_queue_id";
    private static final String CREATE_SEQUENCE_SEC_TEST_PERFORMANCE_QUEUE  = "CREATE SEQUENCE seq_tbl_test_performance_queue_id INCREMENT BY 1 START WITH 1 CACHE 100 NO CYCLE";
    private static final String CREATE_TABLE_TEST_PERFORMANCE_QUEUE = ""
            + "CREATE TABLE tbl_test_performance_queue "
            + "  ( "
            + "     id   BIGINT NOT NULL DEFAULT nextval('seq_tbl_test_performance_queue_id'), "
            + "     report_uid VARCHAR(64), "
            + "     attempts_num INT DEFAULT 0, "
            + "     created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL, "
            + "     query TEXT "
            + "  )";

    private JdbcTemplate jdbcTemplate;

    @Override
    public void createTableWithDropping() throws Exception {

        try {
            // DROP table tbl_test_performance_queue if exists
            jdbcTemplate.execute(DROP_TABLE_TEST_PERFORMANCE_REPORT);
            // DROP sequence seq_tbl_test_performance_report_id if exists
            jdbcTemplate.execute(DROP_SEQUENCE_SEC_TEST_PERFORMANCE_REPORT);
            // CREATE table tbl_test_performance_queue
            jdbcTemplate.execute(CREATE_SEQUENCE_SEC_TEST_PERFORMANCE_REPORT);
            // CREATE sequence seq_tbl_test_performance_report_id
            jdbcTemplate.execute(CREATE_TABLE_TEST_PERFORMANCE_REPORT);

            // DROP table tbl_test_performance_report if exists
            jdbcTemplate.execute(DROP_TABLE_TEST_PERFORMANCE_QUEUE);
            // DROP sequence seq_tbl_test_performance_queue_id if exists
            jdbcTemplate.execute(DROP_SEQUENCE_SEC_TEST_PERFORMANCE_QUEUE);
            // CREATE table tbl_test_performance_report
            jdbcTemplate.execute(CREATE_SEQUENCE_SEC_TEST_PERFORMANCE_QUEUE);
            // CREATE sequence seq_tbl_test_performance_queue_id
            jdbcTemplate.execute(CREATE_TABLE_TEST_PERFORMANCE_QUEUE);
        } catch (DataAccessException e) {
            log.error("[ERROR][CreateReportDAO][createTableWithDropping] ", e);
            throw new Exception(e);
        }
    }

    @Autowired
    @Qualifier("jdbcTemplate")
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
