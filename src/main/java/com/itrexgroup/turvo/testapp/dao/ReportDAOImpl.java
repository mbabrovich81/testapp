package com.itrexgroup.turvo.testapp.dao;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import com.itrexgroup.turvo.testapp.model.report.Report;
import com.itrexgroup.turvo.testapp.model.report.ReportResult;
import com.itrexgroup.turvo.testapp.model.report.ReportState;
import com.itrexgroup.turvo.testapp.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
            + "VALUES      (:reportUid, :state, :databaseName, :createdDate, :query)";

    private static final String UPDATE_TABLE_TEST_PERFORMANCE_REPORT = ""
            + "UPDATE tbl_test_performance_report "
            + "SET    state = :state, "
            + "       time_in_nanos = :timeInNanos, "
            + "       start_date = :startDate, "
            + "       end_date = :endDate, "
            + "       res_msg = :resMsg "
            + "WHERE  report_uid = :reportUid "
            + "       AND database_name = :databaseName";

    private static final String UPDATE_STATE = ""
            + "UPDATE tbl_test_performance_report "
            + "SET    state = :target "
            + "WHERE  state = :source";

    private static final String SELECT_COUNT_IN_PROGRESS = ""
            + "SELECT count(*) "
            + "FROM   tbl_test_performance_report "
            + "WHERE  state = :state";

    private static final String SELECT_BY_REPORT_UID = ""
            + "SELECT report_uid, state, database_name, time_in_nanos, start_date, end_date, created_date, query, res_msg "
            + "FROM   tbl_test_performance_report "
            + "WHERE  report_uid = :reportUid";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void insertData(Report report) throws RuntimeException {
        try {
            namedParameterJdbcTemplate.update(INSERT_TABLE_TEST_PERFORMANCE_REPORT, Utils.reportToMap(report));
        } catch (DataAccessException e) {
            log.error("[ERROR][ReportDAO][insertData] Insert data failed.");
            throw e;
        }
    }

    @Override
    public void updateData(Report report) throws RuntimeException {
        try {
            namedParameterJdbcTemplate.update(UPDATE_TABLE_TEST_PERFORMANCE_REPORT, Utils.reportToMap(report));
        } catch (DataAccessException e) {
            log.error("[ERROR][ReportDAO][updateData] Update data failed.");
            throw e;
        }
    }

    @Override
    public void updateState(ReportState target, ReportState source) throws RuntimeException {
        try {
            Map<String, Object> paramMap =  new HashMap<String, Object>() {{
                put("target", target.name());
                put("source", source.name());
            }};

            namedParameterJdbcTemplate.update(UPDATE_STATE, paramMap);
        } catch (DataAccessException e) {
            log.error("[ERROR][ReportDAO][updateState] Update state failed.");
            throw e;
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
    @Qualifier("namedParameterJdbcTemplate")
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
}
