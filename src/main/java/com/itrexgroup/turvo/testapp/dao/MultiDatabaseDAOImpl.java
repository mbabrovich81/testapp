package com.itrexgroup.turvo.testapp.dao;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */

@Repository
@Slf4j
public class MultiDatabaseDAOImpl implements MultiDatabaseDAO {

    private static final String DROP_TABLE_TEST_TASK = "DROP TABLE IF EXISTS tbl_test_task";
    private static final String DROP_SEQUENCE_SEC_TEST_TASK  = "DROP SEQUENCE IF EXISTS seq_tbl_test_task_id";
    private static final String CREATE_SEQUENCE_SEC_TEST_TASK  = "CREATE SEQUENCE seq_tbl_test_task_id INCREMENT BY 1 START WITH 1 CACHE 100 NO CYCLE";
    private static final String CREATE_TABLE_TEST_TASK = ""
            + "CREATE TABLE tbl_test_task "
            + "  ( "
            + "     id   BIGINT NOT NULL DEFAULT nextval('seq_tbl_test_task_id'), "
            + "     name VARCHAR(255) "
            + "  )";
    private static final String INSERT_TABLE_TEST_TASK= "INSERT INTO tbl_test_task(name) VALUES (?)";

    private JdbcTemplate turvoJdbcTemplate;

    private JdbcTemplate turvo2JdbcTemplate;

    private JdbcTemplate turvo3JdbcTemplate;

    @Value("${turvo.jdbc.query.timeout}")
    private int queryTimeout;

    @Override
    public void createTableWithDropping(DatabaseEnum db) throws Exception {

        try {
            // get JdbcTemplate for specific database
            JdbcTemplate jdbcTemplate = getJdbcTemplate(db);

            if (jdbcTemplate != null) {
                // DROP table tbl_test_task if exists
                jdbcTemplate.execute(DROP_TABLE_TEST_TASK);
                // DROP sequence seq_tbl_test_task_id if exists
                jdbcTemplate.execute(DROP_SEQUENCE_SEC_TEST_TASK);
                // CREATE table tbl_test_task
                jdbcTemplate.execute(CREATE_SEQUENCE_SEC_TEST_TASK);
                // CREATE sequence seq_tbl_test_task_id
                jdbcTemplate.execute(CREATE_TABLE_TEST_TASK);
            }
        } catch (DataAccessException e) {
            log.error("[ERROR][MultiDatabaseDAO][createTableWithDropping] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public void insertData(DatabaseEnum db, List<Object[]> values) throws Exception {

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(db);

            if (jdbcTemplate != null) {
                // set default value to query timeout
                jdbcTemplate.setQueryTimeout(-1);

                // Uses JdbcTemplate's batchUpdate operation to bulk load data
                jdbcTemplate.batchUpdate(INSERT_TABLE_TEST_TASK, values);
            }
        } catch (DataAccessException e) {
            log.error("[ERROR][MultiDatabaseDAO][insertData] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public List<Map<String, Object>> queryForList(DatabaseEnum db, String query) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(db);

        if (jdbcTemplate != null) {
            // set custom value to query timeout
            jdbcTemplate.setQueryTimeout(queryTimeout);

            return jdbcTemplate.queryForList(query);
        }

        return new ArrayList<>();
    }

    /**
     * Get specific JdbcTemplate by the DatabaseEnum
     * @param db - DatabaseEnum
     * @return JdbcTemplate
     */
    private JdbcTemplate getJdbcTemplate(DatabaseEnum db) {
        switch (db) {
            case turvo:
                return turvoJdbcTemplate;
            case turvo2:
                return turvo2JdbcTemplate;
            case turvo3:
                return turvo3JdbcTemplate;
        }

        return null;
    }

    @Autowired
    @Qualifier("turvoJdbcTemplate")
    public void setTurvoJdbcTemplate(JdbcTemplate turvoJdbcTemplate) {
        this.turvoJdbcTemplate = turvoJdbcTemplate;
    }

    @Autowired
    @Qualifier("turvo2JdbcTemplate")
    public void setTurvo2JdbcTemplate(JdbcTemplate turvo2JdbcTemplate) {
        this.turvo2JdbcTemplate = turvo2JdbcTemplate;
    }

    @Autowired
    @Qualifier("turvo3JdbcTemplate")
    public void setTurvo3JdbcTemplate(JdbcTemplate turvo3JdbcTemplate) {
        this.turvo3JdbcTemplate = turvo3JdbcTemplate;
    }
}
