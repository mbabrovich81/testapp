package com.itrexgroup.turvo.testapp.dao;

import com.itrexgroup.turvo.testapp.model.report.PerformanceQueueResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */

@Repository
@Slf4j
public class PerformanceQueueDAOImpl implements PerformanceQueueDAO {

    private static final String INSERT_TABLE_TEST_PERFORMANCE_QUEUE = ""
            + "INSERT INTO tbl_test_performance_queue "
            + "            (report_uid, "
            + "             created_date, "
            + "             query) "
            + "VALUES      (?, ?, ?)";

    private static final String UPDATE_TABLE_TEST_PERFORMANCE_QUEUE = ""
            + "UPDATE tbl_test_performance_queue "
            + "SET    attempts_num = attempts_num + 1 "
            + "WHERE  report_uid = ? ";

    private static final String DELETE_TABLE_TEST_PERFORMANCE_QUEUE = ""
            + "DELETE FROM tbl_test_performance_queue "
            + "WHERE  report_uid = ? ";

    private static final String DELETE_ALL = ""
            + "DELETE FROM tbl_test_performance_queue";

    private static final String SELECT_BY_REPORT_UID = ""
            + "SELECT report_uid, attempts_num, created_date, query "
            + "FROM   tbl_test_performance_queue "
            + "WHERE  report_uid = :reportUid";

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void insertData(Object[] values) throws Exception {
        try {
            jdbcTemplate.update(INSERT_TABLE_TEST_PERFORMANCE_QUEUE, values);
        } catch (DataAccessException e) {
            log.error("[ERROR][PerformanceQueueDAO][insertData] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public void updateData(Object[] values) throws Exception {
        try {
            jdbcTemplate.update(UPDATE_TABLE_TEST_PERFORMANCE_QUEUE, values);
        } catch (DataAccessException e) {
            log.error("[ERROR][PerformanceQueueDAO][updateData] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public void deleteData(Object[] values) throws Exception {
        try {
            jdbcTemplate.update(DELETE_TABLE_TEST_PERFORMANCE_QUEUE, values);
        } catch (DataAccessException e) {
            log.error("[ERROR][PerformanceQueueDAO][deleteData] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public void deleteAll() throws Exception {
        try {
            jdbcTemplate.update(DELETE_ALL);
        } catch (DataAccessException e) {
            log.error("[ERROR][PerformanceQueueDAO][deleteAll] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public PerformanceQueueResult findByReportUid(String reportUid) {
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("reportUid", reportUid);
        }};

        List<PerformanceQueueResult> result = namedParameterJdbcTemplate.query(SELECT_BY_REPORT_UID, paramMap, (rs, i) -> {
            PerformanceQueueResult pqr = new PerformanceQueueResult();

            pqr.setReportUid(rs.getString("report_uid"));

            pqr.setAttemptsNum(rs.getInt("attempts_num"));

            pqr.setCreatedDate(rs.getTimestamp("created_date"));

            String queryVal = rs.getString("query");
            if (!rs.wasNull()) {
                pqr.setQuery(queryVal);
            }

            return pqr;
        });

        return !CollectionUtils.isEmpty(result) ? result.get(0) : null;
    }

    @Autowired
    @Qualifier("jdbcTemplate")
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    @Qualifier("namedParameterJdbcTemplate")
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
}
