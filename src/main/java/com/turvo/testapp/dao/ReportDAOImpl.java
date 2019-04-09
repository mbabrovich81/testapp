package com.turvo.testapp.dao;

import com.turvo.testapp.model.DatabaseEnum;
import com.turvo.testapp.model.report.ReportResult;
import com.turvo.testapp.model.report.ReportState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */

@Repository
@Slf4j
public class ReportDAOImpl implements ReportDAO {

    private static final String INSERT_TABLE_TEST_PERFORMANCE_REPORT = ""
            + "INSERT INTO tbl_test_performance_report "
            + "            (report_uid, "
            + "             state, "
            + "             database_name, "
            + "             created_date, "
            + "             query) "
            + "VALUES      (?, ?, ?, ?, ?)";

    private static final String UPDATE_TABLE_TEST_PERFORMANCE_REPORT = ""
            + "UPDATE tbl_test_performance_report "
            + "SET    state = ?, "
            + "       time_in_nanos = ?, "
            + "       start_date = ?, "
            + "       end_date = ?, "
            + "       res_msg = ? "
            + "WHERE  report_uid = ? "
            + "       AND database_name = ?";

    private static final String UPDATE_STATE = ""
            + "UPDATE tbl_test_performance_report "
            + "SET    state = ? "
            + "WHERE  state = ?";

    private static final String SELECT_COUNT_IN_PROGRESS = ""
            + "SELECT count(*) "
            + "FROM   tbl_test_performance_report "
            + "WHERE  state = :state";

    private static final String SELECT_BY_REPORT_UID = ""
            + "SELECT report_uid, state, database_name, time_in_nanos, start_date, end_date, created_date, query, res_msg "
            + "FROM   tbl_test_performance_report "
            + "WHERE  report_uid = :reportUid";

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void insertData(Object[] values) throws Exception {
        try {
            jdbcTemplate.update(INSERT_TABLE_TEST_PERFORMANCE_REPORT, values);
        } catch (DataAccessException e) {
            log.error("[ERROR][ReportDAO][insertData] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public void updateData(Object[] values) throws Exception {
        try {
            jdbcTemplate.update(UPDATE_TABLE_TEST_PERFORMANCE_REPORT, values);
        } catch (DataAccessException e) {
            log.error("[ERROR][ReportDAO][updateData] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public void updateState(Object[] values) throws Exception {
        try {
            jdbcTemplate.update(UPDATE_STATE, values);
        } catch (DataAccessException e) {
            log.error("[ERROR][ReportDAO][updateState] ", e);
            throw new Exception(e);
        }
    }

    @Override
    public Optional<Long> countInProgress() {
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
           put("state", ReportState.in_progress.name());
        }};

        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(SELECT_COUNT_IN_PROGRESS, paramMap, Long.class));
    }

    @Override
    public List<ReportResult> findByReportUid(String reportUid) {
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("reportUid", reportUid);
        }};

        List<ReportResult> result = namedParameterJdbcTemplate.query(SELECT_BY_REPORT_UID, paramMap, (rs, i) -> {
            ReportResult rr = new ReportResult();

            rr.setReportUid(rs.getString("report_uid"));

            String stateVal = rs.getString("state");
            if (!rs.wasNull()) {
                rr.setState(ReportState.valueOf(stateVal));
            }

            String databaseVal = rs.getString("database_name");
            if (!rs.wasNull()) {
                rr.setDatabase(DatabaseEnum.valueOf(databaseVal));
            }

            long timeVal = rs.getLong("time_in_nanos");
            if (!rs.wasNull()) {
                rr.setRequestTime(timeVal / 1000);
            }

            Timestamp startVal = rs.getTimestamp("start_date");
            if (!rs.wasNull()) {
                rr.setRequestStart(startVal);
            }

            Timestamp endVal = rs.getTimestamp("end_date");
            if (!rs.wasNull()) {
                rr.setRequestEnd(endVal);
            }

            rr.setCreatedDate(rs.getTimestamp("created_date"));

            String queryVal = rs.getString("query");
            if (!rs.wasNull()) {
                rr.setQuery(queryVal);
            }

            String resVal = rs.getString("res_msg");
            if (!rs.wasNull()) {
                rr.setResMsg(resVal);
            }
            return rr;
        });

        return result == null ? new ArrayList<>() : result;
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
