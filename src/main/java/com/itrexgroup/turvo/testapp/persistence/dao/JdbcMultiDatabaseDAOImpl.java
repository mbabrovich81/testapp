package com.itrexgroup.turvo.testapp.persistence.dao;

import com.itrexgroup.turvo.testapp.model.DatabaseEnum;
import com.zaxxer.hikari.HikariConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;

/**
 * Created by maxim.babrovich on 02.04.2019.
 */

@Repository
@Slf4j
public class JdbcMultiDatabaseDAOImpl implements JdbcMultiDatabaseDAO {

    private HikariConfig turvoHikariConfig;

    private HikariConfig turvo2HikariConfig;

    private HikariConfig turvo3HikariConfig;

    @Value("${turvo.jdbc.query.timeout}")
    private int queryTimeout;

    @Override
    public int queryForList(DatabaseEnum db, String query) {
//        JdbcTemplate jdbcTemplate = getJdbcTemplate(db);
//
//        if (jdbcTemplate != null) {
//            // set custom value to query timeout
//            jdbcTemplate.setQueryTimeout(queryTimeout);
//
//            jdbcTemplate.getDataSource().getConnection()
//            return jdbcTemplate.queryForList(query);
//        }
//
//        return new ArrayList<>();
        int size =0;

        HikariConfig config = getHikariConfig(db);

        if (config != null) {

            try {
                log.info("Registering JDBC driver...{}", db.name());

                Class.forName(config.getDriverClassName());

                log.info("Creating database connection...{}", db.name());
                Connection connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());

                DatabaseMetaData dbMd = connection.getMetaData();
                if (dbMd.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)) {
                    log.info("Transaction Isolation level TRANSACTION_SERIALIZABLE is supported. {}", db.name());
                    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                }

                log.info("Executing statement...{}", db.name());
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setQueryTimeout(queryTimeout);

                ResultSet resultSet = statement.executeQuery();

                log.info("Retrieving data from database...{}", db.name());
                while(resultSet.next()) {
                    size++;
                }
                log.info("Retrieved size {} data from database...{}", size, db.name());

                log.info("Closing connection and releasing resources...{}", db.name());
                statement.close();
                connection.close();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return size;
    }

    /**
     * Get specific JdbcTemplate by the DatabaseEnum
     * @param db - DatabaseEnum
     * @return JdbcTemplate
     */
    private HikariConfig getHikariConfig(DatabaseEnum db) {
        switch (db) {
            case turvo:
                return turvoHikariConfig;
            case turvo2:
                return turvo2HikariConfig;
            case turvo3:
                return turvo3HikariConfig;
        }

        return null;
    }

    @Autowired
    @Qualifier("turvoHikariConfig")
    public void setTurvoHikariConfig(HikariConfig turvoHikariConfig) {
        this.turvoHikariConfig = turvoHikariConfig;
    }

    @Autowired
    @Qualifier("turvo2HikariConfig")
    public void setTurvo2HikariConfig(HikariConfig turvo2HikariConfig) {
        this.turvo2HikariConfig = turvo2HikariConfig;
    }

    @Autowired
    @Qualifier("turvo3HikariConfig")
    public void setTurvo3HikariConfig(HikariConfig turvo3HikariConfig) {
        this.turvo3HikariConfig = turvo3HikariConfig;
    }

}
