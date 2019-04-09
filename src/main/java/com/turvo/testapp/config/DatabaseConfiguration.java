package com.turvo.testapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by maxim.babrovich on 29.03.2019.
 */

@Configuration
@ComponentScan
public class DatabaseConfiguration {

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "turvoHikariConfig")
    @ConfigurationProperties(prefix = "turvo.datasource.hikari")
    public HikariConfig turvoHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "turvo2HikariConfig")
    @ConfigurationProperties(prefix = "turvo2.datasource.hikari")
    public HikariConfig turvo2HikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "turvo3HikariConfig")
    @ConfigurationProperties(prefix = "turvo3.datasource.hikari")
    public HikariConfig turvo3HikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "turvoDataSource")
    public DataSource turvoDataSource() {
        return new HikariDataSource(turvoHikariConfig());
    }

    @Bean(name = "turvo2DataSource")
    public DataSource turvo2DataSource() {
        return new HikariDataSource(turvo2HikariConfig());
    }

    @Bean(name = "turvo3DataSource")
    public DataSource turvo3DataSource() {
        return new HikariDataSource(turvo3HikariConfig());
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean(name = "namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    @Bean(name = "turvoJdbcTemplate")
    public JdbcTemplate turvoJdbcTemplate() {
        return new JdbcTemplate(turvoDataSource());
    }

    @Bean(name = "turvo2JdbcTemplate")
    public JdbcTemplate turvo2JdbcTemplate() {
        return new JdbcTemplate(turvo2DataSource());
    }

    @Bean(name = "turvo3JdbcTemplate")
    public JdbcTemplate turvo3JdbcTemplate() {
        return new JdbcTemplate(turvo3DataSource());
    }
}
